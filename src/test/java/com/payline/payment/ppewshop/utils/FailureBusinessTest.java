package com.payline.payment.ppewshop.utils;

import com.payline.payment.ppewshop.MockUtils;
import com.payline.payment.ppewshop.bean.response.PpewShopResponseKO;
import com.payline.pmapi.bean.common.FailureCause;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.payline.payment.ppewshop.utils.FailureBusiness.getFailureCauseFromErrorCode;

class FailureBusinessTest {

    private static Stream<Arguments> errorCode_set() {
        return Stream.of(
                Arguments.of("11001", FailureCause.PAYMENT_PARTNER_ERROR),
                Arguments.of("11002", FailureCause.PAYMENT_PARTNER_ERROR),
                Arguments.of("11999", FailureCause.PAYMENT_PARTNER_ERROR),
                Arguments.of("12001", FailureCause.INVALID_DATA),
                Arguments.of("12002", FailureCause.INVALID_DATA),
                Arguments.of("12003", FailureCause.INVALID_DATA),
                Arguments.of("12004", FailureCause.INVALID_DATA),
                Arguments.of("12006", FailureCause.INVALID_DATA),
                Arguments.of("12205", FailureCause.INVALID_DATA),
                Arguments.of("12207", FailureCause.INVALID_FIELD_FORMAT),
                Arguments.of("21001", FailureCause.PAYMENT_PARTNER_ERROR),
                Arguments.of("21002", FailureCause.PAYMENT_PARTNER_ERROR),
                Arguments.of("21003", FailureCause.PAYMENT_PARTNER_ERROR),
                Arguments.of("21999", FailureCause.PAYMENT_PARTNER_ERROR),
                Arguments.of("21004", FailureCause.PAYMENT_PARTNER_ERROR),
                Arguments.of("22001", FailureCause.INVALID_DATA),
                Arguments.of("22002", FailureCause.INVALID_DATA),
                Arguments.of("22003", FailureCause.INVALID_DATA),
                Arguments.of("12006", FailureCause.INVALID_DATA),
                Arguments.of("12202", FailureCause.INVALID_DATA),
                Arguments.of("12206", FailureCause.INVALID_DATA)
        );
    }

    @ParameterizedTest
    @MethodSource("errorCode_set")
    void getFailureCauseFromErrorCodeTest(String errorCode, FailureCause cause) {
        String xml = MockUtils.templateResponseError
                .replace("ERROR_CODE", errorCode);

        PpewShopResponseKO responseKO = PpewShopResponseKO.fromXml(xml);
        Assertions.assertEquals(cause, getFailureCauseFromErrorCode(responseKO.getErrorCode()));
    }
}