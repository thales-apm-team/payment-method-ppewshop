package com.payline.payment.ppewshop.bean.common;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.payline.payment.ppewshop.utils.PluginUtils;

@JacksonXmlRootElement(localName = "merchandOrderReference")
public class MerchantOrderReference {
    @JacksonXmlProperty(localName = "merchandOrderId", namespace = "urn:PPEWShopServiceV3")
    private String merchantOrderId;

    @JacksonXmlProperty(localName = "merchandRef", namespace = "urn:PPEWShopServiceV3")
    private String merchantRef;

    public MerchantOrderReference() {
        // Empty constructor needed by Jackson for the XML serialization
    }

    private MerchantOrderReference(Builder builder) {
        this.merchantOrderId = builder.merchantOrderId;
        this.merchantRef = builder.merchantRef;
    }

    public String getMerchantOrderId() {
        return merchantOrderId;
    }

    public static class Builder {
        private static final int MERCHANT_ORDER_ID_LENGTH = 13;

        private String merchantOrderId;
        private String merchantRef;

        private Builder() {
        }

        public static Builder aMerchantOrderReference() {
            return new Builder();
        }

        public Builder withMerchantOrderId(String merchantOrderId) {
            this.merchantOrderId = PluginUtils.truncate(merchantOrderId, MERCHANT_ORDER_ID_LENGTH);
            return this;
        }

        public Builder withMerchantRef(String merchantRef) {
            this.merchantRef = merchantRef;
            return this;
        }

        public MerchantOrderReference build() {
            return new MerchantOrderReference(this);
        }
    }
}
