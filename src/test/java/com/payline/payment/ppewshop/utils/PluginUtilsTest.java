package com.payline.payment.ppewshop.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.omg.CORBA.PUBLIC_MEMBER;

import java.math.BigInteger;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

class PluginUtilsTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void truncate() {
    }

    @Test
    void inputStreamToString() {
    }


    @Test
    public void createStringAmount() {
        BigInteger int1 = BigInteger.ZERO;
        BigInteger int2 = BigInteger.ONE;
        BigInteger int3 = BigInteger.TEN;
        BigInteger int4 = BigInteger.valueOf(100);
        BigInteger int5 = BigInteger.valueOf(1000);

        Assertions.assertEquals("0.00", PluginUtils.createStringAmount(int1, Currency.getInstance("EUR")));
        Assertions.assertEquals("0.01", PluginUtils.createStringAmount(int2, Currency.getInstance("EUR")));
        Assertions.assertEquals("0.10", PluginUtils.createStringAmount(int3, Currency.getInstance("EUR")));
        Assertions.assertEquals("1.00", PluginUtils.createStringAmount(int4, Currency.getInstance("EUR")));
        Assertions.assertEquals("10.00", PluginUtils.createStringAmount(int5, Currency.getInstance("EUR")));
    }

    @Test
    void testTruncate() {
    }

    @Test
    void testCreateStringAmount() {
    }

    @Test
    void getCivilityFromPayline() {
    }

    @Test
    void getGoodsCode() {

    }

    @Test
    void cleanUrl() {
        String url = "https://wm-rt-speed.neuges.org/fr/vat/souscription?brandCode=2525011983&amp;NumVdr=1000764191&amp;typeCredit=cla&amp;transactionDeId=8a31839767c420140167c71b4baa000b";
        String expectedUrl = "https://wm-rt-speed.neuges.org/fr/vat/souscription?brandCode=2525011983&NumVdr=1000764191&typeCredit=cla&transactionDeId=8a31839767c420140167c71b4baa000b";

        Assertions.assertEquals(expectedUrl, PluginUtils.cleanUrl(url));
    }
}