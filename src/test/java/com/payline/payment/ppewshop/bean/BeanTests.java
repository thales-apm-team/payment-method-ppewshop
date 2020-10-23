package com.payline.payment.ppewshop.bean;

import com.payline.payment.ppewshop.MockUtils;
import com.payline.payment.ppewshop.bean.common.*;
import com.payline.payment.ppewshop.bean.request.CheckStatusRequest;
import com.payline.payment.ppewshop.bean.request.InitDossierRequest;
import com.payline.payment.ppewshop.bean.response.CheckStatusResponse;
import com.payline.payment.ppewshop.bean.response.InitDossierResponse;
import com.payline.payment.ppewshop.bean.response.PpewShopResponseKO;
import com.payline.pmapi.bean.common.FailureCause;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class BeanTests {
    private final static String transactionId = "1234567890";
    private final static String merchantCode = "1212121212";
    private final static String distributorNumber = "2323232323";
    private final static String countryCode = "FRA";

    private final static String title = "MR";
    private final static String customerLanguage = "FR";
    private final static String firstName = "Test";
    private final static String name = "Test";
    private final static String birthDate = "1992-08-14";
    private final static String email = "test.test@cetelem.fr";
    private final static String addressLine1 = "25 Elysées la Défense";
    private final static String addressLine2 = "Apt 20";
    private final static String city = "La défense";
    private final static String postCode = "92000";
    private final static String cellPhoneNumber = "0172757512";

    private final static String guardPushUrl = "urlPush";
    private final static String guardBackUrl = "urlBack";

    private final static String goodsCode = "616";
    private final static String price = "1000";
    private final static String financialProductType = "CLA";

    private static final String redirectionUrl = "http://redirectionUrl.com";
    private static final String warningCode = "13008";
    private static final String warningDescription = "warningDescription";

    private static final String merchandOrderId = "32552564";
    private static final String authorizationNumber = "34600015";

    private static final String errorCode = "11001";
    private static final String errorDescription = "ERROR_DESCRIPTION";


    @Test
    void initDossierRequestTest() {
        MerchantInformation merchantInformation = MerchantInformation.Builder.aMerchantInformation()
                .withMerchantCode(merchantCode)
                .withDistributorNumber(distributorNumber)
                .withCountryCode(countryCode)
                .build();

        MerchantConfiguration merchantConfiguration = MerchantConfiguration.Builder.aMerchantConfiguration()
                .withGuardBackUrl(guardBackUrl)
                .withGuardPushUrl(guardPushUrl)
                .build();

        CustomerInformation customerInformation = CustomerInformation.Builder.aCustomerInformation()
                .withTitle(title)
                .withCustomerLanguage(customerLanguage)
                .withFirstName(firstName)
                .withName(name)
                .withBirthDate(birthDate)
                .withEmail(email)
                .withAddressLine1(addressLine1)
                .withAddressLine2(addressLine2)
                .withCity(city)
                .withPostCode(postCode)
                .withCellPhoneNumber(cellPhoneNumber)
                .build();

        OrderInformation orderInformation = OrderInformation.Builder.anOrderInformation()
                .withGoodsCode(goodsCode)
                .withPrice(price)
                .withFinancialProductType(financialProductType)
                .build();

        InitDossierIn dossierIn = InitDossierIn.Builder
                .anInitDossier()
                .withMerchantInformation(merchantInformation)
                .withMerchantConfiguration(merchantConfiguration)
                .withCustomerInformation(customerInformation)
                .withOrderInformation(orderInformation)
                .build();

        InitDossierRequest request = new InitDossierRequest(dossierIn);

        Assertions.assertEquals(MockUtils.templateInitDossierRequest, request.toXml());
    }

    @Test
    void checkStatusRequestTest() {
        MerchantInformation merchantInformation = MerchantInformation.Builder.aMerchantInformation()
                .withMerchantCode(merchantCode)
                .withDistributorNumber(distributorNumber)
                .withCountryCode(countryCode)
                .build();

        CheckStatusIn checkStatusIn = CheckStatusIn.Builder
                .aCheckStatusIn()
                .withTransactionId(transactionId)
                .withMerchantInformation(merchantInformation)
                .build();

        CheckStatusRequest request = new CheckStatusRequest(checkStatusIn);

        Assertions.assertEquals(MockUtils.templateCheckStatusRequest, request.toXml());
    }

    @Test
    void initDossierResponseOKTest() {
        InitDossierResponse response = InitDossierResponse.fromXml(MockUtils.templateInitDossierResponse);

        Assertions.assertNotNull(response.getInitDossierOut());
        Assertions.assertNotNull(response.getInitDossierOut().getRedirectionUrl());
        Assertions.assertEquals(redirectionUrl, response.getInitDossierOut().getRedirectionUrl());
        Assertions.assertNotNull(transactionId, response.getInitDossierOut().getTransactionId());
        Assertions.assertNotNull(response.getInitDossierOut().getWarning());
        Assertions.assertEquals(warningCode, response.getInitDossierOut().getWarning().getWarningCode());
        Assertions.assertEquals(warningDescription, response.getInitDossierOut().getWarning().getWarningDescription());
    }

    @Test
    void checkStatusResponseOKTest(){
        CheckStatusResponse response = CheckStatusResponse.fromXml(MockUtils.templateCheckStatusResponse.replace(MockUtils.STATUS_CODE, CheckStatusOut.StatusCode.A.name()));

        Assertions.assertNotNull(response.getCheckStatusOut());
        Assertions.assertEquals(transactionId, response.getCheckStatusOut().getTransactionId());
        Assertions.assertEquals(merchandOrderId, response.getCheckStatusOut().getMerchantOrderReference().getMerchantOrderId());
        Assertions.assertEquals(CheckStatusOut.StatusCode.A, response.getCheckStatusOut().getStatusCode());
        Assertions.assertEquals(authorizationNumber, response.getCheckStatusOut().getCreditAuthorizationNumber());
    }

    @Test
    void ResponseKOTest() {
        PpewShopResponseKO responseKO = PpewShopResponseKO.fromXml(MockUtils.templateResponseError.replace("ERROR_CODE", errorCode));

        Assertions.assertNotNull(responseKO);
        Assertions.assertEquals(errorCode, responseKO.getErrorCode().code);
        Assertions.assertEquals(errorDescription, responseKO.getErrorDescription());
    }

}
