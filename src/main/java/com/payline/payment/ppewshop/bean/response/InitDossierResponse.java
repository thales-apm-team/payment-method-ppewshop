package com.payline.payment.ppewshop.bean.response;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.payline.payment.ppewshop.bean.common.InitDossierOut;
import com.payline.payment.ppewshop.exception.InvalidDataException;

import java.io.IOException;

@JacksonXmlRootElement(namespace = "urn:PPEWShopServiceV3", localName = "initDossierResponse")
public class InitDossierResponse {
    private static final XmlMapper xmlMapper = new XmlMapper();

    private InitDossierOut initDossierOut;

    public InitDossierOut getInitDossierOut() {
        return initDossierOut;
    }

    public static InitDossierResponse fromXml(String xml) {
        try {
            return xmlMapper.readValue(xml, InitDossierResponse.class);
        } catch (IOException e) {
            throw new InvalidDataException("Unable to parse XML InitDossierResponse", e);
        }
    }


}
