package com.payline.payment.ppewshop.service.impl;

import com.payline.payment.ppewshop.bean.common.CheckStatusOut;
import com.payline.payment.ppewshop.bean.response.CheckStatusResponse;
import com.payline.payment.ppewshop.exception.PluginException;
import com.payline.payment.ppewshop.utils.PluginUtils;
import com.payline.payment.ppewshop.utils.http.HttpClient;
import com.payline.pmapi.bean.common.*;
import com.payline.pmapi.bean.notification.request.NotificationRequest;
import com.payline.pmapi.bean.notification.response.NotificationResponse;
import com.payline.pmapi.bean.notification.response.impl.PaymentResponseByNotificationResponse;
import com.payline.pmapi.bean.notification.response.impl.TransactionStateChangedResponse;
import com.payline.pmapi.bean.payment.request.NotifyTransactionStatusRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.buyerpaymentidentifier.impl.EmptyTransactionDetails;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseOnHold;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseSuccess;
import com.payline.pmapi.logger.LogManager;
import com.payline.pmapi.service.NotificationService;
import org.apache.logging.log4j.Logger;

import java.util.Date;

public class NotificationServiceImpl implements NotificationService {
    private static final Logger LOGGER = LogManager.getLogger(PaymentServiceImpl.class);
    private HttpClient client = HttpClient.getInstance();

    @Override
    public NotificationResponse parse(NotificationRequest request) {
        NotificationResponse notificationResponse;

        final String transactionId = request.getTransactionId();
        NotificationResponseHandler notificationResponseHandler;
        if (transactionId == null) {
            // transaction does not exist yet in Payline -> PaymentResponseByNotificationResponse will be returned.
            notificationResponseHandler = new PaymentResponseByNotificationResponseHandler();
        } else {
            // transaction exists in Payline -> TransactionStateChangedResponse will be returned.
            notificationResponseHandler = new TransactionStateChangedResponseHandler();
        }

        TransactionStatus transactionStatus;
        String partnerTransactionId = "UNKNOWN";
        try {
            String xml = PluginUtils.inputStreamToString(request.getContent());
            CheckStatusResponse checkStatusResponse = CheckStatusResponse.fromXml(xml);
            partnerTransactionId = checkStatusResponse.getCheckStatusOut().getTransactionId();


            String statusCode = checkStatusResponse.getCheckStatusOut().getStatusCode();
            switch (statusCode) {
                case CheckStatusOut.StatusCode.A:
                    notificationResponse = notificationResponseHandler.successResponse(transactionId, checkStatusResponse);
                    break;
                case CheckStatusOut.StatusCode.E:
                    notificationResponse = notificationResponseHandler.onHoldResponse(transactionId, checkStatusResponse);
                    break;
                case CheckStatusOut.StatusCode.I:
                    notificationResponse = notificationResponseHandler.failureResponse(transactionId, checkStatusResponse, FailureCause.REFUSED);
                    break;
                case CheckStatusOut.StatusCode.C:
                    notificationResponse = notificationResponseHandler.failureResponse(transactionId, checkStatusResponse, FailureCause.CANCEL);
                    break;
                case CheckStatusOut.StatusCode.R:
                    notificationResponse = notificationResponseHandler.failureResponse(transactionId, checkStatusResponse, FailureCause.REFUSED);
                    break;
                default:
                    notificationResponse = notificationResponseHandler.failureResponse(transactionId, checkStatusResponse, FailureCause.INVALID_DATA);
                    break;
            }

        } catch (PluginException e) {
            notificationResponse = notificationResponseHandler.handlePluginException(transactionId, partnerTransactionId, e);

        } catch (RuntimeException e) {
            LOGGER.error("Unexpected plugin error", e);
            notificationResponse = notificationResponseHandler.handleRuntimeException(transactionId, partnerTransactionId, e);
        }

        return notificationResponse;
    }

    @Override
    public void notifyTransactionStatus(NotifyTransactionStatusRequest notifyTransactionStatusRequest) {
        // does nothing
    }


    /**
     * Define the methods of the classes which will build the instances of {@link NotificationResponse} to return.
     */
    private interface NotificationResponseHandler {
        NotificationResponse successResponse(String transactionId, CheckStatusResponse checkStatusResponse);

        NotificationResponse failureResponse(String transactionId, CheckStatusResponse checkStatusResponse, FailureCause failureCause);

        NotificationResponse onHoldResponse(String transactionId, CheckStatusResponse checkStatusResponse);

        NotificationResponse handlePluginException(String transactionId, String partnerTransactionId, PluginException e);

        NotificationResponse handleRuntimeException(String transactionId, String partnerTransactionId, RuntimeException e);
    }

    /**
     * Build {@link PaymentResponseByNotificationResponse} instances.
     */
    private static class PaymentResponseByNotificationResponseHandler implements NotificationResponseHandler {

        @Override
        public NotificationResponse successResponse(String transactionId, CheckStatusResponse checkStatusResponse) {
            PaymentResponse response = PaymentResponseSuccess.PaymentResponseSuccessBuilder
                    .aPaymentResponseSuccess()
                    .withPartnerTransactionId(checkStatusResponse.getCheckStatusOut().getTransactionId())
                    .withTransactionDetails(new EmptyTransactionDetails())
                    .withTransactionAdditionalData(checkStatusResponse.getCheckStatusOut().getCreditAuthorizationNumber())
                    .withStatusCode(checkStatusResponse.getCheckStatusOut().getStatusCode())
                    .build();

            return this.buildResponse(response, checkStatusResponse.getCheckStatusOut().getTransactionId());
        }

        @Override
        public NotificationResponse failureResponse(String transactionId, CheckStatusResponse checkStatusResponse, FailureCause failureCause) {
            PaymentResponse response = PaymentResponseFailure.PaymentResponseFailureBuilder
                    .aPaymentResponseFailure()
                    .withPartnerTransactionId(checkStatusResponse.getCheckStatusOut().getTransactionId())
                    .withErrorCode(checkStatusResponse.getCheckStatusOut().getStatusCode())
                    .withFailureCause(failureCause)
                    .build();

            return this.buildResponse(response, checkStatusResponse.getCheckStatusOut().getTransactionId());
        }

        @Override
        public NotificationResponse onHoldResponse(String transactionId, CheckStatusResponse checkStatusResponse) {
            PaymentResponse response = PaymentResponseOnHold.PaymentResponseOnHoldBuilder
                    .aPaymentResponseOnHold()
                    .withPartnerTransactionId(checkStatusResponse.getCheckStatusOut().getTransactionId())
                    .withOnHoldCause(OnHoldCause.ASYNC_RETRY)
                    .withStatusCode(checkStatusResponse.getCheckStatusOut().getStatusCode())
                    .build();

            return this.buildResponse(response, checkStatusResponse.getCheckStatusOut().getTransactionId());
        }

        @Override
        public NotificationResponse handlePluginException(String transactionId, String partnerTransactionId, PluginException e) {
            PaymentResponse response = PaymentResponseFailure.PaymentResponseFailureBuilder
                    .aPaymentResponseFailure()
                    .withPartnerTransactionId(partnerTransactionId)
                    .withErrorCode(e.getErrorCode())
                    .withFailureCause(e.getFailureCause())
                    .build();

            return this.buildResponse(response, partnerTransactionId);
        }

        @Override
        public NotificationResponse handleRuntimeException(String transactionId, String partnerTransactionId, RuntimeException e) {
            PaymentResponse response = PaymentResponseFailure.PaymentResponseFailureBuilder
                    .aPaymentResponseFailure()
                    .withPartnerTransactionId(partnerTransactionId)
                    .withErrorCode(PluginException.runtimeErrorCode(e))
                    .withFailureCause(FailureCause.INTERNAL_ERROR)
                    .build();

            return this.buildResponse(response, partnerTransactionId);
        }


        private PaymentResponseByNotificationResponse buildResponse(PaymentResponse paymentResponse, String partnerTransactionId) {
            return PaymentResponseByNotificationResponse.PaymentResponseByNotificationResponseBuilder.aPaymentResponseByNotificationResponseBuilder()
                    .withPaymentResponse(paymentResponse)
                    .withTransactionCorrelationId(
                            TransactionCorrelationId.TransactionCorrelationIdBuilder
                                    .aCorrelationIdBuilder()
                                    .withType(TransactionCorrelationId.CorrelationIdType.PARTNER_TRANSACTION_ID)
                                    .withValue(partnerTransactionId)
                                    .build()
                    )
                    .withHttpStatus(204)
                    .build();
        }

    }

    /**
     * Build {@link TransactionStateChangedResponse} instances.
     */
    private static class TransactionStateChangedResponseHandler implements NotificationResponseHandler {

        @Override
        public NotificationResponse successResponse(String transactionId, CheckStatusResponse checkStatusResponse) {
            return this.buildResponse(transactionId
                    , checkStatusResponse.getCheckStatusOut().getTransactionId()
                    , SuccessTransactionStatus.builder().build()
            );
        }

        @Override
        public NotificationResponse failureResponse(String transactionId, CheckStatusResponse checkStatusResponse, FailureCause failureCause) {
            return this.buildResponse(transactionId
                    , checkStatusResponse.getCheckStatusOut().getTransactionId()
                    , FailureTransactionStatus.builder().failureCause(failureCause).build()
            );

        }

        @Override
        public NotificationResponse onHoldResponse(String transactionId, CheckStatusResponse checkStatusResponse) {
            return this.buildResponse(transactionId
                    , checkStatusResponse.getCheckStatusOut().getTransactionId()
                    , OnHoldTransactionStatus.builder().onHoldCause(OnHoldCause.ASYNC_RETRY).build()
            );
        }

        @Override
        public NotificationResponse handlePluginException(String transactionId, String partnerTransactionId, PluginException e) {
            return TransactionStateChangedResponse.TransactionStateChangedResponseBuilder
                    .aTransactionStateChangedResponse()
                    .withPartnerTransactionId(partnerTransactionId)
                    .withTransactionId(transactionId)
                    .withTransactionStatus(FailureTransactionStatus.builder().failureCause(e.getFailureCause()).build())
                    .withStatusDate(new Date())
                    .withHttpStatus(204)
                    .build();
        }

        @Override
        public NotificationResponse handleRuntimeException(String transactionId, String partnerTransactionId, RuntimeException e) {
            return TransactionStateChangedResponse.TransactionStateChangedResponseBuilder
                    .aTransactionStateChangedResponse()
                    .withPartnerTransactionId(partnerTransactionId)
                    .withTransactionId(transactionId)
                    .withTransactionStatus(FailureTransactionStatus.builder().failureCause(FailureCause.INTERNAL_ERROR).build())
                    .withStatusDate(new Date())
                    .withHttpStatus(204)
                    .build();
        }


        private TransactionStateChangedResponse buildResponse(String transactionId, String partnerTransactionId, TransactionStatus status) {
            return TransactionStateChangedResponse.TransactionStateChangedResponseBuilder
                    .aTransactionStateChangedResponse()
                    .withPartnerTransactionId(partnerTransactionId)
                    .withTransactionId(transactionId)
                    .withTransactionStatus(status)
                    .withStatusDate(new Date())
                    .withAction(TransactionStateChangedResponse.Action.AUTHOR_AND_CAPTURE)
                    .build();
        }
    }
}
