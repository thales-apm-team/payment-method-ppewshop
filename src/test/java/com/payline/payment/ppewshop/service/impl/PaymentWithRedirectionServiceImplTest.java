package com.payline.payment.ppewshop.service.impl;

import com.payline.payment.ppewshop.MockUtils;
import com.payline.payment.ppewshop.bean.common.CheckStatusOut;
import com.payline.payment.ppewshop.bean.configuration.RequestConfiguration;
import com.payline.payment.ppewshop.bean.response.CheckStatusResponse;
import com.payline.payment.ppewshop.bean.response.PpewShopResponseKO;
import com.payline.payment.ppewshop.exception.PluginException;
import com.payline.payment.ppewshop.utils.http.HttpClient;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseOnHold;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseRedirect;
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

class PaymentWithRedirectionServiceImplTest {

    @InjectMocks
    private PaymentWithRedirectionServiceImpl service = new PaymentWithRedirectionServiceImpl();

    @Mock
    private HttpClient client = HttpClient.getInstance();


    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    private static Stream<Arguments> statusCode_set() {
        return Stream.of(
                Arguments.of(CheckStatusOut.StatusCode.A, PaymentResponseSuccess.class),
                Arguments.of(CheckStatusOut.StatusCode.E, PaymentResponseOnHold.class),
                Arguments.of(CheckStatusOut.StatusCode.I, PaymentResponseRedirect.class),
                Arguments.of(CheckStatusOut.StatusCode.R, PaymentResponseFailure.class),
                Arguments.of(CheckStatusOut.StatusCode.C, PaymentResponseFailure.class),
                Arguments.of("XXXXX", PaymentResponseFailure.class)
        );
    }

    @ParameterizedTest
    @MethodSource("statusCode_set")
    void retrieveTransactionStatus(String statusCode, Class responseClass) throws Exception {
        String xmlOK = MockUtils.templateCheckStatusResponse
                .replace(MockUtils.STATUS_CODE, statusCode);

        CheckStatusResponse checkStatusResponse = CheckStatusResponse.fromXml(xmlOK);
        Mockito.doReturn(checkStatusResponse).when(client).checkStatus(any(), any());

        RequestConfiguration configuration = new RequestConfiguration(
                MockUtils.aContractConfiguration()
                , MockUtils.anEnvironment()
                , MockUtils.aPartnerConfiguration()
        );

        String transactionId = "1";
        String email = "an.email@foo.bar";

        PaymentResponse response = service.retrieveTransactionStatus(configuration, transactionId, email);
        Assertions.assertEquals(responseClass, response.getClass());
    }

    @Test
    void retrieveTransactionStatusExeption() throws Exception {
        // init Mock
        String xml = MockUtils.templateCheckStatusResponse.replace("http://redirectionUrl.com", "aMalformedUrl");
        CheckStatusResponse checkStatusResponse = CheckStatusResponse.fromXml(xml);
        Mockito.doReturn(checkStatusResponse).when(client).checkStatus(Mockito.any(), Mockito.any());


        RequestConfiguration configuration = new RequestConfiguration(
                MockUtils.aContractConfiguration()
                , MockUtils.anEnvironment()
                , MockUtils.aPartnerConfiguration()
        );

        String transactionId = "1";
        String email = "an.email@foo.bar";

        PaymentResponse response = service.retrieveTransactionStatus(configuration, transactionId, email);

        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
        PaymentResponseFailure responseFailure = (PaymentResponseFailure) response;
        Assertions.assertEquals(FailureCause.INVALID_DATA, responseFailure.getFailureCause());
    }


    @Test
    void retrieveTransactionStatusException()  {
        PluginException exception = new PluginException(PpewShopResponseKO.ErrorCode._21999, FailureCause.INVALID_DATA);
        Mockito.doThrow(exception).when(client).checkStatus(any(), any());

        RequestConfiguration configuration = new RequestConfiguration(
                MockUtils.aContractConfiguration()
                , MockUtils.anEnvironment()
                , MockUtils.aPartnerConfiguration()
        );

        String transactionId = "1";
        String email = "an.email@foo.bar";

        PaymentResponse response = service.retrieveTransactionStatus(configuration, transactionId, email);
        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
    }

}