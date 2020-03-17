package com.payline.payment.ppewshop.bean.response;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.payline.payment.ppewshop.exception.InvalidDataException;
import com.payline.payment.ppewshop.utils.PluginUtils;
import com.payline.pmapi.bean.common.FailureCause;

import java.io.IOException;

public class PpewShopResponseKO {
    private static final XmlMapper xmlMapper = new XmlMapper();

    private ErrorCode errorCode;
    private String errorDescription;

    public ErrorCode getErrorCode() {
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

        FailureCause cause;
        switch (this.errorCode) {
            case CODE_11001:
            case CODE_11002:
            case CODE_11008:
            case CODE_11009:
            case CODE_11999:
            case CODE_21001:
            case CODE_21002:
            case CODE_21003:
            case CODE_21009:
            case CODE_21999:
            case CODE_21004:
                cause = FailureCause.PAYMENT_PARTNER_ERROR;
                break;
            case CODE_12001:
            case CODE_12002:
            case CODE_12003:
            case CODE_12004:
            case CODE_12006:
            case CODE_12205:
            case CODE_22001:
            case CODE_22002:
            case CODE_22003:
                cause = FailureCause.INVALID_DATA;
                break;
            case CODE_12207:
                cause = FailureCause.INVALID_FIELD_FORMAT;
                break;
            case CODE_12301:
                cause = FailureCause.REFUSED;
                break;
            default:
                cause = FailureCause.PARTNER_UNKNOWN_ERROR;
                break;
        }
        return cause;
    }

    public enum ErrorCode {
        CODE_11001("11001"),
        CODE_11002("11002"),
        CODE_11008("11008"),
        CODE_11009("11009"),
        CODE_11999("11999"),
        CODE_21001("21001"),
        CODE_21002("21002"),
        CODE_21003("21003"),
        CODE_21009("21009"),
        CODE_21999("21999"),
        CODE_21004("21004"),
        CODE_12001("12001"),
        CODE_12002("12002"),
        CODE_12003("12003"),
        CODE_12004("12004"),
        CODE_12006("12006"),
        CODE_12205("12205"),
        CODE_22001("22001"),
        CODE_22002("22002"),
        CODE_22003("22003"),
        CODE_12207("12207"),
        CODE_12301("12301");

        public final String code;

        @JsonValue
        public String getCode() {
            return code;
        }

        ErrorCode(String code) {
            this.code = code;
        }
    }

}
