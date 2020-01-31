package com.payline.payment.ppewshop.bean.common;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonPropertyOrder({"merchandCode", "distributorNumber", "countryCode"})
public class MerchantInformation {

    @JacksonXmlProperty(localName = "merchandCode", namespace = "urn:PPEWShopServiceV3")
    private String merchantCode;
    @JacksonXmlProperty(namespace = "urn:PPEWShopServiceV3")
    private String distributorNumber;
    @JacksonXmlProperty(namespace = "urn:PPEWShopServiceV3")
    private String countryCode;

    private MerchantInformation(MerchantInformation.Builder builder) {
        this.merchantCode = builder.merchantCode;
        this.distributorNumber = builder.distributorNumber;
        this.countryCode = builder.countryCode;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public String getDistributorNumber() {
        return distributorNumber;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public static class Builder {
        private String merchantCode;
        private String distributorNumber;
        private String countryCode;

        private Builder() {
        }

        public static MerchantInformation.Builder aMerchantInformation() {
            return new MerchantInformation.Builder();
        }

        public MerchantInformation.Builder withMerchantCode(String merchantCode) {
            this.merchantCode = merchantCode;
            return this;
        }

        public MerchantInformation.Builder withDistributorNumber(String distributorNumber) {
            this.distributorNumber = distributorNumber;
            return this;
        }

        public MerchantInformation.Builder withCountryCode(String countryCode) {
            this.countryCode = countryCode;
            return this;
        }

        public MerchantInformation build() {
            return new MerchantInformation(this);
        }
    }

}
