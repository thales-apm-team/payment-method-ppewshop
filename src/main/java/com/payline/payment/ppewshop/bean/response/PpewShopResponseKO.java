package com.payline.payment.ppewshop.bean.response;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.payline.payment.ppewshop.exception.InvalidDataException;
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
        CODE_11001("11001"),    // The service encountered an exception while trying to store the order information.
        CODE_11002("11002"),    // The service encountered an exception while trying to store the order information.
        CODE_11008("11008"),    // Error – Exchange server error
        CODE_11009("11009"),    // Error – Subsidiary error
        CODE_11999("11999"),    // Fatal error while querying web-service.
        CODE_21001("21001"),    // The service encountered an exception while trying to retrieve the order information.
        CODE_21002("21002"),    // The service was not able to retrieve the status of this loan application.
        CODE_21003("21003"),    // The service encountered an exception while trying to retrieve the order information.
        CODE_21009("21009"),    // Error – Subsidiary error
        CODE_21999("21999"),    // Fatal error while querying web-service.
        CODE_21004("21004"),    // The service encountered an exception while trying to call WS LoanOrderManagement to retrieve the order information.
        CODE_12001("12001"),    // Some of the required parameters are invalid : <company code>, <country code>, <amount>, <material code>.
        CODE_12002("12002"),    // Unable to identify the company.
        CODE_12003("12003"),    // Unauthorized material code.
        CODE_12004("12004"),    // Invalid format for the desired guarantee. Authorized values are 0, 1, 2 and 3.
        CODE_12006("12006"),    // Unable to identify the company for this entrance point
        CODE_12205("12205"),    // The “bankCardType” field should be empty if financialProductType is “CLA”.
        CODE_22001("22001"),    // Some of the required parameters are invalid : <company code>, <country code>, <distributor number>, <cetelem transaction id>.
        CODE_22002("22002"),    // Unable to identify the company.
        CODE_22003("22003"),    // There is no valid order information with this identifier.
        CODE_12207("12207"),    // Field <nom du champ> received from client has exceeded maximum size (<size>)
        CODE_12301("12301");    // Unhandlable request.

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
