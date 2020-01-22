package com.payline.payment.ppewshop.bean;

import com.payline.payment.ppewshop.MockUtils;
import com.payline.payment.ppewshop.bean.common.CustomerInformation;
import com.payline.payment.ppewshop.bean.common.MerchantConfiguration;
import com.payline.payment.ppewshop.bean.common.MerchantInformation;
import com.payline.payment.ppewshop.bean.common.OrderInformation;
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

    private static final String errorCode = "ERROR_CODE";
    private static final String errorDescription = "ERROR_DESCRIPTION";


    @Test
    void initDossierRequestTest() throws Exception {
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

        InitDossierRequest request = new InitDossierRequest();
        request.getInitDossierIn().setMerchantInformation(merchantInformation);
        request.getInitDossierIn().setMerchantConfiguration(merchantConfiguration);
        request.getInitDossierIn().setCustomerInformation(customerInformation);
        request.getInitDossierIn().setOrderInformation(orderInformation);

        Assertions.assertEquals(MockUtils.templateInitDossierRequest, request.toXml());
    }

    @Test
    void checkStatusRequestTest() throws Exception {
        MerchantInformation merchantInformation = MerchantInformation.Builder.aMerchantInformation()
                .withMerchantCode(merchantCode)
                .withDistributorNumber(distributorNumber)
                .withCountryCode(countryCode)
                .build();

        CheckStatusRequest request = new CheckStatusRequest();
        request.getCheckStatusIn().setMerchantInformation(merchantInformation);
        request.getCheckStatusIn().setTransactionId(transactionId);

        Assertions.assertEquals(MockUtils.templateCheckStatusRequest, request.toXml());
    }

    @Test
    void initDossierResponseOKTest() throws Exception {
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
    void checkStatusResponseOKTest() throws Exception {
        CheckStatusResponse response = CheckStatusResponse.fromXml(MockUtils.templateCheckStatusResponse);

        Assertions.assertNotNull(response.getCheckStatusOut());
        Assertions.assertEquals(transactionId, response.getCheckStatusOut().getTransactionId());
        Assertions.assertEquals(merchandOrderId, response.getCheckStatusOut().getMerchantOrderReference().getMerchantOrderId());
        Assertions.assertEquals(MockUtils.STATUS_CODE, response.getCheckStatusOut().getStatusCode());
        Assertions.assertEquals(authorizationNumber, response.getCheckStatusOut().getCreditAuthorizationNumber());
    }

    @Test
    void ResponseKOTest() throws Exception {
        PpewShopResponseKO responseKO = PpewShopResponseKO.fromXml(MockUtils.templateResponseError);

        Assertions.assertNotNull(responseKO);
        Assertions.assertEquals(errorCode, responseKO.getErrorCode());
        Assertions.assertEquals(errorDescription, responseKO.getErrorDescription());
    }


    private static Stream<Arguments> errorCode_set() {
        return Stream.of(
                Arguments.of("11001", FailureCause.PAYMENT_PARTNER_ERROR),
                Arguments.of("11002", FailureCause.PAYMENT_PARTNER_ERROR),
                Arguments.of("11008", FailureCause.PAYMENT_PARTNER_ERROR),
                Arguments.of("11009", FailureCause.PAYMENT_PARTNER_ERROR),
                Arguments.of("11999", FailureCause.PAYMENT_PARTNER_ERROR),
                Arguments.of("12001", FailureCause.INVALID_DATA),
                Arguments.of("12002", FailureCause.INVALID_DATA),
                Arguments.of("12003", FailureCause.INVALID_DATA),
                Arguments.of("12004", FailureCause.INVALID_DATA),
                Arguments.of("12006", FailureCause.INVALID_DATA),
                Arguments.of("12205", FailureCause.INVALID_DATA),
                Arguments.of("12207", FailureCause.INVALID_FIELD_FORMAT),
                Arguments.of("12301", FailureCause.REFUSED),
                Arguments.of("21001", FailureCause.PAYMENT_PARTNER_ERROR),
                Arguments.of("21002", FailureCause.PAYMENT_PARTNER_ERROR),
                Arguments.of("21003", FailureCause.PAYMENT_PARTNER_ERROR),
                Arguments.of("21009", FailureCause.PAYMENT_PARTNER_ERROR),
                Arguments.of("21999", FailureCause.PAYMENT_PARTNER_ERROR),
                Arguments.of("21004", FailureCause.PAYMENT_PARTNER_ERROR),
                Arguments.of("22001", FailureCause.INVALID_DATA),
                Arguments.of("22002", FailureCause.INVALID_DATA),
                Arguments.of("22003", FailureCause.INVALID_DATA),
                Arguments.of("XXXXX", FailureCause.PARTNER_UNKNOWN_ERROR)
        );
    }

    @ParameterizedTest
    @MethodSource("errorCode_set")
    void getFailureCauseFromErrorCodeTest(String errorCode, FailureCause cause) throws Exception{
        String xml = MockUtils.templateResponseError
                .replace("ERROR_CODE", errorCode);

        PpewShopResponseKO responseKO = PpewShopResponseKO.fromXml(xml);
        Assertions.assertEquals(cause, responseKO.getFailureCauseFromErrorCode());
    }

}
