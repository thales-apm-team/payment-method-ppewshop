package com.payline.payment.ppewshop.bean.common;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class CheckStatusIn {
    @JacksonXmlProperty(namespace = "urn:PPEWShopServiceV3")
    private MerchantInformation merchantInformation;
    @JacksonXmlProperty(namespace = "urn:PPEWShopServiceV3")
    private String transactionId;

    public MerchantInformation getMerchantInformation() {
        return merchantInformation;
    }

    public void setMerchantInformation(MerchantInformation merchantInformation) {
        this.merchantInformation = merchantInformation;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}
