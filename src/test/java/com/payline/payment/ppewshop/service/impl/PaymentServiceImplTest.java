package com.payline.payment.ppewshop.service.impl;

import com.payline.payment.ppewshop.MockUtils;
import com.payline.payment.ppewshop.bean.request.InitDossierRequest;
import com.payline.payment.ppewshop.bean.response.InitDossierResponse;
import com.payline.payment.ppewshop.bean.response.PpewShopResponseKO;
import com.payline.payment.ppewshop.exception.PluginException;
import com.payline.payment.ppewshop.service.HttpService;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.payment.Order;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseRedirect;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;

class PaymentServiceImplTest {

    @InjectMocks
    private PaymentServiceImpl service = new PaymentServiceImpl();

    @Mock
    private HttpService httpService = HttpService.getInstance();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void paymentRequest(){
        // init Mock
        InitDossierResponse initDossierResponse = InitDossierResponse.fromXml(MockUtils.templateInitDossierResponse);
        Mockito.doReturn(initDossierResponse).when(httpService).initDossier(Mockito.any(), Mockito.any());

        PaymentRequest request = MockUtils.aPaylinePaymentRequest();
        PaymentResponse response = service.paymentRequest(request);

        Assertions.assertEquals(PaymentResponseRedirect.class, response.getClass());
        PaymentResponseRedirect responseRedirect = (PaymentResponseRedirect) response;
        Assertions.assertEquals("http://redirectionUrl.com"
                , responseRedirect.getRedirectionRequest().getUrl().toExternalForm());
    }

    @Test
    void paymentRequestKO() {
        // init Mock
        PluginException exception = new PluginException(PpewShopResponseKO.ErrorCode.CODE_21999.code, FailureCause.INVALID_DATA);
        Mockito.doThrow(exception).when(httpService).initDossier(any(), any());

        PaymentRequest request = MockUtils.aPaylinePaymentRequest();
        PaymentResponse response = service.paymentRequest(request);

        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
    }

    @Test
    void paymentRequestException() {
        // init Mock
        String xml = MockUtils.templateInitDossierResponse.replace("http://redirectionUrl.com", "aMalformedUrl");
        InitDossierResponse initDossierResponse = InitDossierResponse.fromXml(xml);
        Mockito.doReturn(initDossierResponse).when(httpService).initDossier(Mockito.any(), Mockito.any());

        PaymentRequest request = MockUtils.aPaylinePaymentRequest();
        PaymentResponse response = service.paymentRequest(request);

        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
        PaymentResponseFailure responseFailure = (PaymentResponseFailure) response;
        Assertions.assertEquals(FailureCause.INVALID_DATA, responseFailure.getFailureCause());
    }

    @Test
    void createInitDossierFromPaymentRequestTest() {
        PaymentRequest request = MockUtils.aPaylinePaymentRequest();
        InitDossierRequest initDossierRequest = service.createInitDossierFromPaymentRequest(request);

        Assertions.assertNotNull(initDossierRequest);
        Assertions.assertNotNull(initDossierRequest.getInitDossierIn());
        Assertions.assertNotNull(initDossierRequest.getInitDossierIn().getCustomerInformation());
        Assertions.assertNotNull(initDossierRequest.getInitDossierIn().getMerchantConfiguration());
        Assertions.assertNotNull(initDossierRequest.getInitDossierIn().getMerchantInformation());
        Assertions.assertNotNull(initDossierRequest.getInitDossierIn().getOrderInformation());
        Assertions.assertNotNull(initDossierRequest.getInitDossierIn().getOrderReference());
        
    }

    @Test
    void getMainCatTest() {
        List<Order.OrderItem> items = new ArrayList<>();
        items.add(Order.OrderItem.OrderItemBuilder.anOrderItem()
                .withAmount(MockUtils.aPaylineAmount(2)).withCategory("foo").withQuantity(1L).build()
        );
        items.add(Order.OrderItem.OrderItemBuilder.anOrderItem()
                .withAmount(MockUtils.aPaylineAmount(1)).withCategory("bar").withQuantity(4L).build()
        );
        items.add(Order.OrderItem.OrderItemBuilder.anOrderItem()
                .withAmount(MockUtils.aPaylineAmount(3)).withCategory("baz").withQuantity(1L).build()
        );
        items.add(Order.OrderItem.OrderItemBuilder.anOrderItem()
                .withAmount(MockUtils.aPaylineAmount(3)).withCategory("foo").withQuantity(1L).build()
        );

        String cat = service.getMainCat(items);
        Assertions.assertEquals("foo", cat);
    }


    private static Stream<Arguments> civility_set() {
        return Stream.of(
                Arguments.of("1", "MME"),
                Arguments.of("2", "MME"),
                Arguments.of("3", "MLE"),
                Arguments.of("4", "MR"),
                Arguments.of("5", "MR"),
                Arguments.of("6", "MME"),
                Arguments.of("7", "MR"),
                Arguments.of("8", "MR"),
                Arguments.of("9", "MR"),
                Arguments.of("10", "MR"),
                Arguments.of("11", "MR"),
                Arguments.of("12", "MR")
        );
    }

    @ParameterizedTest
    @MethodSource("civility_set")
    void testGetCivilityFromPayline(String paylineCivility, String ppewCivility) {
        Assertions.assertEquals(ppewCivility, PaymentServiceImpl.getCivilityFromPayline(paylineCivility));
    }
}