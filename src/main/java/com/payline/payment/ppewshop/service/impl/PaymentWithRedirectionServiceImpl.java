package com.payline.payment.ppewshop.service.impl;

import com.payline.payment.ppewshop.bean.common.CheckStatusOut;
import com.payline.payment.ppewshop.bean.common.MerchantInformation;
import com.payline.payment.ppewshop.bean.configuration.RequestConfiguration;
import com.payline.payment.ppewshop.bean.request.CheckStatusRequest;
import com.payline.payment.ppewshop.bean.response.CheckStatusResponse;
import com.payline.payment.ppewshop.exception.PluginException;
import com.payline.payment.ppewshop.utils.Constants;
import com.payline.payment.ppewshop.utils.http.HttpClient;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.common.OnHoldCause;
import com.payline.pmapi.bean.payment.request.RedirectionPaymentRequest;
import com.payline.pmapi.bean.payment.request.TransactionStatusRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.buyerpaymentidentifier.impl.Email;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseOnHold;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseRedirect;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseSuccess;
import com.payline.pmapi.logger.LogManager;
import com.payline.pmapi.service.PaymentWithRedirectionService;
import org.apache.logging.log4j.Logger;

import java.net.MalformedURLException;
import java.net.URL;

public class PaymentWithRedirectionServiceImpl implements PaymentWithRedirectionService {
    private static final Logger LOGGER = LogManager.getLogger(PaymentServiceImpl.class);

    private HttpClient client = HttpClient.getInstance();

    @Override
    public PaymentResponse finalizeRedirectionPayment(RedirectionPaymentRequest request) {
        RequestConfiguration configuration = new RequestConfiguration(
                request.getContractConfiguration()
                , request.getEnvironment()
                , request.getPartnerConfiguration()
        );

        String partnerTransactionId = request.getRequestContext().getRequestData().get(Constants.RequestContextKeys.PARTNER_TRANSACTION_ID);
        String buyerPaymentId = request.getBuyer().getEmail();
        return retrieveTransactionStatus(configuration, partnerTransactionId, buyerPaymentId);
    }

    @Override
    public PaymentResponse handleSessionExpired(TransactionStatusRequest request) {
        RequestConfiguration configuration = new RequestConfiguration(
                request.getContractConfiguration()
                , request.getEnvironment()
                , request.getPartnerConfiguration()
        );

        return retrieveTransactionStatus(configuration, request.getTransactionId(), request.getBuyer().getEmail());
    }

    private PaymentResponse retrieveTransactionStatus(RequestConfiguration configuration, String transactionId, String email) {
        PaymentResponse paymentResponse;
        try {
            CheckStatusRequest checkStatusRequest = createCheckStatusRequest(configuration, transactionId);
            CheckStatusResponse checkStatusResponse = client.checkStatus(configuration, checkStatusRequest);

            String partnerTransactionId = checkStatusResponse.getCheckStatusOut().getTransactionId();
            String statusCode = checkStatusResponse.getCheckStatusOut().getStatusCode();

            switch (statusCode) {
                case CheckStatusOut.StatusCode.A:
                    paymentResponse = createPaymentResponseSuccess(partnerTransactionId
                            , statusCode
                            , email
                            , checkStatusResponse.getCheckStatusOut().getCreditAuthorizationNumber());
                    break;
                case CheckStatusOut.StatusCode.E:
                    paymentResponse = createPaymentResponseOnHold(partnerTransactionId, statusCode);
                    break;
                case CheckStatusOut.StatusCode.I:
                    paymentResponse = createResponseRedirect(partnerTransactionId
                            , statusCode
                            , checkStatusResponse.getCheckStatusOut().getRedirectionURL());
                    break;
                case CheckStatusOut.StatusCode.C:
                    paymentResponse = createPaymentResponseFailure(partnerTransactionId, statusCode, FailureCause.CANCEL);
                    break;
                case CheckStatusOut.StatusCode.R:
                    paymentResponse = createPaymentResponseFailure(partnerTransactionId, statusCode, FailureCause.REFUSED);
                    break;
                default:
                    paymentResponse = createPaymentResponseFailure(partnerTransactionId, statusCode, FailureCause.INVALID_DATA);
                    break;
            }

        } catch (MalformedURLException e) {
            LOGGER.error("Invalid URL format", e);
            throw new PluginException(e.getMessage(), FailureCause.INVALID_DATA);
        } catch (PluginException e) {
            paymentResponse = e.toPaymentResponseFailureBuilder().build();
        } catch (RuntimeException e) {
            LOGGER.error("Unexpected plugin error", e);
            paymentResponse = PaymentResponseFailure.PaymentResponseFailureBuilder
                    .aPaymentResponseFailure()
                    .withErrorCode(PluginException.runtimeErrorCode(e))
                    .withFailureCause(FailureCause.INTERNAL_ERROR)
                    .build();
        }
        return paymentResponse;
    }

    private CheckStatusRequest createCheckStatusRequest(RequestConfiguration configuration, String transactionId) {
        MerchantInformation merchantInformation = MerchantInformation.Builder
                .aMerchantInformation()
                .withMerchantCode(configuration.getContractConfiguration().getProperty(Constants.ContractConfigurationKeys.MERCHANT_CODE).getValue())
                .withDistributorNumber(configuration.getContractConfiguration().getProperty(Constants.ContractConfigurationKeys.DISTRIBUTOR_NUMBER).getValue())
                .withCountryCode(configuration.getContractConfiguration().getProperty(Constants.ContractConfigurationKeys.COUNTRY_CODE).getValue())
                .build();

        CheckStatusRequest checkStatusRequest = new CheckStatusRequest();
        checkStatusRequest.getCheckStatusIn().setTransactionId(transactionId);
        checkStatusRequest.getCheckStatusIn().setMerchantInformation(merchantInformation);
        return checkStatusRequest;
    }

    private PaymentResponseSuccess createPaymentResponseSuccess(String partnerTransactionId, String statusCode, String email, String additionalData) {
        return PaymentResponseSuccess.PaymentResponseSuccessBuilder
                .aPaymentResponseSuccess()
                .withPartnerTransactionId(partnerTransactionId)
                .withTransactionDetails(Email.EmailBuilder.anEmail().withEmail(email).build())
                .withTransactionAdditionalData(additionalData)
                .withStatusCode(statusCode)
                .build();
    }

    private PaymentResponseOnHold createPaymentResponseOnHold(String partnerTransactionId, String statusCode) {
        return PaymentResponseOnHold.PaymentResponseOnHoldBuilder
                .aPaymentResponseOnHold()
                .withPartnerTransactionId(partnerTransactionId)
                .withOnHoldCause(OnHoldCause.ASYNC_RETRY)
                .withStatusCode(statusCode)
                .build();
    }

    private PaymentResponseRedirect createResponseRedirect(String partnerTransactionId, String statusCode, String url) throws MalformedURLException {
        PaymentResponseRedirect.RedirectionRequest redirectionRequest = PaymentResponseRedirect.RedirectionRequest.RedirectionRequestBuilder
                .aRedirectionRequest()
                .withUrl(new URL(url))
                .withRequestType(PaymentResponseRedirect.RedirectionRequest.RequestType.GET)
                .build();

        return PaymentResponseRedirect.PaymentResponseRedirectBuilder
                .aPaymentResponseRedirect()
                .withPartnerTransactionId(partnerTransactionId)
                .withStatusCode(statusCode)
                .withRedirectionRequest(redirectionRequest)
                .build();
    }

    private PaymentResponseFailure createPaymentResponseFailure(String partnerTransactionId, String statusCode, FailureCause cause) {
        return PaymentResponseFailure.PaymentResponseFailureBuilder
                .aPaymentResponseFailure()
                .withPartnerTransactionId(partnerTransactionId)
                .withErrorCode(statusCode)
                .withFailureCause(cause)
                .build();
    }
}
