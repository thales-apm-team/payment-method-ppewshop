package com.payline.payment.ppewshop.utils.http;

import com.payline.payment.ppewshop.MockUtils;
import com.payline.payment.ppewshop.bean.common.CustomerInformation;
import com.payline.payment.ppewshop.bean.common.MerchantConfiguration;
import com.payline.payment.ppewshop.bean.common.MerchantInformation;
import com.payline.payment.ppewshop.bean.common.OrderInformation;
import com.payline.payment.ppewshop.bean.configuration.RequestConfiguration;
import com.payline.payment.ppewshop.bean.request.CheckStatusRequest;
import com.payline.payment.ppewshop.bean.request.InitDossierRequest;
import com.payline.payment.ppewshop.bean.response.CheckStatusResponse;
import com.payline.payment.ppewshop.bean.response.InitDossierResponse;
import com.payline.payment.ppewshop.exception.PluginException;
import org.apache.http.client.methods.HttpRequestBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class HttpClientTest {

    @Spy
    @InjectMocks
    private HttpClient client = HttpClient.getInstance();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void checkStatus() {
        StringResponse stringResponse = MockUtils.mockStringResponse(200
                , "OK"
                , MockUtils.templateCheckStatusResponse
                , null);

        Mockito.doReturn(stringResponse).when(client).post(any(), any(), any());

        RequestConfiguration configuration = new RequestConfiguration(
                MockUtils.aContractConfiguration()
                , MockUtils.anEnvironment()
                , MockUtils.aPartnerConfiguration()
        );

        MerchantInformation merchantInformation = MerchantInformation.Builder.aMerchantInformation()
                .withMerchantCode("merchantCode")
                .withDistributorNumber("distributor")
                .withCountryCode("countryCode")
                .build();

        CheckStatusRequest request = new CheckStatusRequest();
        request.getCheckStatusIn().setMerchantInformation(merchantInformation);
        request.getCheckStatusIn().setTransactionId("transactionId");

        CheckStatusResponse response = client.checkStatus(configuration, request);

        Assertions.assertNotNull(response);

        // assert the mock is working properly (to avoid false negative)
        verify( client, never() ).execute( any() );
    }

    @Test
    void checkStatusKO() {
        StringResponse stringResponse = MockUtils.mockStringResponse(500
                , "KO"
                , MockUtils.templateResponseError
                , null);

        Mockito.doReturn(stringResponse).when(client).post(any(), any(), any());

        RequestConfiguration configuration = new RequestConfiguration(
                MockUtils.aContractConfiguration()
                , MockUtils.anEnvironment()
                , MockUtils.aPartnerConfiguration()
        );

        MerchantInformation merchantInformation = MerchantInformation.Builder.aMerchantInformation()
                .withMerchantCode("merchantCode")
                .withDistributorNumber("distributor")
                .withCountryCode("countryCode")
                .build();

        CheckStatusRequest request = new CheckStatusRequest();
        request.getCheckStatusIn().setMerchantInformation(merchantInformation);
        request.getCheckStatusIn().setTransactionId("transactionId");

        Assertions.assertThrows(PluginException.class, () -> client.checkStatus(configuration, request));

        // assert the mock is working properly (to avoid false negative)
        verify( client, never() ).execute( any() );
    }

    @Test
    void initDossier() {
        StringResponse stringResponse = MockUtils.mockStringResponse(200
                , "OK"
                , MockUtils.templateInitDossierResponse
                , null);

        Mockito.doReturn(stringResponse).when(client).post(any(), any(), any());

        RequestConfiguration configuration = new RequestConfiguration(
                MockUtils.aContractConfiguration()
                , MockUtils.anEnvironment()
                , MockUtils.aPartnerConfiguration()
        );

        MerchantInformation merchantInformation = MerchantInformation.Builder.aMerchantInformation()
                .withMerchantCode("merchantCode")
                .withDistributorNumber("distributorNumber")
                .withCountryCode("countryCode")
                .build();

        MerchantConfiguration merchantConfiguration = MerchantConfiguration.Builder.aMerchantConfiguration()
                .withGuardBackUrl("guardBackUrl")
                .withGuardPushUrl("guardPushUrl")
                .build();

        CustomerInformation customerInformation = CustomerInformation.Builder.aCustomerInformation()
                .withTitle("title")
                .withCustomerLanguage("customerLanguage")
                .withFirstName("firstName")
                .withName("name")
                .withBirthDate("birthDate")
                .withEmail("email")
                .withAddressLine1("addressLine1")
                .withAddressLine2("addressLine2")
                .withCity("city")
                .withPostCode("postCode")
                .withCellPhoneNumber("cellPhoneNumber")
                .build();

        OrderInformation orderInformation = OrderInformation.Builder.anOrderInformation()
                .withGoodsCode("goodsCode")
                .withPrice("price")
                .withFinancialProductType("financialProductType")
                .build();

        InitDossierRequest request = new InitDossierRequest();
        request.getInitDossierIn().setMerchantInformation(merchantInformation);
        request.getInitDossierIn().setMerchantConfiguration(merchantConfiguration);
        request.getInitDossierIn().setCustomerInformation(customerInformation);
        request.getInitDossierIn().setOrderInformation(orderInformation);

        InitDossierResponse response = client.initDossier(configuration, request);

        Assertions.assertNotNull(response);
    }

    @Test
    void initDossierKO() {
        StringResponse stringResponse = MockUtils.mockStringResponse(500
                , "KO"
                , MockUtils.templateResponseError
                , null);

        Mockito.doReturn(stringResponse).when(client).post(any(), any(), any());

        RequestConfiguration configuration = new RequestConfiguration(
                MockUtils.aContractConfiguration()
                , MockUtils.anEnvironment()
                , MockUtils.aPartnerConfiguration()
        );

        MerchantInformation merchantInformation = MerchantInformation.Builder.aMerchantInformation()
                .withMerchantCode("merchantCode")
                .withDistributorNumber("distributorNumber")
                .withCountryCode("countryCode")
                .build();

        MerchantConfiguration merchantConfiguration = MerchantConfiguration.Builder.aMerchantConfiguration()
                .withGuardBackUrl("guardBackUrl")
                .withGuardPushUrl("guardPushUrl")
                .build();

        CustomerInformation customerInformation = CustomerInformation.Builder.aCustomerInformation()
                .withTitle("title")
                .withCustomerLanguage("customerLanguage")
                .withFirstName("firstName")
                .withName("name")
                .withBirthDate("birthDate")
                .withEmail("email")
                .withAddressLine1("addressLine1")
                .withAddressLine2("addressLine2")
                .withCity("city")
                .withPostCode("postCode")
                .withCellPhoneNumber("cellPhoneNumber")
                .build();

        OrderInformation orderInformation = OrderInformation.Builder.anOrderInformation()
                .withGoodsCode("goodsCode")
                .withPrice("price")
                .withFinancialProductType("financialProductType")
                .build();

        InitDossierRequest request = new InitDossierRequest();
        request.getInitDossierIn().setMerchantInformation(merchantInformation);
        request.getInitDossierIn().setMerchantConfiguration(merchantConfiguration);
        request.getInitDossierIn().setCustomerInformation(customerInformation);
        request.getInitDossierIn().setOrderInformation(orderInformation);


        Assertions.assertThrows(PluginException.class, () -> client.initDossier(configuration, request));

        // assert the mock is working properly (to avoid false negative)
        verify( client, never() ).execute( any() );
    }
}