package com.payline.payment.ppweshop.service.impl;

import com.payline.payment.ppweshop.utils.http.HttpClient;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.logger.LogManager;
import com.payline.pmapi.service.PaymentService;
import org.apache.logging.log4j.Logger;

public class PaymentServiceImpl implements PaymentService {
    private static final Logger LOGGER = LogManager.getLogger(PaymentServiceImpl.class);
    private HttpClient client = HttpClient.getInstance();


    @Override
    public PaymentResponse paymentRequest(PaymentRequest paymentRequest) {
        return null;
    }

}
