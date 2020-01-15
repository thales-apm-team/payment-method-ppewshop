package com.payline.payment.ppewshop.bean.response;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.payline.pmapi.bean.common.FailureCause;

import java.io.IOException;

public class PpewShopResponseKO {
    private String errorCode;
    private String errorDescription;

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public static PpewShopResponseKO fromXml(String xml) throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        return xmlMapper.readValue(xml, PpewShopResponseKO.class);
    }

    public FailureCause getFailureCauseFromErrorCode() {
        FailureCause cause;
        switch (this.errorCode) {
            case ErrorCode._11001:
            case ErrorCode._11002:
            case ErrorCode._11008:
            case ErrorCode._11009:
            case ErrorCode._11999:
            case ErrorCode._21001:
            case ErrorCode._21002:
            case ErrorCode._21003:
            case ErrorCode._21009:
            case ErrorCode._21999:
            case ErrorCode._21004:
                cause = FailureCause.PAYMENT_PARTNER_ERROR;
                break;
            case ErrorCode._12001:
            case ErrorCode._12002:
            case ErrorCode._12003:
            case ErrorCode._12004:
            case ErrorCode._12006:
            case ErrorCode._12205:
            case ErrorCode._22001:
            case ErrorCode._22002:
            case ErrorCode._22003:
                cause = FailureCause.INVALID_DATA;
                break;
            case ErrorCode._12207:
                cause = FailureCause.INVALID_FIELD_FORMAT;
                break;
            case ErrorCode._12301:
                cause = FailureCause.REFUSED;
                break;
            default:
                cause = FailureCause.PARTNER_UNKNOWN_ERROR;
                break;
        }
        return cause;
    }

    public static class ErrorCode {
        public static final String _11001 = "11001";
        public static final String _11002 = "11002";
        public static final String _11008 = "11008";
        public static final String _11009 = "11009";
        public static final String _11999 = "11999";
        public static final String _21001 = "21001";
        public static final String _21002 = "21002";
        public static final String _21003 = "21003";
        public static final String _21009 = "21009";
        public static final String _21999 = "21999";
        public static final String _21004 = "21004";
        public static final String _12001 = "12001";
        public static final String _12002 = "12002";
        public static final String _12003 = "12003";
        public static final String _12004 = "12004";
        public static final String _12006 = "12006";
        public static final String _12205 = "12205";
        public static final String _22001 = "22001";
        public static final String _22002 = "22002";
        public static final String _22003 = "22003";
        public static final String _12207 = "12207";
        public static final String _12301 = "12301";
    }

}
