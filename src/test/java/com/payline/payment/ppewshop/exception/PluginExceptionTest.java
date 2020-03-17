package com.payline.payment.ppewshop.exception;

import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PluginExceptionTest {

    @Test
    void runtimeErrorCode() {
        try{
            PaymentResponseFailure response = null;
            response.getErrorCode();
        }catch (RuntimeException e){
            Assertions.assertEquals("plugin error: NullPointerException", PluginException.runtimeErrorCode(e));
        }
    }
}