package com.payline.payment.ppewshop.bean.response;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.payline.payment.ppewshop.exception.InvalidDataException;
import com.payline.payment.ppewshop.utils.PluginUtils;
import com.payline.pmapi.bean.common.FailureCause;

import java.io.IOException;

public class PpewShopResponseKO {
    private static final XmlMapper xmlMapper = new XmlMapper();

    private String errorCode;
    private String errorDescription;

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public static PpewShopResponseKO fromXml(String xml) {

        try {
            return xmlMapper.readValue(xml, PpewShopResponseKO.class);
        } catch (IOException e) {
            throw new InvalidDataException("Unable to parse XML ResponseKO", e);
        }
    }

    public FailureCause getFailureCauseFromErrorCode() {
        if (PluginUtils.isEmpty(this.errorCode)){
            return FailureCause.PARTNER_UNKNOWN_ERROR;
        }

        FailureCause cause;
        switch (this.errorCode) {
            case ErrorCode.CODE_11001:
            case ErrorCode.CODE_11002:
            case ErrorCode.CODE_11008:
            case ErrorCode.CODE_11009:
            case ErrorCode.CODE_11999:
            case ErrorCode.CODE_21001:
            case ErrorCode.CODE_21002:
            case ErrorCode.CODE_21003:
            case ErrorCode.CODE_21009:
            case ErrorCode.CODE_21999:
            case ErrorCode.CODE_21004:
                cause = FailureCause.PAYMENT_PARTNER_ERROR;
                break;
            case ErrorCode.CODE_12001:
            case ErrorCode.CODE_12002:
            case ErrorCode.CODE_12003:
            case ErrorCode.CODE_12004:
            case ErrorCode.CODE_12006:
            case ErrorCode.CODE_12205:
            case ErrorCode.CODE_22001:
            case ErrorCode.CODE_22002:
            case ErrorCode.CODE_22003:
                cause = FailureCause.INVALID_DATA;
                break;
            case ErrorCode.CODE_12207:
                cause = FailureCause.INVALID_FIELD_FORMAT;
                break;
            case ErrorCode.CODE_12301:
                cause = FailureCause.REFUSED;
                break;
            default:
                cause = FailureCause.PARTNER_UNKNOWN_ERROR;
                break;
        }
        return cause;
    }

    public static class ErrorCode {
        private ErrorCode() {
        }

        public static final String CODE_11001 = "11001";
        public static final String CODE_11002 = "11002";
        public static final String CODE_11008 = "11008";
        public static final String CODE_11009 = "11009";
        public static final String CODE_11999 = "11999";
        public static final String CODE_21001 = "21001";
        public static final String CODE_21002 = "21002";
        public static final String CODE_21003 = "21003";
        public static final String CODE_21009 = "21009";
        public static final String CODE_21999 = "21999";
        public static final String CODE_21004 = "21004";
        public static final String CODE_12001 = "12001";
        public static final String CODE_12002 = "12002";
        public static final String CODE_12003 = "12003";
        public static final String CODE_12004 = "12004";
        public static final String CODE_12006 = "12006";
        public static final String CODE_12205 = "12205";
        public static final String CODE_22001 = "22001";
        public static final String CODE_22002 = "22002";
        public static final String CODE_22003 = "22003";
        public static final String CODE_12207 = "12207";
        public static final String CODE_12301 = "12301";
    }

}
