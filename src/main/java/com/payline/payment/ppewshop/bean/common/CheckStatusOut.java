package com.payline.payment.ppewshop.bean.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"transactionId", "merchandOrderReference", "statusCode", "creditAuthorizationNumber", "redirectionUrl"})
public class CheckStatusOut {
    private String transactionId;

    @JsonProperty("merchandOrderReference")
    private MerchantOrderReference merchantOrderReference;
    private StatusCode statusCode;
    private String creditAuthorizationNumber;
    private String redirectionUrl;

    public String getTransactionId() {
        return transactionId;
    }

    public String getRedirectionUrl() {
        return this.redirectionUrl;
    }

    public MerchantOrderReference getMerchantOrderReference() {
        return merchantOrderReference;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public String getCreditAuthorizationNumber() {
        return creditAuthorizationNumber;
    }

    /**
     * A: Acceptée
     * E: à l'étude
     * I: Incomplet
     * R: Refusée
     * C: Cancel
     */
    public enum StatusCode {
        A, E, I, R, C;
    }
}
