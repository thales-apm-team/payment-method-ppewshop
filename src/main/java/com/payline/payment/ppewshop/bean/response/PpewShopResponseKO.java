package com.payline.payment.ppewshop.bean.response;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.payline.payment.ppewshop.exception.InvalidDataException;

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

    public enum ErrorCode {
        CODE_11001("11001"),    // The service encountered an exception while trying to store the order information.
        CODE_11002("11002"),    // The service encountered an exception while trying to store the order information.
        CODE_11999("11999"),    // Fatal error while querying web-service.
        CODE_21001("21001"),    // The service encountered an exception while trying to retrieve the order information.
        CODE_21002("21002"),    // The service was not able to retrieve the status of this loan application.
        CODE_21003("21003"),    // The service encountered an exception while trying to retrieve the order information.
        CODE_21004("21004"),    // The service encountered an exception while trying to call WS LoanOrderManagement to retrieve the order information.
        CODE_21999("21999"),    // Fatal error while querying web-service.
        CODE_12001("12001"),    // Some of the required parameters are invalid : <company code>, <country code>, <amount>, <material code>.
        CODE_12002("12002"),    // Unable to identify the company.
        CODE_12003("12003"),    // Unauthorized material code.
        CODE_12004("12004"),    // Invalid format for the desired guarantee. Authorized values are 0, 1, 2 and 3.
        CODE_12006("12006"),    // Unable to identify the company for this entrance point
        CODE_12202("12202"),    // The "bankCardType" field is missing inside of the "OrderInformation" dataset because the fnancial product selected requires this information
        CODE_12205("12205"),    // The “bankCardType” field should be empty if financialProductType is “CLA”.
        CODE_12206("12206"),    // The value <Valeur recue> is unexpected for the field "bankCardType"
        CODE_22001("22001"),    // Some of the required parameters are invalid : <company code>, <country code>, <distributor number>, <cetelem transaction id>.
        CODE_22002("22002"),    // Unable to identify the company.
        CODE_22003("22003"),    // There is no valid order information with this identifier.
        CODE_12207("12207");    // Field <nom du champ> received from client has exceeded maximum size (<size>)

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
