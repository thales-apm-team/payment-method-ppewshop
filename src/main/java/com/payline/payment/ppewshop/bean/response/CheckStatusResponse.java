package com.payline.payment.ppewshop.bean.response;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.payline.payment.ppewshop.bean.common.CheckStatusOut;
import com.payline.payment.ppewshop.exception.InvalidDataException;

import java.io.IOException;

public class CheckStatusResponse {
    private static transient XmlMapper xmlMapper = new XmlMapper();

    private CheckStatusOut checkStatusOut;

    public CheckStatusResponse() {
        this.checkStatusOut = new CheckStatusOut();
    }

    public CheckStatusOut getCheckStatusOut() {
        return checkStatusOut;
    }

    public static CheckStatusResponse fromXml(String xml) {
        try {
            return xmlMapper.readValue(xml, CheckStatusResponse.class);
        } catch (IOException e) {
            throw new InvalidDataException("Unable to parse XML CheckStatusResponse", e);
        }
    }
}
