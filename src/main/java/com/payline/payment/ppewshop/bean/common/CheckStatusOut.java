package com.payline.payment.ppewshop.bean.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"transactionId", "merchandOrderReference", "statusCode", "creditAuthorizationNumber"})
public class CheckStatusOut {
    private String transactionId;

    private String redirectionURL;

    @JsonProperty("merchandOrderReference")
    private MerchantOrderReference merchantOrderReference;
    private String statusCode;
    private String creditAuthorizationNumber;

    public String getTransactionId() {
        return transactionId;
    }

    public String getRedirectionURL(){
        return this.redirectionURL;
    }

    public MerchantOrderReference getMerchantOrderReference() {
        return merchantOrderReference;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public String getCreditAuthorizationNumber() {
        return creditAuthorizationNumber;
    }

    public static class StatusCode{
        public static final String A = "A";
        public static final String E = "E";
        public static final String I = "I";
        public static final String R = "R";
        public static final String C = "C";
    }
}
