package com.payline.payment.ppewshop.bean.common;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class OrderInformation {
    public static final String CLA = "CLA";

    @JacksonXmlProperty(namespace = "urn:PPEWShopServiceV3")
    private String goodsCode;
    @JacksonXmlProperty(namespace = "urn:PPEWShopServiceV3")
    private String price;
    @JacksonXmlProperty(namespace = "urn:PPEWShopServiceV3")
    private String financialProductType;

    private OrderInformation(Builder builder) {
        this.goodsCode = builder.goodsCode;
        this.price = builder.price;
        this.financialProductType = builder.financialProductType;
    }

    public String getGoodsCode() {
        return goodsCode;
    }

    public String getPrice() {
        return price;
    }

    public String getFinancialProductType() {
        return financialProductType;
    }

    public static class Builder {
        private String goodsCode;
        private String price;
        private String financialProductType;

        public static Builder anOrderInformation() {
            return new Builder();
        }

        public Builder withGoodsCode(String goodsCode) {
            this.goodsCode = goodsCode;
            return this;
        }

        public Builder withPrice(String price) {
            this.price = price;
            return this;
        }

        public Builder withFinancialProductType(String productType) {
            this.financialProductType = productType;
            return this;
        }

        public OrderInformation build() {
            return new OrderInformation(this);
        }

    }
}
