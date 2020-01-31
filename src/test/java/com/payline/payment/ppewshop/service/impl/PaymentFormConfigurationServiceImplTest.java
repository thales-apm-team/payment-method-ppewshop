package com.payline.payment.ppewshop.service.impl;

import com.payline.payment.ppewshop.MockUtils;
import com.payline.pmapi.bean.paymentform.bean.form.NoFieldForm;
import com.payline.pmapi.bean.paymentform.response.configuration.PaymentFormConfigurationResponse;
import com.payline.pmapi.bean.paymentform.response.configuration.impl.PaymentFormConfigurationResponseSpecific;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentFormConfigurationServiceImplTest {
    private PaymentFormConfigurationServiceImpl service = new PaymentFormConfigurationServiceImpl();

    @Test
    void getPaymentFormConfiguration() {
        PaymentFormConfigurationResponse response = service.getPaymentFormConfiguration(MockUtils.aPaymentFormConfigurationRequest());

        Assertions.assertEquals(PaymentFormConfigurationResponseSpecific.class, response.getClass());

        PaymentFormConfigurationResponseSpecific responseSpecific = (PaymentFormConfigurationResponseSpecific) response;
        Assertions.assertEquals(NoFieldForm.class, responseSpecific.getPaymentForm().getClass());
    }
}