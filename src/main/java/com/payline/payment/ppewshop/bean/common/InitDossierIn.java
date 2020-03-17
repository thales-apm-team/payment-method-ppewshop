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

    public InitDossierIn(Builder builder) {
        this.merchantInformation = builder.merchantInformation;
        this.merchantConfiguration = builder.merchantConfiguration;
        this.customerInformation = builder.customerInformation;
        this.orderInformation = builder.orderInformation;
        this.orderReference = builder.orderReference;
    }

    public MerchantInformation getMerchantInformation() {
        return merchantInformation;
    }

    public MerchantConfiguration getMerchantConfiguration() {
        return merchantConfiguration;
    }

    public CustomerInformation getCustomerInformation() {
        return customerInformation;
    }

    public OrderInformation getOrderInformation() {
        return orderInformation;
    }

    public MerchantOrderReference getOrderReference() {
        return orderReference;
    }

    public static class Builder{
        private MerchantInformation merchantInformation;
        private MerchantConfiguration merchantConfiguration;
        private CustomerInformation customerInformation;
        private OrderInformation orderInformation;
        private MerchantOrderReference orderReference;

        public static Builder anInitDossier(){
            return new Builder();
        }

        public Builder withMerchantInformation(MerchantInformation information){
            this.merchantInformation = information;
            return this;
        }

        public Builder withMerchantConfiguration(MerchantConfiguration configuration){
            this.merchantConfiguration = configuration;
            return this;
        }
        public Builder withCustomerInformation(CustomerInformation information){
            this.customerInformation = information;
            return this;
        }
        public Builder withOrderInformation(OrderInformation information){
            this.orderInformation = information;
            return this;
        }
        public Builder withMerchantOrderReference(MerchantOrderReference orderReference){
            this.orderReference = orderReference;
            return this;
        }

        public InitDossierIn build(){
            return new InitDossierIn(this);
        }
    }
}
