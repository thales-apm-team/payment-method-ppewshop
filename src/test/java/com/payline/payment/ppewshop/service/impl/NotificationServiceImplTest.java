package com.payline.payment.ppewshop.service.impl;

import com.payline.payment.ppewshop.MockUtils;
import com.payline.payment.ppewshop.bean.common.CheckStatusOut;
import com.payline.payment.ppewshop.bean.response.CheckStatusResponse;
import com.payline.payment.ppewshop.service.HttpService;
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
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseSuccess;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;

class NotificationServiceImplTest {
    @InjectMocks
    private NotificationServiceImpl service = new NotificationServiceImpl();

    @Mock
    private HttpService httpService = HttpService.getInstance();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    private static Stream<Arguments> statusCode_set() {
        return Stream.of(
                Arguments.of(CheckStatusOut.StatusCode.A, SuccessTransactionStatus.class),
                Arguments.of(CheckStatusOut.StatusCode.E, OnHoldTransactionStatus.class),
                Arguments.of(CheckStatusOut.StatusCode.I, FailureTransactionStatus.class),
                Arguments.of(CheckStatusOut.StatusCode.R, FailureTransactionStatus.class),
                Arguments.of(CheckStatusOut.StatusCode.C, FailureTransactionStatus.class)
        );
    }

    @ParameterizedTest
    @MethodSource("statusCode_set")
    void parseStatusChanged(CheckStatusOut.StatusCode statusCode, Class responseClass) {
        NotificationRequest request = MockUtils.aPaylineNotificationRequestBuilder().withTransactionId("1").build();

        String xmlOK = MockUtils.templateCheckStatusResponse
                .replace(MockUtils.STATUS_CODE, statusCode.name());

        CheckStatusResponse checkStatusResponse = CheckStatusResponse.fromXml(xmlOK);
        Mockito.doReturn(checkStatusResponse).when(httpService).checkStatus(any(), any());


        NotificationResponse response = service.parse(request);

        Assertions.assertEquals(TransactionStateChangedResponse.class, response.getClass());
        TransactionStateChangedResponse transactionStateChangedResponse = (TransactionStateChangedResponse) response;
        Assertions.assertEquals(responseClass, transactionStateChangedResponse.getTransactionStatus().getClass());
    }

    @Test
    void parseStatusChangedException() {
        NotificationRequest request = MockUtils.aPaylineNotificationRequestBuilder().withTransactionId("1").build();

        NotificationResponse response = service.parse(request);
        Assertions.assertEquals(TransactionStateChangedResponse.class, response.getClass());
        TransactionStateChangedResponse transactionStateChangedResponse = (TransactionStateChangedResponse) response;
        Assertions.assertEquals(FailureTransactionStatus.class, transactionStateChangedResponse.getTransactionStatus().getClass());

        FailureTransactionStatus transactionStatus = (FailureTransactionStatus) transactionStateChangedResponse.getTransactionStatus();
        Assertions.assertEquals(FailureCause.INTERNAL_ERROR, transactionStatus.getFailureCause());

    }


    private static Stream<Arguments> notification_set() {
        return Stream.of(
                Arguments.of(CheckStatusOut.StatusCode.A, PaymentResponseSuccess.class),
                Arguments.of(CheckStatusOut.StatusCode.E, PaymentResponseOnHold.class),
                Arguments.of(CheckStatusOut.StatusCode.I, PaymentResponseFailure.class),
                Arguments.of(CheckStatusOut.StatusCode.R, PaymentResponseFailure.class),
                Arguments.of(CheckStatusOut.StatusCode.C, PaymentResponseFailure.class)
        );
    }

    @ParameterizedTest
    @MethodSource("notification_set")
    void parseNotificationChanged(CheckStatusOut.StatusCode statusCode, Class responseClass) {
        NotificationRequest request = MockUtils.aPaylineNotificationRequestBuilder().build();

        String xmlOK = MockUtils.templateCheckStatusResponse
                .replace(MockUtils.STATUS_CODE, statusCode.name());

        CheckStatusResponse checkStatusResponse = CheckStatusResponse.fromXml(xmlOK);
        Mockito.doReturn(checkStatusResponse).when(httpService).checkStatus(any(), any());


        NotificationResponse response = service.parse(request);

        Assertions.assertEquals(PaymentResponseByNotificationResponse.class, response.getClass());
        PaymentResponseByNotificationResponse paymentResponseByNotificationResponse = (PaymentResponseByNotificationResponse) response;
        Assertions.assertEquals(responseClass, paymentResponseByNotificationResponse.getPaymentResponse().getClass());
    }

    @Test
    void parseNotificationException() {
        NotificationRequest request = MockUtils.aPaylineNotificationRequestBuilder().build();

        NotificationResponse response = service.parse(request);

        Assertions.assertEquals(PaymentResponseByNotificationResponse.class, response.getClass());
        PaymentResponseByNotificationResponse paymentResponseByNotificationResponse = (PaymentResponseByNotificationResponse) response;
        Assertions.assertEquals(PaymentResponseFailure.class, paymentResponseByNotificationResponse.getPaymentResponse().getClass());
        PaymentResponseFailure responseFailure = (PaymentResponseFailure) paymentResponseByNotificationResponse.getPaymentResponse();
        Assertions.assertEquals(FailureCause.INTERNAL_ERROR, responseFailure.getFailureCause());
    }

    @Test
    void getTransactionIdFromURL() {
        Assertions.assertEquals("1234567890123", service.getTransactionIdFromURL("http://wwww.this.is.an.url.com/transactionDeId=1234567890123&foo=bar"));
        Assertions.assertEquals("1234567890123", service.getTransactionIdFromURL("http://wwww.this.is.an.url.com/transactionDeId=1234567890123"));
    }
}