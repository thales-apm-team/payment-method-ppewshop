package com.payline.payment.ppewshop.service.impl;

import com.payline.payment.ppewshop.bean.common.*;
import com.payline.payment.ppewshop.bean.configuration.RequestConfiguration;
import com.payline.payment.ppewshop.bean.request.InitDossierRequest;
import com.payline.payment.ppewshop.bean.response.InitDossierResponse;
import com.payline.payment.ppewshop.exception.InvalidDataException;
import com.payline.payment.ppewshop.exception.PluginException;
import com.payline.payment.ppewshop.utils.Constants;
import com.payline.payment.ppewshop.utils.PluginUtils;
import com.payline.payment.ppewshop.utils.http.HttpClient;
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
    private HttpClient client = HttpClient.getInstance();

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
            InitDossierResponse initDossierResponse = client.initDossier(configuration, initDossierRequest);

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

        Buyer.Address address = buyer.getAddressForType(Buyer.AddressType.BILLING);
        if (address == null) {
            throw new InvalidDataException("paymentRequest.buyer.address(BILLING) is required");
        }

        Date birthDate = buyer.getBirthday();
        if (birthDate == null) {
            throw new InvalidDataException("paymentRequest.buyer.birthDate is required");
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
                .withGoodsCode(getGoodsCode(getMainCat(request.getOrder().getItems())))
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
        if (items== null || items.isEmpty()){
            throw new InvalidDataException("paymentRequest.order.items is required");
        }

        Map<String, BigInteger> categories = new HashMap<>();

        for (Order.OrderItem item : items) {
            // get category and amount values
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

    /**
     * Return a PPEW category mapped from Payline category
     *
     * @param cat Payline category to convert
     * @return
     */
    public static String getGoodsCode(String cat) {
        if (cat == null) return null;

        switch (cat) {
            case "1":
                return "625";
            case "100010001":
                return "625";
            case "100010002":
                return "626";
            case "100010003":
                return "626";
            case "2":
                return "610";
            case "20001":
                return "611";
            case "200010001":
                return "624";
            case "200010002":
                return "611";
            case "200010003":
                return "615";
            case "200010004":
                return "623";
            case "200010005":
                return "623";
            case "200010006":
                return "622";
            case "200010007":
                return "613";
            case "3":
                return "621";
            case "4":
                return "320";
            case "40001":
                return "322";
            case "400010001":
                return "326";
            case "400010002":
                return "327";
            case "40002":
                return "320";
            case "400020001":
                return "323";
            case "400020002":
                return "324";
            case "400020003":
                return "329";
            case "40003":
                return "328";
            case "400030001":
                return "328";
            case "400030002":
                return "328";
            case "5":
                return "663";
            case "50001":
                return "913";
            case "500010001":
                return "733";
            case "50002":
                return "913";
            case "50003":
                return "663";
            case "500030001":
                return "737";
            case "50004":
                return "663";
            case "500040001":
                return "738";
            case "500040002":
                return "739";
            case "500040003":
                return "740";
            case "500040004":
                return "741";
            case "599990001":
                return "941";
            case "599990002":
                return "912";
            case "6":
                return "640";
            case "7":
                return "660";
            case "8":
                return "730";
            case "9":
                return "660";
            case "10":
                return "660";
            case "11":
                return "660";
            case "110001":
                return "742";
            case "12":
                return "330";
            case "120001":
                return "331";
            case "1200010001":
                return "334";
            case "1200010002":
                return "336";
            case "1200010003":
                return "337";
            case "1200010004":
                return "337";
            case "120002":
                return "332";
            case "120003":
                return "339";
            case "1200030001":
                return "338";
            case "120004":
                return "341";
            case "120005":
                return "340";
            case "120006":
                return "343";
            case "120007":
                return "342";
            case "120008":
                return "330";
            case "1200080001":
                return "660";
            case "13":
                return "000";
            case "14":
                return "660";
            case "15":
                return "660";
            case "16":
                return "000";
            case "17":
                return "000";
            case "170001":
                return "000";
            case "170002":
                return "000";
            case "18":
                return "660";
            case "19":
                return "660";
            case "20":
                return "631";
            case "21":
                return "000";
            case "22":
                return "000";
            case "23":
                return "000";
            case "24":
                return "858";
            case "240001":
                return "858";
            case "2400010001":
                return "855";
            case "2400010002":
                return "857";
            case "25":
                return "650";
            case "26":
                return "620";
            default:
                return "000";

        }
    }

}
