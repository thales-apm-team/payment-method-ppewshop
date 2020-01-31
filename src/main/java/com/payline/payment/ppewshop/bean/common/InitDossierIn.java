package com.payline.payment.ppewshop.bean.common;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class InitDossierIn {
    @JacksonXmlProperty(namespace = "urn:PPEWShopServiceV3")
    private MerchantInformation merchantInformation;
    @JacksonXmlProperty(namespace = "urn:PPEWShopServiceV3")
    private MerchantConfiguration merchantConfiguration;
    @JacksonXmlProperty(namespace = "urn:PPEWShopServiceV3")
    private CustomerInformation customerInformation;
    @JacksonXmlProperty(namespace = "urn:PPEWShopServiceV3")
    private OrderInformation orderInformation;
    @JacksonXmlProperty(namespace = "urn:PPEWShopServiceV3")
    private MerchantOrderReference orderReference;

    public MerchantInformation getMerchantInformation() {
        return merchantInformation;
    }

    public void setMerchantInformation(MerchantInformation merchantInformation) {
        this.merchantInformation = merchantInformation;
    }

    public MerchantConfiguration getMerchantConfiguration() {
        return merchantConfiguration;
    }

    public void setMerchantConfiguration(MerchantConfiguration merchantConfiguration) {
        this.merchantConfiguration = merchantConfiguration;
    }

    public CustomerInformation getCustomerInformation() {
        return customerInformation;
    }

    public void setCustomerInformation(CustomerInformation customerInformation) {
        this.customerInformation = customerInformation;
    }

    public OrderInformation getOrderInformation() {
        return orderInformation;
    }

    public void setOrderInformation(OrderInformation orderInformation) {
        this.orderInformation = orderInformation;
    }

    public MerchantOrderReference getOrderReference() {
        return orderReference;
    }

    public void setOrderReference(MerchantOrderReference orderReference) {
        this.orderReference = orderReference;
    }
}
