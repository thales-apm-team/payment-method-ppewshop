package com.payline.payment.ppewshop.service.impl;

import com.payline.payment.ppewshop.bean.common.*;
import com.payline.payment.ppewshop.bean.configuration.RequestConfiguration;
import com.payline.payment.ppewshop.bean.request.InitDossierRequest;
import com.payline.payment.ppewshop.bean.response.InitDossierResponse;
import com.payline.payment.ppewshop.exception.InvalidDataException;
import com.payline.payment.ppewshop.exception.PluginException;
import com.payline.payment.ppewshop.service.HttpService;
import com.payline.payment.ppewshop.utils.Constants;
import com.payline.payment.ppewshop.utils.GoodsCodeBusiness;
import com.payline.payment.ppewshop.utils.PluginUtils;
import com.payline.pmapi.bean.common.Buyer;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.payment.Order;
import com.payline.pmapi.bean.payment.RequestContext;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseRedirect;
import com.payline.pmapi.logger.LogManager;
import com.payline.pmapi.service.PaymentService;
import org.apache.logging.log4j.Logger;

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class PaymentServiceImpl implements PaymentService {
    private static final Logger LOGGER = LogManager.getLogger(PaymentServiceImpl.class);
    private HttpService httpService = HttpService.getInstance();

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy");
    private static final int MERCHANT_ORDER_ID_LENGTH = 13;


    @Override
    public PaymentResponse paymentRequest(PaymentRequest request) {
        PaymentResponse response;
        try {
            final RequestConfiguration configuration = new RequestConfiguration(
                    request.getContractConfiguration()
                    , request.getEnvironment()
                    , request.getPartnerConfiguration()
            );

            InitDossierRequest initDossierRequest = createInitDossierFromPaymentRequest(request);
            InitDossierResponse initDossierResponse = httpService.initDossier(configuration, initDossierRequest);

            PaymentResponseRedirect.RedirectionRequest redirectionRequest = PaymentResponseRedirect.RedirectionRequest.RedirectionRequestBuilder
                    .aRedirectionRequest()
                    .withUrl(new URL(initDossierResponse.getInitDossierOut().getRedirectionUrl()))
                    .withRequestType(PaymentResponseRedirect.RedirectionRequest.RequestType.GET)
                    .build();

            Map<String, String> requestData = new HashMap<>();
            requestData.put(Constants.RequestContextKeys.PARTNER_TRANSACTION_ID, initDossierResponse.getInitDossierOut().getTransactionId());
            RequestContext context = RequestContext.RequestContextBuilder
                    .aRequestContext()
                    .withRequestData(requestData)
                    .build();

            response = PaymentResponseRedirect.PaymentResponseRedirectBuilder
                    .aPaymentResponseRedirect()
                    .withPartnerTransactionId(initDossierResponse.getInitDossierOut().getTransactionId())
                    .withRedirectionRequest(redirectionRequest)
                    .withRequestContext(context)
                    .build();

        } catch (MalformedURLException e) {
            String errorMessage = "Invalid URL format";
            LOGGER.error(errorMessage, e);
            response = PaymentResponseFailure.PaymentResponseFailureBuilder
                    .aPaymentResponseFailure()
                    .withErrorCode(PluginUtils.truncate(errorMessage, PluginException.ERROR_CODE_MAX_LENGTH))
                    .withFailureCause(FailureCause.INVALID_DATA)
                    .build();
        } catch (PluginException e) {
            response = e.toPaymentResponseFailureBuilder().build();
        } catch (RuntimeException e) {
            LOGGER.error("Unexpected plugin error", e);
            response = PaymentResponseFailure.PaymentResponseFailureBuilder
                    .aPaymentResponseFailure()
                    .withErrorCode(PluginException.runtimeErrorCode(e))
                    .withFailureCause(FailureCause.INTERNAL_ERROR)
                    .build();
        }

        return response;
    }


    InitDossierRequest createInitDossierFromPaymentRequest(PaymentRequest request) {
        MerchantInformation merchantInformation = MerchantInformation.Builder.aMerchantInformation()
                .withMerchantCode(request.getContractConfiguration().getProperty(Constants.ContractConfigurationKeys.MERCHANT_CODE).getValue())
                .withDistributorNumber(request.getContractConfiguration().getProperty(Constants.ContractConfigurationKeys.DISTRIBUTOR_NUMBER).getValue())
                .withCountryCode(request.getContractConfiguration().getProperty(Constants.ContractConfigurationKeys.COUNTRY_CODE).getValue())
                .build();

        MerchantConfiguration merchantConfiguration = MerchantConfiguration.Builder.aMerchantConfiguration()
                .withGuardBackUrl(request.getEnvironment().getNotificationURL())
                .withGuardPushUrl(request.getEnvironment().getRedirectionReturnURL())
                .build();

        Buyer buyer = request.getBuyer();

        if (buyer.getFullName() == null) {
            throw new InvalidDataException("paymentRequest.buyer.fullName is required");
        }

        if (buyer.getAddresses() == null) {
            throw new InvalidDataException(("paymentRequest.buyer.addresses is required"));
        }

        Buyer.Address address = buyer.getAddressForType(Buyer.AddressType.BILLING);
        if (address == null) {
            throw new InvalidDataException("paymentRequest.buyer.address(BILLING) is required");
        }

        Date birthDate = buyer.getBirthday();
        if (birthDate == null) {
            throw new InvalidDataException("paymentRequest.buyer.birthDate is required");
        }

        if (request.getLocale() == null) {
            throw new InvalidDataException("paymentRequest.locale is required");
        }

        CustomerInformation customerInformation = CustomerInformation.Builder.aCustomerInformation()
                .withCustomerLanguage(request.getLocale().getISO3Country())
                .withTitle(getCivilityFromPayline(buyer.getFullName().getCivility()))
                .withFirstName(buyer.getFullName().getFirstName())
                .withName(buyer.getFullName().getLastName())
                .withBirthDate(simpleDateFormat.format(birthDate))
                .withEmail(buyer.getEmail())
                .withAddressLine1(address.getStreet1())
                .withAddressLine2(address.getStreet2())
                .withCity(address.getCity())
                .withPostCode(address.getZipCode())
                .withCellPhoneNumber(buyer.getPhoneNumberForType(Buyer.PhoneNumberType.CELLULAR))
                .withPrivatePhoneNumber(buyer.getPhoneNumberForType(Buyer.PhoneNumberType.HOME))
                .withProfessionalPhoneNumber(buyer.getPhoneNumberForType(Buyer.PhoneNumberType.WORK))
                .build();

        OrderInformation orderInformation = OrderInformation.Builder.anOrderInformation()
                .withGoodsCode(GoodsCodeBusiness.getInstance().getGoodsCode(getMainCat(request.getOrder().getItems())))
                .withPrice(PluginUtils.createStringAmount(request.getAmount().getAmountInSmallestUnit(), request.getAmount().getCurrency()))
                .withFinancialProductType(OrderInformation.CLA)
                .build();

        MerchantOrderReference orderReference = MerchantOrderReference.Builder.aMerchantOrderReference()
                .withMerchantOrderId(PluginUtils.truncate(request.getTransactionId(), MERCHANT_ORDER_ID_LENGTH))
                .withMerchantRef(request.getOrder().getReference())
                .build();

        InitDossierIn dossierIn = InitDossierIn.Builder
                .anInitDossier()
                .withMerchantInformation(merchantInformation)
                .withMerchantConfiguration(merchantConfiguration)
                .withCustomerInformation(customerInformation)
                .withOrderInformation(orderInformation)
                .withMerchantOrderReference(orderReference)
                .build();

        return new InitDossierRequest(dossierIn);
    }

    /**
     * Get the main category of a list of items
     * The main category is the category having the maximum amount
     *
     * @param items list of Payline item
     * @return category having the maximum amount
     */
    String getMainCat(List<Order.OrderItem> items) {
        if (items == null || items.isEmpty()) {
            throw new InvalidDataException("paymentRequest.order.items is required");
        }

        Map<String, BigInteger> categories = new HashMap<>();

        for (Order.OrderItem item : items) {
            // get category and amount values
            if (item.getCategory() == null) {
                throw new InvalidDataException("item must contain a category");
            }

            if (item.getAmount() == null || item.getAmount().getAmountInSmallestUnit() == null) {
                throw new InvalidDataException("item must contain an amount");
            }

            if (item.getQuantity() == null) {
                throw new InvalidDataException("item must contain a quantity");
            }
            String cat = item.getCategory();
            BigInteger amount = item.getAmount().getAmountInSmallestUnit()
                    .multiply(BigInteger.valueOf(item.getQuantity()));

            // if category doesn't exists in the Map, create it
            categories.putIfAbsent(cat, BigInteger.ZERO);

            // add item amount to the total amount of this category
            categories.computeIfPresent(cat, (k, v) -> v.add(amount));
        }

        // return the category having the biggest total amount
        Map.Entry<String, BigInteger> maxEntry = Collections.max(categories.entrySet()
                , Comparator.comparing(Map.Entry::getValue));
        return maxEntry.getKey();
    }


    /**
     * Return a PPewShop civility mapped from Payline civility
     *
     * @param civility Payline civility to convert
     * @return PPew civility (MME, MLE or MR)
     */
    public static String getCivilityFromPayline(String civility) {
        if (civility == null) return null;

        switch (civility) {
            case "1":
            case "2":
            case "6":
                return "MME";
            case "3":
                return "MLE";
            case "4":
            case "5":
            case "7":
            case "8":
            case "9":
            case "10":
            case "11":
            case "12":
            default:
                return "MR";
        }
    }

}
