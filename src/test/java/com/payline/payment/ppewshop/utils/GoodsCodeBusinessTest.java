package com.payline.payment.ppewshop.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class GoodsCodeBusinessTest {

    private static Stream<Arguments> category_set() {
        return Stream.of(
                Arguments.of("1", "625"),
                Arguments.of("100010001", "625"),
                Arguments.of("100010002", "626"),
                Arguments.of("100010003", "626"),
                Arguments.of("2", "610"),
                Arguments.of("20001", "611"),
                Arguments.of("200010001", "624"),
                Arguments.of("200010002", "611"),
                Arguments.of("200010003", "615"),
                Arguments.of("200010004", "623"),
                Arguments.of("200010005", "623"),
                Arguments.of("200010006", "622"),
                Arguments.of("200010007", "613"),
                Arguments.of("3", "621"),
                Arguments.of("4", "320"),
                Arguments.of("40001", "322"),
                Arguments.of("400010001", "326"),
                Arguments.of("400010002", "327"),
                Arguments.of("40002", "320"),
                Arguments.of("400020001", "323"),
                Arguments.of("400020002", "324"),
                Arguments.of("400020003", "329"),
                Arguments.of("40003", "328"),
                Arguments.of("400030001", "328"),
                Arguments.of("400030002", "328"),
                Arguments.of("5", "663"),
                Arguments.of("50001", "913"),
                Arguments.of("500010001", "733"),
                Arguments.of("50002", "913"),
                Arguments.of("50003", "663"),
                Arguments.of("500030001", "737"),
                Arguments.of("50004", "663"),
                Arguments.of("500040001", "738"),
                Arguments.of("500040002", "739"),
                Arguments.of("500040003", "740"),
                Arguments.of("500040004", "741"),
                Arguments.of("599990001", "941"),
                Arguments.of("599990002", "912"),
                Arguments.of("6", "640"),
                Arguments.of("7", "660"),
                Arguments.of("8", "730"),
                Arguments.of("9", "660"),
                Arguments.of("10", "660"),
                Arguments.of("11", "660"),
                Arguments.of("110001", "742"),
                Arguments.of("12", "330"),
                Arguments.of("120001", "331"),
                Arguments.of("1200010001", "334"),
                Arguments.of("1200010002", "336"),
                Arguments.of("1200010003", "337"),
                Arguments.of("1200010004", "337"),
                Arguments.of("120002", "332"),
                Arguments.of("120003", "339"),
                Arguments.of("1200030001", "338"),
                Arguments.of("120004", "341"),
                Arguments.of("120005", "340"),
                Arguments.of("120006", "343"),
                Arguments.of("120007", "342"),
                Arguments.of("120008", "330"),
                Arguments.of("1200080001", "660"),
                Arguments.of("13", "000"),
                Arguments.of("14", "660"),
                Arguments.of("15", "660"),
                Arguments.of("16", "000"),
                Arguments.of("17", "000"),
                Arguments.of("170001", "000"),
                Arguments.of("170002", "000"),
                Arguments.of("18", "660"),
                Arguments.of("19", "660"),
                Arguments.of("20", "631"),
                Arguments.of("21", "000"),
                Arguments.of("22", "000"),
                Arguments.of("23", "000"),
                Arguments.of("24", "858"),
                Arguments.of("240001", "858"),
                Arguments.of("2400010001", "855"),
                Arguments.of("2400010002", "857"),
                Arguments.of("25", "650"),
                Arguments.of("26", "620"),
                Arguments.of("XXXXX", "000")
        );
    }

    @ParameterizedTest
    @MethodSource("category_set")
    void getGoodsCodeTest(String paylineCategory, String PpewCategory) {
        Assertions.assertEquals(PpewCategory, GoodsCodeBusiness.getGoodsCode(paylineCategory));
    }
}