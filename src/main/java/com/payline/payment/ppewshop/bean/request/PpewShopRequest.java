package com.payline.payment.ppewshop.bean.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.payline.payment.ppewshop.exception.InvalidDataException;

public class PpewShopRequest {
    private ObjectMapper mapper;


    public PpewShopRequest() {
        JacksonXmlModule xmlModule = new JacksonXmlModule();
        xmlModule.setDefaultUseWrapper(false);
        mapper = new XmlMapper(xmlModule);
    }

    public String toXml() {
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new InvalidDataException("Unable to create XML", e);
        }
    }
}
