package com.payline.payment.ppewshop.bean.response;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.payline.payment.ppewshop.bean.common.InitDossierOut;

import java.io.IOException;

@JacksonXmlRootElement(namespace = "urn:PPEWShopServiceV3", localName = "initDossierResponse")
public class InitDossierResponse {
    private InitDossierOut initDossierOut;

    public InitDossierOut getInitDossierOut() {
        return initDossierOut;
    }

    public static InitDossierResponse fromXml(String xml) throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        return xmlMapper.readValue(xml, InitDossierResponse.class);
    }


}
