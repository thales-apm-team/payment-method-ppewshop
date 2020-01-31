package com.payline.payment.ppewshop.bean.request;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.payline.payment.ppewshop.bean.common.CheckStatusIn;

@JacksonXmlRootElement(namespace = "urn:PPEWShopServiceV3", localName = "checkStatus")
public class CheckStatusRequest extends PpewShopRequest {
    @JacksonXmlProperty(namespace = "urn:PPEWShopServiceV3")
    private CheckStatusIn checkStatusIn;

    public CheckStatusRequest() {
        super();
        this.checkStatusIn = new CheckStatusIn();
    }

    public CheckStatusIn getCheckStatusIn() {
        return checkStatusIn;
    }
}


