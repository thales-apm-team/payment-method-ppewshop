package com.payline.payment.ppewshop.utils.http;

import com.payline.payment.ppewshop.MockUtils;
import com.payline.payment.ppewshop.bean.common.*;
import com.payline.payment.ppewshop.bean.configuration.RequestConfiguration;
import com.payline.payment.ppewshop.bean.request.CheckStatusRequest;
import com.payline.payment.ppewshop.bean.request.InitDossierRequest;
import com.payline.payment.ppewshop.bean.response.CheckStatusResponse;
import com.payline.payment.ppewshop.bean.response.InitDossierResponse;
import com.payline.payment.ppewshop.exception.PluginException;
import com.payline.payment.ppewshop.service.HttpService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class HttpServiceTest {

    @Spy
    @InjectMocks
    private HttpService httpService = HttpService.getInstance();

    @Mock
    private HttpClient client = HttpClient.getInstance();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void checkStatus() {
        StringResponse stringResponse = MockUtils.mockStringResponse(200
                , "OK"
                , MockUtils.templateCheckStatusResponse.replace(MockUtils.STATUS_CODE, CheckStatusOut.StatusCode.A.name())
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

        CheckStatusIn checkStatusIn = CheckStatusIn.Builder
                .aCheckStatusIn()
                .withTransactionId("transactionId")
                .withMerchantInformation(merchantInformation)
                .build();

        CheckStatusRequest request = new CheckStatusRequest(checkStatusIn);

        CheckStatusResponse response = httpService.checkStatus(configuration, request);

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getCheckStatusOut());
        Assertions.assertEquals( "1234567890",response.getCheckStatusOut().getTransactionId());
        Assertions.assertEquals( CheckStatusOut.StatusCode.A,response.getCheckStatusOut().getStatusCode());
        Assertions.assertEquals( "34600015",response.getCheckStatusOut().getCreditAuthorizationNumber());
        Assertions.assertEquals( "32552564",response.getCheckStatusOut().getMerchantOrderReference().getMerchantOrderId());

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

        CheckStatusIn checkStatusIn = CheckStatusIn.Builder
                .aCheckStatusIn()
                .withTransactionId("transactionId")
                .withMerchantInformation(merchantInformation)
                .build();

        CheckStatusRequest request = new CheckStatusRequest(checkStatusIn);

        Assertions.assertThrows(PluginException.class, () -> httpService.checkStatus(configuration, request));

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

        InitDossierIn dossierIn = InitDossierIn.Builder
                .anInitDossier()
                .withMerchantInformation(merchantInformation)
                .withMerchantConfiguration(merchantConfiguration)
                .withCustomerInformation(customerInformation)
                .withOrderInformation(orderInformation)
                .build();

        InitDossierRequest request = new InitDossierRequest(dossierIn);
        InitDossierResponse response = httpService.initDossier(configuration, request);

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getInitDossierOut());
        Assertions.assertEquals( "1234567890",response.getInitDossierOut().getTransactionId());
        Assertions.assertEquals( "http://redirectionUrl.com",response.getInitDossierOut().getRedirectionUrl());

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

        InitDossierIn dossierIn = InitDossierIn.Builder
                .anInitDossier()
                .withMerchantInformation(merchantInformation)
                .withMerchantConfiguration(merchantConfiguration)
                .withCustomerInformation(customerInformation)
                .withOrderInformation(orderInformation)
                .build();

        InitDossierRequest request = new InitDossierRequest(dossierIn);

        Assertions.assertThrows(PluginException.class, () -> httpService.initDossier(configuration, request));

        // assert the mock is working properly (to avoid false negative)
        verify( client, never() ).execute( any() );
    }
}