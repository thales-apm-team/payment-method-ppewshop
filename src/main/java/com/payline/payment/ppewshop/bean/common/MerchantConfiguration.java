package com.payline.payment.ppewshop.bean.common;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class MerchantConfiguration {
    @JacksonXmlProperty(namespace = "urn:PPEWShopServiceV3")
    private String guarPushUrl;
    @JacksonXmlProperty(namespace = "urn:PPEWShopServiceV3")
    private String guarBackUrl;

    private MerchantConfiguration(Builder builder) {
        this.guarPushUrl = builder.guardPushUrl;
        this.guarBackUrl = builder.guardBackUrl;
    }

    public String getGuarPushUrl() {
        return guarPushUrl;
    }

    public String getGuarBackUrl() {
        return guarBackUrl;
    }

    public static class Builder {
        private String guardPushUrl;
        private String guardBackUrl;

        public static Builder aMerchantConfiguration() {
            return new Builder();
        }

        public Builder withGuardPushUrl(String guardPushUrl) {
            this.guardPushUrl = guardPushUrl;
            return this;
        }

        public Builder withGuardBackUrl(String guardBackUrl) {
            this.guardBackUrl = guardBackUrl;
            return this;
        }

        public MerchantConfiguration build() {
            return new MerchantConfiguration(this);
        }
    }
}
