package com.payline.payment.ppewshop.exception;

import com.payline.payment.ppewshop.utils.PluginUtils;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.paymentform.response.configuration.impl.PaymentFormConfigurationResponseFailure;

/**
 * Generic exception which can be converted into the various ResponseFailure objects from the PM-API.
 */
public class PluginException extends RuntimeException {

    public static final int ERROR_CODE_MAX_LENGTH = 50;

    private final String errorCode;
    private final FailureCause failureCause;

    public PluginException(String message) {
        this(message, FailureCause.INTERNAL_ERROR);
    }

    public PluginException(String message, FailureCause failureCause) {
        super(message);
        checkMessageAndFailureCause(message, failureCause);
        this.errorCode = PluginUtils.truncate(message, ERROR_CODE_MAX_LENGTH);
        this.failureCause = failureCause;
    }

    public PluginException(String message, Exception cause) {
        this(message, FailureCause.INTERNAL_ERROR, cause);
    }

    public PluginException(String message, FailureCause failureCause, Exception cause) {
        super(message, cause);
        checkMessageAndFailureCause(message, failureCause);
        this.errorCode = PluginUtils.truncate(message, ERROR_CODE_MAX_LENGTH);
        this.failureCause = failureCause;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public FailureCause getFailureCause() {
        return failureCause;
    }

    /**
     * Check if the Exception message and the FailureCause are not null or empty
     *
     * @param message      the Exception message
     * @param failureCause the FailureCause associated to the Exception
     */
    private void checkMessageAndFailureCause(String message, FailureCause failureCause) {
        if (PluginUtils.isEmpty(message) || failureCause == null) {
            throw new IllegalStateException("PluginException must have a non-empty message and a failureCause");
        }
    }

    /**
     * Instantiate a builder for {@link PaymentResponseFailure}.
     * Returning a builder instead of the class instance allow subsequent complement,
     * with other fields than 'failureCause' or 'errorCode', such as 'partnerTransactionId' for example.
     *
     * @return A pre-configured builder
     */
    public PaymentResponseFailure.PaymentResponseFailureBuilder toPaymentResponseFailureBuilder() {
        return PaymentResponseFailure.PaymentResponseFailureBuilder.aPaymentResponseFailure()
                .withFailureCause(failureCause)
                .withErrorCode(errorCode);
    }

    /**
     * Instantiate a builder for {@link PaymentFormConfigurationResponseFailure}.
     * Returning a builder instead of the class instance allow subsequent complement,
     * with other fields than 'failureCause' or 'errorCode', such as 'partnerTransactionId' for example.
     *
     * @return A pre-configured builder
     */
    public PaymentFormConfigurationResponseFailure.PaymentFormConfigurationResponseFailureBuilder toPaymentFormConfigurationResponseFailureBuilder() {
        return PaymentFormConfigurationResponseFailure.PaymentFormConfigurationResponseFailureBuilder.aPaymentFormConfigurationResponseFailure()
                .withFailureCause(failureCause)
                .withErrorCode(errorCode);
    }

    /**
     * Utility static method to build an error code from a {@link RuntimeException}.
     * For example, if the RuntimeException is a NullPointerException, the message will be: "plugin error: NullPointerException"
     *
     * @param e The exception
     * @return A truncated errorCode to insert into any FailureResponse object.
     */
    public static String runtimeErrorCode(RuntimeException e) {
        String errorCode = "plugin error: " + e.toString().substring(e.toString().lastIndexOf('.') + 1);
        return PluginUtils.truncate(errorCode, ERROR_CODE_MAX_LENGTH);
    }

}
