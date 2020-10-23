package com.payline.payment.ppewshop.bean.common;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class CheckStatusIn {
    @JacksonXmlProperty(namespace = "urn:PPEWShopServiceV3")
    private MerchantInformation merchantInformation;
    @JacksonXmlProperty(namespace = "urn:PPEWShopServiceV3")
    private String transactionId;

    private CheckStatusIn(Builder builder) {
        this.transactionId = builder.transactionId;
        this.merchantInformation = builder.merchantInformation;
    }

    public MerchantInformation getMerchantInformation() {
        return merchantInformation;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public static class Builder {
        private MerchantInformation merchantInformation;
        private String transactionId;

        public static Builder aCheckStatusIn() {
            return new Builder();
        }

        public Builder withTransactionId(String transactionId) {
            this.transactionId = transactionId;
            return this;
        }

        public Builder withMerchantInformation(MerchantInformation information) {
            this.merchantInformation = information;
            return this;
        }

        public CheckStatusIn build() {
            return new CheckStatusIn(this);
        }
    }
}
