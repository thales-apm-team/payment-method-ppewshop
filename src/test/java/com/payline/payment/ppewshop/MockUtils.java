package com.payline.payment.ppewshop;

import com.payline.payment.ppewshop.utils.Constants;
import com.payline.payment.ppewshop.utils.http.StringResponse;
import com.payline.pmapi.bean.common.Buyer;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.bean.notification.request.NotificationRequest;
import com.payline.pmapi.bean.payment.*;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.paymentform.request.PaymentFormConfigurationRequest;
import com.payline.pmapi.bean.paymentform.request.PaymentFormLogoRequest;
import com.payline.pmapi.bean.refund.request.RefundRequest;
import com.payline.pmapi.bean.reset.request.ResetRequest;
import org.mockito.internal.util.reflection.FieldSetter;

import java.math.BigInteger;
import java.util.*;

public class MockUtils {
    private static String TRANSACTIONID = "123456789012345678901";
    private static String PARTNER_TRANSACTIONID = "098765432109876543210";

    public static final String STATUS_CODE = "STATUS_CODE";

    public static final String templateCheckStatusRequest = "<checkStatus xmlns=\"urn:PPEWShopServiceV3\">\n" +
            "  <checkStatusIn>\n" +
            "    <merchantInformation>\n" +
            "      <merchandCode>1212121212</merchandCode>\n" +
            "      <distributorNumber>2323232323</distributorNumber>\n" +
            "      <countryCode>FRA</countryCode>\n" +
            "    </merchantInformation>\n" +
            "    <transactionId>1234567890</transactionId>\n" +
            "  </checkStatusIn>\n" +
            "</checkStatus>\n";

    public static final String templateInitDossierRequest = "<initDossier xmlns=\"urn:PPEWShopServiceV3\">\n" +
            "  <initDossierIn>\n" +
            "    <merchantInformation>\n" +
            "      <merchandCode>1212121212</merchandCode>\n" +
            "      <distributorNumber>2323232323</distributorNumber>\n" +
            "      <countryCode>FRA</countryCode>\n" +
            "    </merchantInformation>\n" +
            "    <merchantConfiguration>\n" +
            "      <guarPushUrl>urlPush</guarPushUrl>\n" +
            "      <guarBackUrl>urlBack</guarBackUrl>\n" +
            "    </merchantConfiguration>\n" +
            "    <customerInformation>\n" +
            "      <title>MR</title>\n" +
            "      <customerLanguage>FR</customerLanguage>\n" +
            "      <firstName>Test</firstName>\n" +
            "      <name>Test</name>\n" +
            "      <birthDate>1992-08-14</birthDate>\n" +
            "      <email>test.test@cetelem.fr</email>\n" +
            "      <addressLine1>25 Elysées la Défense</addressLine1>\n" +
            "      <addressLine2>Apt 20</addressLine2>\n" +
            "      <city>La défense</city>\n" +
            "      <postCode>92000</postCode>\n" +
            "      <cellPhoneNumber>0172757512</cellPhoneNumber>\n" +
            "      <privatePhoneNumber/>\n" +
            "      <professionalPhoneNumber/>\n" +
            "    </customerInformation>\n" +
            "    <orderInformation>\n" +
            "      <goodsCode>616</goodsCode>\n" +
            "      <price>1000</price>\n" +
            "      <financialProductType>CLA</financialProductType>\n" +
            "    </orderInformation>\n" +
            "    <orderReference/>\n" +
            "  </initDossierIn>\n" +
            "</initDossier>\n";

    public static final String templateInitDossierResponse = "<initDossierResponse xmlns=\"urn:PPEWShopServiceV3\">" +
            "<initDossierOut>" +
            "<transactionId>1234567890</transactionId>" +
            "<redirectionUrl>http://redirectionUrl.com</redirectionUrl>" +
            "<warning>" +
            "<warningCode>13008</warningCode>" +
            "<warningDescription>warningDescription</warningDescription>" +
            "</warning>" +
            "</initDossierOut>" +
            "</initDossierResponse>";

    public static final String templateCheckStatusResponse = "<checkStatusResponse xmlns=\"urn:PPEWShopServiceV3\">\n" +
            "    <checkStatusOut>\n" +
            "        <transactionId>1234567890</transactionId>\n" +
            "        <merchandOrderReference>\n" +
            "            <merchandOrderId>32552564</merchandOrderId>\n" +
            "        </merchandOrderReference>\n" +
            "        <statusCode>STATUS_CODE</statusCode>\n" +
            "        <redirectionUrl>http://redirectionUrl.com</redirectionUrl>\n" +
            "        <creditAuthorizationNumber>34600015</creditAuthorizationNumber>\n" +
            "    </checkStatusOut>\n" +
            "</checkStatusResponse>";

    public static final String templateResponseError = "<axis2ns1:PPEWShopServiceException xmlns:axis2ns1=\"urn:PPEWShopServiceV3\">" +
            "<axis2ns1:errorCode>ERROR_CODE</axis2ns1:errorCode>" +
            "<axis2ns1:errorDescription>ERROR_DESCRIPTION</axis2ns1:errorDescription>" +
            "</axis2ns1:PPEWShopServiceException>";

    /**------------------------------------------------------------------------------------------------------------------*/

    /**
     * Generate a valid {@link Environment}.
     */
    public static Environment anEnvironment() {
        return new Environment("http://notificationURL.com",
                "http://redirectionURL.com",
                "http://redirectionCancelURL.com",
                true);
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    /**
     * Generate a valid {@link PartnerConfiguration}.
     */
    public static PartnerConfiguration aPartnerConfiguration() {
        Map<String, String> partnerConfigurationMap = new HashMap<>();
        partnerConfigurationMap.put(Constants.PartnerConfigurationKeys.URL, "https://recette-webpartners-cetelem-net.neuges.org/PPEWShop/services/PPEWShopServiceV3");
        Map<String, String> sensitiveConfigurationMap = new HashMap<>();
        return new PartnerConfiguration(partnerConfigurationMap, sensitiveConfigurationMap);
    }
    /**------------------------------------------------------------------------------------------------------------------*/


    /**
     * Generate a valid {@link PaymentFormConfigurationRequest}.
     */
    public static PaymentFormConfigurationRequest aPaymentFormConfigurationRequest() {
        return aPaymentFormConfigurationRequestBuilder().build();
    }

    /**
     * Generate a builder for a valid {@link PaymentFormConfigurationRequest}.
     * This way, some attributes may be overridden to match specific test needs.
     */
    public static PaymentFormConfigurationRequest.PaymentFormConfigurationRequestBuilder aPaymentFormConfigurationRequestBuilder() {
        return PaymentFormConfigurationRequest.PaymentFormConfigurationRequestBuilder.aPaymentFormConfigurationRequest()
                .withAmount(aPaylineAmount())
                .withBuyer(aBuyer())
                .withContractConfiguration(aContractConfiguration())
                .withEnvironment(anEnvironment())
                .withLocale(Locale.FRANCE)
                .withOrder(aPaylineOrder())
                .withPartnerConfiguration(aPartnerConfiguration());
    }

    /**
     * Generate a valid {@link PaymentFormLogoRequest}.
     */
    public static PaymentFormLogoRequest aPaymentFormLogoRequest() {
        return PaymentFormLogoRequest.PaymentFormLogoRequestBuilder.aPaymentFormLogoRequest()
                .withContractConfiguration(aContractConfiguration())
                .withEnvironment(anEnvironment())
                .withPartnerConfiguration(aPartnerConfiguration())
                .withLocale(Locale.getDefault())
                .build();
    }

    /**
     * Generate a valid, but not complete, {@link Order}
     */
    public static Order aPaylineOrder() {
        List<Order.OrderItem> items = new ArrayList<>();

        items.add(Order.OrderItem.OrderItemBuilder
                .anOrderItem()
                .withReference("foo")
                .withAmount(aPaylineAmount())
                .withQuantity((long) 1)
                .withCategory("1") // Informatique
                .build());

        return Order.OrderBuilder.anOrder()
                .withDate(new Date())
                .withAmount(aPaylineAmount())
                .withItems(items)
                .withReference("ref-20191105153749")
                .build();
    }

    /**
     * Generate a valid Payline Amount.
     */
    public static com.payline.pmapi.bean.common.Amount aPaylineAmount() {
        return aPaylineAmount(200000);
    }

    public static com.payline.pmapi.bean.common.Amount aPaylineAmount(int amount) {
        return new com.payline.pmapi.bean.common.Amount(BigInteger.valueOf(amount), Currency.getInstance("EUR"));
    }

    /**
     * @return a valid user agent.
     */
    public static String aUserAgent() {
        return "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:67.0) Gecko/20100101 Firefox/67.0";
    }

    /**
     * Generate a valid {@link Browser}.
     */
    public static Browser aBrowser() {
        return Browser.BrowserBuilder.aBrowser()
                .withLocale(Locale.getDefault())
                .withIp("192.168.0.1")
                .withUserAgent(aUserAgent())
                .build();
    }

    /**
     * Generate a valid {@link Buyer}.
     */
    public static Buyer aBuyer() {
        return Buyer.BuyerBuilder.aBuyer()
                .withFullName(new Buyer.FullName("Marie", "Durand", "1"))
                .withBirthday(new Date())
                .withAddresses(addresses())
                .withPhoneNumbers(phoneNumbers())
                .withEmail("foo@bar.baz")
                .build();
    }

    public static Map<Buyer.AddressType, Buyer.Address> addresses(){
        Map<Buyer.AddressType, Buyer.Address> addresses = new HashMap<>();
        addresses.put(Buyer.AddressType.BILLING, anAddress());
        addresses.put(Buyer.AddressType.DELIVERY, anAddress());

        return addresses;
    }

    public static Buyer.Address anAddress(){
        return Buyer.Address.AddressBuilder
                .anAddress()
                .withStreet1("street1")
                .withStreet2("street2")
                .withCity("City")
                .withZipCode("75000")
                .withState("France")
                .build();
    }

    public static Map<Buyer.PhoneNumberType, String> phoneNumbers(){
        Map<Buyer.PhoneNumberType, String> phoneNumbers = new HashMap<>();
        phoneNumbers.put(Buyer.PhoneNumberType.HOME, "0612345678");
        phoneNumbers.put(Buyer.PhoneNumberType.WORK, "0712345678");
        phoneNumbers.put(Buyer.PhoneNumberType.CELLULAR, "0612345678");
        phoneNumbers.put(Buyer.PhoneNumberType.BILLING, "0612345678");

        return phoneNumbers;
    }

    /**
     * Generate a valid {@link PaymentFormContext}.
     */
    public static PaymentFormContext aPaymentFormContext() {
        Map<String, String> paymentFormParameter = new HashMap<>();

        return PaymentFormContext.PaymentFormContextBuilder.aPaymentFormContext()
                .withPaymentFormParameter(paymentFormParameter)
                .withSensitivePaymentFormParameter(new HashMap<>())
                .build();
    }

    /**------------------------------------------------------------------------------------------------------------------*/
    /**
     * Generate a valid {@link ContractParametersCheckRequest}.
     */
    public static ContractParametersCheckRequest aContractParametersCheckRequest() {
        return aContractParametersCheckRequestBuilder().build();
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    /**
     * Generate a builder for a valid {@link ContractParametersCheckRequest}.
     * This way, some attributes may be overridden to match specific test needs.
     */
    public static ContractParametersCheckRequest.CheckRequestBuilder aContractParametersCheckRequestBuilder() {
        return ContractParametersCheckRequest.CheckRequestBuilder.aCheckRequest()
                .withAccountInfo(anAccountInfo())
                .withContractConfiguration(aContractConfiguration())
                .withEnvironment(anEnvironment())
                .withLocale(Locale.getDefault())
                .withPartnerConfiguration(aPartnerConfiguration());
    }

    /**
     * Generate a valid {@link PaymentRequest}.
     */
    public static PaymentRequest aPaylinePaymentRequest() {
        return aPaylinePaymentRequestBuilder().build();
    }

    /**
     * Generate a builder for a valid {@link PaymentRequest}.
     * This way, some attributes may be overridden to match specific test needs.
     */
    public static PaymentRequest.Builder aPaylinePaymentRequestBuilder() {
        return PaymentRequest.builder()
                .withAmount(aPaylineAmount())
                .withBrowser(aBrowser())
                .withBuyer(aBuyer())
                .withCaptureNow(true)
                .withContractConfiguration(aContractConfiguration())
                .withEnvironment(anEnvironment())
                .withLocale(Locale.getDefault())
                .withOrder(aPaylineOrder())
                .withPartnerConfiguration(aPartnerConfiguration())
                .withPaymentFormContext(aPaymentFormContext())
                .withSoftDescriptor("softDescriptor")
                .withTransactionId(TRANSACTIONID);
    }

    public static RefundRequest aPaylineRefundRequest() {
        return aPaylineRefundRequestBuilder().build();
    }

    public static RefundRequest.RefundRequestBuilder aPaylineRefundRequestBuilder() {
        return RefundRequest.RefundRequestBuilder.aRefundRequest()
                .withAmount(aPaylineAmount())
                .withOrder(aPaylineOrder())
                .withBuyer(aBuyer())
                .withContractConfiguration(aContractConfiguration())
                .withEnvironment(anEnvironment())
                .withTransactionId(TRANSACTIONID)
                .withPartnerTransactionId(PARTNER_TRANSACTIONID)
                .withPartnerConfiguration(aPartnerConfiguration());
    }


    public static ResetRequest aPaylineResetRequest() {
        return aPaylineResetRequestBuilder().build();
    }

    public static ResetRequest.ResetRequestBuilder aPaylineResetRequestBuilder() {
        return ResetRequest.ResetRequestBuilder.aResetRequest()
                .withAmount(aPaylineAmount())
                .withOrder(aPaylineOrder())
                .withBuyer(aBuyer())
                .withContractConfiguration(aContractConfiguration())
                .withEnvironment(anEnvironment())
                .withTransactionId(TRANSACTIONID)
                .withPartnerTransactionId(PARTNER_TRANSACTIONID)
                .withPartnerConfiguration(aPartnerConfiguration());
    }

    public static NotificationRequest aPaylineNotificationRequest() {
        return aPaylineNotificationRequestBuilder().build();
    }

    public static NotificationRequest.NotificationRequestBuilder aPaylineNotificationRequestBuilder() {
        return NotificationRequest.NotificationRequestBuilder.aNotificationRequest()
                .withHeaderInfos(new HashMap<>())
                .withPathInfo("thisIsAPath")
                .withHttpMethod("POST")
                .withContractConfiguration(aContractConfiguration())
                .withPartnerConfiguration(aPartnerConfiguration())
                .withEnvironment(anEnvironment());
    }


    /**------------------------------------------------------------------------------------------------------------------*/
    /**
     * Generate a valid accountInfo, an attribute of a {@link ContractParametersCheckRequest} instance.
     */
    public static Map<String, String> anAccountInfo() {
        return anAccountInfo(aContractConfiguration());
    }
    /**------------------------------------------------------------------------------------------------------------------*/

    /**
     * Generate a valid accountInfo, an attribute of a {@link ContractParametersCheckRequest} instance,
     * from the given {@link ContractConfiguration}.
     *
     * @param contractConfiguration The model object from which the properties will be copied
     */
    public static Map<String, String> anAccountInfo(ContractConfiguration contractConfiguration) {
        Map<String, String> accountInfo = new HashMap<>();
        for (Map.Entry<String, ContractProperty> entry : contractConfiguration.getContractProperties().entrySet()) {
            accountInfo.put(entry.getKey(), entry.getValue().getValue());
        }
        return accountInfo;
    }

    /**
     * Generate a valid {@link ContractConfiguration}.
     */
    public static ContractConfiguration aContractConfiguration() {
        Map<String, ContractProperty> contractProperties = new HashMap<>();
        contractProperties.put(Constants.ContractConfigurationKeys.MERCHANT_CODE, new ContractProperty("3550937738"));
        contractProperties.put(Constants.ContractConfigurationKeys.DISTRIBUTOR_NUMBER, new ContractProperty("1000828996"));
        contractProperties.put(Constants.ContractConfigurationKeys.COUNTRY_CODE, new ContractProperty("FRA"));

        return new ContractConfiguration("PPEWShop", contractProperties);
    }


    /**
     * Moch a StringResponse with the given elements.
     *
     * @param statusCode    The HTTP status code (ex: 200, 403)
     * @param statusMessage The HTTP status message (ex: "OK", "Forbidden")
     * @param content       The response content as a string
     * @param headers       The response headers
     * @return A mocked StringResponse
     */
    public static StringResponse mockStringResponse(int statusCode, String statusMessage, String content, Map<String, String> headers) {
        StringResponse response = new StringResponse();

        try {
            if (content != null && !content.isEmpty()) {
                FieldSetter.setField(response, StringResponse.class.getDeclaredField("content"), content);
            }
            if (headers != null && headers.size() > 0) {
                FieldSetter.setField(response, StringResponse.class.getDeclaredField("headers"), headers);
            }
            if (statusCode >= 100 && statusCode < 600) {
                FieldSetter.setField(response, StringResponse.class.getDeclaredField("statusCode"), statusCode);
            }
            if (statusMessage != null && !statusMessage.isEmpty()) {
                FieldSetter.setField(response, StringResponse.class.getDeclaredField("statusMessage"), statusMessage);
            }
        } catch (NoSuchFieldException e) {
            // This would happen in a testing context: spare the exception throw, the test case will probably fail anyway
            return null;
        }

        return response;
    }

    /*
        Method specific to PPEWShop
     */


}
