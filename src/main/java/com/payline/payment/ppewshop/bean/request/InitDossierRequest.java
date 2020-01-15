package com.payline.payment.ppewshop.bean.request;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.payline.payment.ppewshop.bean.common.InitDossierIn;

@JacksonXmlRootElement(namespace = "urn:PPEWShopServiceV3", localName = "initDossier")
public class InitDossierRequest  extends PpewShopRequest {
    @JacksonXmlProperty(namespace = "urn:PPEWShopServiceV3")
    private InitDossierIn initDossierIn;

    public InitDossierRequest(){
        super();
        this.initDossierIn = new InitDossierIn();
    }

    public InitDossierIn getInitDossierIn() {
        return initDossierIn;
    }
}
