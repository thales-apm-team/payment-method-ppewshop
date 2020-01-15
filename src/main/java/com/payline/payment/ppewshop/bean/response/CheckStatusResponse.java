package com.payline.payment.ppewshop.bean.response;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.payline.payment.ppewshop.bean.common.CheckStatusOut;

import java.io.IOException;

public class CheckStatusResponse {
    private CheckStatusOut checkStatusOut;

    public CheckStatusResponse() {
        this.checkStatusOut = new CheckStatusOut();
    }

    public CheckStatusOut getCheckStatusOut() {
        return checkStatusOut;
    }

    public static CheckStatusResponse fromXml(String xml) throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        return xmlMapper.readValue(xml, CheckStatusResponse.class);
    }
}
