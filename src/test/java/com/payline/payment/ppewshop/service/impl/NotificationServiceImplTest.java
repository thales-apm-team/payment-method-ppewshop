package com.payline.payment.ppewshop.service.impl;

import com.payline.payment.ppewshop.MockUtils;
import com.payline.payment.ppewshop.bean.common.CheckStatusOut;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.common.FailureTransactionStatus;
import com.payline.pmapi.bean.common.OnHoldTransactionStatus;
import com.payline.pmapi.bean.common.SuccessTransactionStatus;
import com.payline.pmapi.bean.notification.request.NotificationRequest;
import com.payline.pmapi.bean.notification.response.NotificationResponse;
import com.payline.pmapi.bean.notification.response.impl.PaymentResponseByNotificationResponse;
import com.payline.pmapi.bean.notification.response.impl.TransactionStateChangedResponse;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseOnHold;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseRedirect;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseSuccess;
import com.payline.pmapi.service.NotificationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.stream.Stream;

class NotificationServiceImplTest {
    @InjectMocks
    private NotificationServiceImpl service = new NotificationServiceImpl();

    @BeforeEach
    void setUp() {
    }



    private static Stream<Arguments> statusCode_set() {
        return Stream.of(
                Arguments.of(CheckStatusOut.StatusCode.A, SuccessTransactionStatus.class),
                Arguments.of(CheckStatusOut.StatusCode.E, OnHoldTransactionStatus.class),
                Arguments.of(CheckStatusOut.StatusCode.I, FailureTransactionStatus.class),
                Arguments.of(CheckStatusOut.StatusCode.R, FailureTransactionStatus.class),
                Arguments.of(CheckStatusOut.StatusCode.C, FailureTransactionStatus.class),
                Arguments.of("XXXXX", FailureTransactionStatus.class)
        );
    }

    @ParameterizedTest
    @MethodSource("statusCode_set")
    void parseStatusChanged(String statusCode, Class responseClass) {
        String xml = MockUtils.templateCheckStatusResponse
                .replace(MockUtils.STATUS_CODE, statusCode);
        InputStream stream = new ByteArrayInputStream(xml.getBytes());
        NotificationRequest request = MockUtils.aPaylineNotificationRequestBuilder().withTransactionId("1").withContent(stream).build();

        NotificationResponse response = service.parse(request);

        Assertions.assertEquals(TransactionStateChangedResponse.class, response.getClass());
        TransactionStateChangedResponse transactionStateChangedResponse = (TransactionStateChangedResponse) response;
        Assertions.assertEquals(responseClass, transactionStateChangedResponse.getTransactionStatus().getClass());
    }

    @Test
    void parseStatusChangedException(){
        InputStream stream = new ByteArrayInputStream("thisIsNotXmlFormatted".getBytes());
        NotificationRequest request = MockUtils.aPaylineNotificationRequestBuilder().withTransactionId("1").withContent(stream).build();

        NotificationResponse response = service.parse(request);
        Assertions.assertEquals(TransactionStateChangedResponse.class, response.getClass());
        TransactionStateChangedResponse transactionStateChangedResponse = (TransactionStateChangedResponse) response;
        Assertions.assertEquals(FailureTransactionStatus.class, transactionStateChangedResponse.getTransactionStatus().getClass());

        FailureTransactionStatus transactionStatus = (FailureTransactionStatus) transactionStateChangedResponse.getTransactionStatus();
        Assertions.assertEquals(FailureCause.INVALID_DATA, transactionStatus.getFailureCause());

    }


    private static Stream<Arguments> notification_set() {
        return Stream.of(
                Arguments.of(CheckStatusOut.StatusCode.A, PaymentResponseSuccess.class),
                Arguments.of(CheckStatusOut.StatusCode.E, PaymentResponseOnHold.class),
                Arguments.of(CheckStatusOut.StatusCode.I, PaymentResponseFailure.class),
                Arguments.of(CheckStatusOut.StatusCode.R, PaymentResponseFailure.class),
                Arguments.of(CheckStatusOut.StatusCode.C, PaymentResponseFailure.class),
                Arguments.of("XXXXX", PaymentResponseFailure.class)
        );
    }

    @ParameterizedTest
    @MethodSource("notification_set")
    void parseNotificationChanged(String statusCode, Class responseClass) {
        String xml = MockUtils.templateCheckStatusResponse
                .replace(MockUtils.STATUS_CODE, statusCode);
        InputStream stream = new ByteArrayInputStream(xml.getBytes());
        NotificationRequest request = MockUtils.aPaylineNotificationRequestBuilder().withContent(stream).build();

        NotificationResponse response = service.parse(request);

        Assertions.assertEquals(PaymentResponseByNotificationResponse.class, response.getClass());
        PaymentResponseByNotificationResponse paymentResponseByNotificationResponse = (PaymentResponseByNotificationResponse) response;
        Assertions.assertEquals(responseClass, paymentResponseByNotificationResponse.getPaymentResponse().getClass());
    }

    @Test
    void parseNotificationException(){
        InputStream stream = new ByteArrayInputStream("thisIsNotXmlFormatted".getBytes());
        NotificationRequest request = MockUtils.aPaylineNotificationRequestBuilder().withContent(stream).build();

        NotificationResponse response = service.parse(request);

        Assertions.assertEquals(PaymentResponseByNotificationResponse.class, response.getClass());
        PaymentResponseByNotificationResponse paymentResponseByNotificationResponse = (PaymentResponseByNotificationResponse) response;
        Assertions.assertEquals(PaymentResponseFailure.class, paymentResponseByNotificationResponse.getPaymentResponse().getClass());
        PaymentResponseFailure responseFailure = (PaymentResponseFailure) paymentResponseByNotificationResponse.getPaymentResponse();
        Assertions.assertEquals(FailureCause.INVALID_DATA, responseFailure.getFailureCause());
    }

}