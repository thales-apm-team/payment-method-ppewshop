package com.payline.payment.ppewshop.bean.common;

public class InitDossierOut {
    private String transactionId;
    private String redirectionUrl;
    private Warning warning;

    public String getTransactionId() {
        return transactionId;
    }

    public String getRedirectionUrl() {
        return redirectionUrl;
    }

    public Warning getWarning() {
        return warning;
    }
}
