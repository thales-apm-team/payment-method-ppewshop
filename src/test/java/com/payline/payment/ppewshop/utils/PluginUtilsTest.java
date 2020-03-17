package com.payline.payment.ppewshop.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Currency;
import java.util.stream.Stream;

class PluginUtilsTest {


    @Test
    void inputStreamToString() {
        String expected = "foo";
        InputStream stream = new ByteArrayInputStream(expected.getBytes());

        Assertions.assertEquals(expected, PluginUtils.inputStreamToString(stream));
    }


    @Test
    void createStringAmount() {
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
        Assertions.assertEquals("0123456789", PluginUtils.truncate("01234567890123456789", 10));
        Assertions.assertEquals("01234567890123456789", PluginUtils.truncate("01234567890123456789", 60));
        Assertions.assertEquals("", PluginUtils.truncate("", 30));
        Assertions.assertNull(PluginUtils.truncate(null, 30));
    }

    @Test
    void testIsEmpty() {
        Assertions.assertTrue(PluginUtils.isEmpty(null));
        Assertions.assertTrue(PluginUtils.isEmpty(""));
        Assertions.assertFalse(PluginUtils.isEmpty("foo"));
    }

    @Test
    void cleanUrl() {
        String url = "https://wm-rt-speed.neuges.org/fr/vat/souscription?brandCode=2525011983&amp;NumVdr=1000764191&amp;typeCredit=cla&amp;transactionDeId=8a31839767c420140167c71b4baa000b";
        String expectedUrl = "https://wm-rt-speed.neuges.org/fr/vat/souscription?brandCode=2525011983&NumVdr=1000764191&typeCredit=cla&transactionDeId=8a31839767c420140167c71b4baa000b";

        Assertions.assertEquals(expectedUrl, PluginUtils.cleanUrl(url));
    }
}