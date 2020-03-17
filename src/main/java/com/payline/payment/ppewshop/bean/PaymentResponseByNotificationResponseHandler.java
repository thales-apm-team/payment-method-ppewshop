package com.payline.payment.ppewshop.bean;

import com.payline.payment.ppewshop.bean.response.CheckStatusResponse;
import com.payline.payment.ppewshop.exception.PluginException;
import com.payline.payment.ppewshop.service.impl.NotificationServiceImpl;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.common.OnHoldCause;
import com.payline.pmapi.bean.common.TransactionCorrelationId;
import com.payline.pmapi.bean.notification.response.NotificationResponse;
import com.payline.pmapi.bean.notification.response.impl.PaymentResponseByNotificationResponse;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.buyerpaymentidentifier.impl.EmptyTransactionDetails;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseOnHold;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseSuccess;

public class PaymentResponseByNotificationResponseHandler implements NotificationResponseHandler {
    @Override
    public NotificationResponse successResponse(String transactionId, CheckStatusResponse checkStatusResponse) {
        PaymentResponse response = PaymentResponseSuccess.PaymentResponseSuccessBuilder
                .aPaymentResponseSuccess()
                .withPartnerTransactionId(checkStatusResponse.getCheckStatusOut().getTransactionId())
                .withTransactionDetails(new EmptyTransactionDetails())
                .withTransactionAdditionalData(checkStatusResponse.getCheckStatusOut().getCreditAuthorizationNumber())
                .withStatusCode(checkStatusResponse.getCheckStatusOut().getStatusCode().name())
                .build();

        return this.buildResponse(response, checkStatusResponse.getCheckStatusOut().getTransactionId());
    }

    @Override
    public NotificationResponse failureResponse(String transactionId, CheckStatusResponse checkStatusResponse, FailureCause failureCause) {
        PaymentResponse response = PaymentResponseFailure.PaymentResponseFailureBuilder
                .aPaymentResponseFailure()
                .withPartnerTransactionId(checkStatusResponse.getCheckStatusOut().getTransactionId())
                .withErrorCode(checkStatusResponse.getCheckStatusOut().getStatusCode().name())
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
                .withStatusCode(checkStatusResponse.getCheckStatusOut().getStatusCode().name())
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
                .withHttpStatus(NotificationServiceImpl.CREATED)
                .build();
    }

}
