package com.payline.payment.ppewshop.utils;

/**
 * Support for constants used everywhere in the plugin sources.
 */
public class Constants {

    /**
     * Keys for the entries in ContractConfiguration map.
     */
    public static class ContractConfigurationKeys {
        public static final String MERCHANT_CODE = "merchantCode";
        public static final String DISTRIBUTOR_NUMBER = "distributorNumber";
        public static final String COUNTRY_CODE = "countryCode";

        /* Static utility class : no need to instantiate it (Sonar bug fix) */
        private ContractConfigurationKeys() {
        }
    }

    /**
     * Keys for the entries in PartnerConfiguration maps.
     */
    public static class PartnerConfigurationKeys {


        public static final String URL = "URL";

        /* Static utility class : no need to instantiate it (Sonar bug fix) */
        private PartnerConfigurationKeys() {
        }
    }


    /**
     * Keys for the entries in RequestContext data.
     */
    public static class RequestContextKeys {
        public static final String PARTNER_TRANSACTION_ID = "partnerTransactionId";

        /* Static utility class : no need to instantiate it (Sonar bug fix) */
        private RequestContextKeys() {
        }
    }

    // TODO(code-review-spl): A quoi sert cette classe ?
    /**
     * Keys for the entries in PaymentFormContext data
     */
    public static class PaymentFormKeys {

        private PaymentFormKeys() {
        }
    }

    /* Static utility class : no need to instantiate it (Sonar bug fix) */
    private Constants() {
    }

}
