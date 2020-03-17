package com.payline.payment.ppewshop.utils;


import com.payline.payment.ppewshop.exception.PluginException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Currency;
import java.util.stream.Collectors;

public class PluginUtils {

    private PluginUtils() {
        // ras.
    }

    public static String truncate(String value, int length) {
        if (value != null && value.length() > length) {
            value = value.substring(0, length);
        }
        return value;
    }

    /**
     * Convert an InputStream into a String
     *
     * @param stream the InputStream to convert
     * @return the converted String encoded in UTF-8
     */
    public static String inputStreamToString(InputStream stream) {
        try(BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))){
            return br.lines().collect(Collectors.joining(System.lineSeparator()));
        }catch (IOException e){
            throw new PluginException("Unable to read the stream");
        }
    }

    /**
     * Return a string which was converted from cents to currency amount
     *
     * @param amount the amount in cents
     * @return Amount as String
     */
    public static String createStringAmount(BigInteger amount, Currency currency) {
        int nbDigits = currency.getDefaultFractionDigits();
        final BigDecimal bigDecimal = new BigDecimal(amount);
        return String.valueOf(bigDecimal.movePointLeft(nbDigits));
    }

    /**
     * Check if a String is null or empty
     *
     * @param value the String to check
     * @return
     */
    public static boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }

    /**
     * replace &amp by & in URL. this is asked by PPEWShop.
     * example url: https://wm-rt-speed.neuges.org/fr/vat/souscription?brandCode=2525011983&amp;NumVdr=1000764191&amp;typeCredit=cla&amp;transactionDeId=8a31baa000b
     * @param url PPEWShop url to clean
     * @return
     */
    public static String cleanUrl(String url) {
        return url.replace("&amp;", "&");
    }

}