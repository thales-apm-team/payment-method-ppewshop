package com.payline.payment.ppewshop.bean.common;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class CustomerInformation {
    @JacksonXmlProperty(namespace = "urn:PPEWShopServiceV3")
    private String title;
    @JacksonXmlProperty(namespace = "urn:PPEWShopServiceV3")
    private String customerLanguage;
    @JacksonXmlProperty(namespace = "urn:PPEWShopServiceV3")
    private String firstName;
    @JacksonXmlProperty(namespace = "urn:PPEWShopServiceV3")
    private String name;
    @JacksonXmlProperty(namespace = "urn:PPEWShopServiceV3")
    private String birthDate;
    @JacksonXmlProperty(namespace = "urn:PPEWShopServiceV3")
    private String email;
    @JacksonXmlProperty(namespace = "urn:PPEWShopServiceV3")
    private String addressLine1;
    @JacksonXmlProperty(namespace = "urn:PPEWShopServiceV3")
    private String addressLine2;
    @JacksonXmlProperty(namespace = "urn:PPEWShopServiceV3")
    private String city;
    @JacksonXmlProperty(namespace = "urn:PPEWShopServiceV3")
    private String postCode;
    @JacksonXmlProperty(namespace = "urn:PPEWShopServiceV3")
    private String cellPhoneNumber;
    @JacksonXmlProperty(namespace = "urn:PPEWShopServiceV3")
    private String privatePhoneNumber;
    @JacksonXmlProperty(namespace = "urn:PPEWShopServiceV3")
    private String professionalPhoneNumber;

    private CustomerInformation(Builder builder) {
        this.title = builder.title;
        this.customerLanguage = builder.customerLanguage;
        this.firstName = builder.firstName;
        this.name = builder.name;
        this.birthDate = builder.birthDate;
        this.email = builder.email;
        this.addressLine1 = builder.addressLine1;
        this.addressLine2 = builder.addressLine2;
        this.city = builder.city;
        this.postCode = builder.postCode;
        this.cellPhoneNumber = builder.cellPhoneNumber;
        this.privatePhoneNumber = builder.privatePhoneNumber;
        this.professionalPhoneNumber = builder.professionalPhoneNumber;
    }

    public String getTitle() {
        return title;
    }

    public String getCustomerLanguage() {
        return customerLanguage;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getName() {
        return name;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getEmail() {
        return email;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public String getCity() {
        return city;
    }

    public String getPostCode() {
        return postCode;
    }

    public String getCellPhoneNumber() {
        return cellPhoneNumber;
    }

    public String getPrivatePhoneNumber() {
        return privatePhoneNumber;
    }

    public String getProfessionalPhoneNumber() {
        return professionalPhoneNumber;
    }

    public static class Builder {
        private String title;
        private String customerLanguage;
        private String firstName;
        private String name;
        private String birthDate;
        private String email;
        private String addressLine1;
        private String addressLine2;
        private String city;
        private String postCode;
        private String cellPhoneNumber;
        private String privatePhoneNumber;
        private String professionalPhoneNumber;


        public static Builder aCustomerInformation() {
            return new Builder();
        }

        public Builder withTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder withCustomerLanguage(String customerLanguage) {
            this.customerLanguage = customerLanguage;
            return this;
        }

        public Builder withFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withBirthDate(String birthDate) {
            this.birthDate = birthDate;
            return this;
        }

        public Builder withEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder withAddressLine1(String addressLine1) {
            this.addressLine1 = addressLine1;
            return this;
        }

        public Builder withAddressLine2(String addressLine2) {
            this.addressLine2 = addressLine2;
            return this;
        }

        public Builder withCity(String city) {
            this.city = city;
            return this;
        }

        public Builder withPostCode(String postCode) {
            this.postCode = postCode;
            return this;
        }

        public Builder withCellPhoneNumber(String cellPhoneNumber) {
            this.cellPhoneNumber = cellPhoneNumber;
            return this;
        }

        public Builder withPrivatePhoneNumber(String privatePhoneNumber) {
            this.privatePhoneNumber = privatePhoneNumber;
            return this;
        }

        public Builder withProfessionalPhoneNumber(String professionalPhoneNumber) {
            this.professionalPhoneNumber = professionalPhoneNumber;
            return this;
        }

        public CustomerInformation build() {
            return new CustomerInformation(this);
        }
    }
}
