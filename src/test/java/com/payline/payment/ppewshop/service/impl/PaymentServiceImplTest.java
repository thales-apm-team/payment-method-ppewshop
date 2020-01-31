package com.payline.payment.ppewshop.service.impl;

import com.payline.payment.ppewshop.MockUtils;
import com.payline.payment.ppewshop.bean.request.InitDossierRequest;
import com.payline.payment.ppewshop.bean.response.InitDossierResponse;
import com.payline.payment.ppewshop.bean.response.PpewShopResponseKO;
import com.payline.payment.ppewshop.exception.PluginException;
import com.payline.payment.ppewshop.utils.http.HttpClient;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.payment.Order;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseRedirect;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

class PaymentServiceImplTest {

    @InjectMocks
    private PaymentServiceImpl service = new PaymentServiceImpl();

    @Mock
    private HttpClient client = HttpClient.getInstance();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void paymentRequest(){
        // init Mock
        InitDossierResponse initDossierResponse = InitDossierResponse.fromXml(MockUtils.templateInitDossierResponse);
        Mockito.doReturn(initDossierResponse).when(client).initDossier(Mockito.any(), Mockito.any());

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
        PluginException exception = new PluginException(PpewShopResponseKO.ErrorCode.CODE_21999, FailureCause.INVALID_DATA);
        Mockito.doThrow(exception).when(client).initDossier(any(), any());

        PaymentRequest request = MockUtils.aPaylinePaymentRequest();
        PaymentResponse response = service.paymentRequest(request);

        Assertions.assertEquals(PaymentResponseFailure.class, response.getClass());
    }

    @Test
    void paymentRequestException() {
        // init Mock
        String xml = MockUtils.templateInitDossierResponse.replace("http://redirectionUrl.com", "aMalformedUrl");
        InitDossierResponse initDossierResponse = InitDossierResponse.fromXml(xml);
        Mockito.doReturn(initDossierResponse).when(client).initDossier(Mockito.any(), Mockito.any());

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
}