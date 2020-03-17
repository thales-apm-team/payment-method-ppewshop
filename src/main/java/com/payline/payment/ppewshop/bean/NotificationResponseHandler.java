package com.payline.payment.ppewshop.bean;

import com.payline.payment.ppewshop.bean.response.CheckStatusResponse;
import com.payline.payment.ppewshop.exception.PluginException;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.notification.response.NotificationResponse;

/**
 * Define the methods of the classes which will build the instances of {@link NotificationResponse} to return.
 */
public interface NotificationResponseHandler {
    NotificationResponse successResponse(String transactionId, CheckStatusResponse checkStatusResponse);

    NotificationResponse failureResponse(String transactionId, CheckStatusResponse checkStatusResponse, FailureCause failureCause);

    NotificationResponse onHoldResponse(String transactionId, CheckStatusResponse checkStatusResponse);

    NotificationResponse handlePluginException(String transactionId, String partnerTransactionId, PluginException e);

    NotificationResponse handleRuntimeException(String transactionId, String partnerTransactionId, RuntimeException e);

}
