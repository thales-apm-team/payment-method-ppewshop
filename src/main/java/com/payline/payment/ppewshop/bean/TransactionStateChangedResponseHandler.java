package com.payline.payment.ppewshop.bean;

import com.payline.payment.ppewshop.bean.response.CheckStatusResponse;
import com.payline.payment.ppewshop.exception.PluginException;
import com.payline.payment.ppewshop.service.impl.NotificationServiceImpl;
import com.payline.pmapi.bean.common.*;
import com.payline.pmapi.bean.notification.response.NotificationResponse;
import com.payline.pmapi.bean.notification.response.impl.TransactionStateChangedResponse;

import java.util.Date;

public class TransactionStateChangedResponseHandler implements NotificationResponseHandler {

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
                .withHttpStatus(NotificationServiceImpl.CREATED)
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
                .withHttpStatus(NotificationServiceImpl.CREATED)
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
