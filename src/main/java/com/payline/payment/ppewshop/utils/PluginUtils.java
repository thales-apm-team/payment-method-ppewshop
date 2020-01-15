package com.payline.payment.ppewshop.utils;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
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
        BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        return br.lines().collect(Collectors.joining(System.lineSeparator()));
    }

    /**
     * Return a string which was converted from cents to euro
     *
     * @param amount
     * @return Amount as String
     */
    public static String createStringAmount(BigInteger amount, Currency currency) {
        //récupérer le nombre de digits dans currency
        int nbDigits = currency.getDefaultFractionDigits();

        StringBuilder sb = new StringBuilder();
        sb.append(amount);

        for (int i = sb.length(); i < 3; i++) {
            sb.insert(0, "0");
        }

        sb.insert(sb.length() - nbDigits, ".");
        return sb.toString();
    }

    /**
     * Return a PPewShop civility mapped from Payline civility
     *
     * @param civility
     * @return PPew civility (MME, MLE or MR)
     */
    public static String getCivilityFromPayline(String civility) {
        if (civility == null) return null;

        switch (civility) {
            case "1":
            case "2":
            case "6":
                return "MME";
            case "3":
                return "MLE";
            case "4":
            case "5":
            case "7":
            case "8":
            case "9":
            case "10":
            case "11":
            case "12":
            default:
                return "MR";
        }
    }

    // todo peut etre mettre les categories PPEW en constant
    public static String getGoodsCode(String cat) {
        if (cat == null) return null;

        switch (cat) {
            case "1":
                return "616";
            case "100010001":
                return "625";
            case "100010002":
                return "626";
            case "100010003":
                return "626";
            case "2":
                return "610";
            case "20001":
                // todo
            case "200010001":
                return "624";
            case "200010002":
                return "611";
            case "200010003":
                return "615";
            case "200010004":
                return "623";
            case "200010005":
                return "623";
            case "200010006":
                return "622";
            case "200010007":
                return "613";
            case "3":
                return "621";
            case "4":
                return "320";
            case "40001":
                return "322";
            case "400010001":
                return "326";
            case "400010002":
                return "327";
            case "40002":
                //Todo
            case "400020001":
                return "323";
            case "400020002":
                return "324";
            case "400020003":
                return "329";
            case "40003":
                return "328";
            case "400030001":
                return "328";
            case "400030002":
                return "328";
            case "5":
                return "663";
            case "50001":
                return "733";
            case "500010001":
                return "733";
            case "50002":
                return "663";
            case "50003":
                return "663";
            case "500030001":
                return "663";
            case "50004":
                return "663";
            case "500040001":
                return "663";
            case "500040002":
                return "663";
            case "500040003":
                return "663";
            case "500040004":
                return "663";
            case "599990001":
                return "343";
            case "599990002":
                return "343";
            case "6":
                return "640";
            case "7":
                return "660";
            case "8":
                return "730";
            case "9":
                return "660";
            case "10":
                return "660";
            case "11":
                //todo
            case "110001":
                //todo
            case "12":
                return "330";
            case "120001":
                return "330";
            case "1200010001":
                return "334";
            case "1200010002":
                return "336";
            case "1200010003":
                return "337";
            case "1200010004":
                return "337";
            case "120002":
                return "332";
            case "120003":
                return "339";
            case "1200030001":
                return "338";
            case "120004":
                return "341";
            case "120005":
                return "340";
            case "120006":
                return "343";
            case "120007":
                return "342";
            case "120008":
                return "330";
            case "1200080001":
                return "660";
            case "13":
                //todo
            case "14":
                return "660";
            case "15":
                return "660";
            case "16":
                //todo
            case "17":
                //todo
            case "170001":
                //todo
            case "170002":
                //todo
            case "18":
                return "660";
            case "19":
                return "660";
            case "20":
                return "631";
            case "21":
                //todo
            case "22":
                //todo
            case "23":
                //todo
            case "24":
                return "858";
            case "240001":
                return "858";
            case "2400010001":
                return "855";
            case "2400010002":
                return "857";
            case "25":
                return "650";
            case "26":
                return "620";
            default:
                return "foo"; // todo

        }
    }

    public static String cleanUrl(String url){
        return url.replace("&amp;", "&");
    }

}