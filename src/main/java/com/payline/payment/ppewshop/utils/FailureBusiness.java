package com.payline.payment.ppewshop.utils;

import com.payline.payment.ppewshop.bean.response.PpewShopResponseKO;
import com.payline.pmapi.bean.common.FailureCause;

public class FailureBusiness {


    public static FailureCause getFailureCauseFromErrorCode(PpewShopResponseKO.ErrorCode errorCode) {

        FailureCause cause;
        switch (errorCode) {
            case CODE_11001:
            case CODE_11002:
            case CODE_11008:
            case CODE_11009:
            case CODE_11999:
            case CODE_21001:
            case CODE_21002:
            case CODE_21003:
            case CODE_21009:
            case CODE_21999:
            case CODE_21004:
                cause = FailureCause.PAYMENT_PARTNER_ERROR;
                break;
            case CODE_12001:
            case CODE_12002:
            case CODE_12003:
            case CODE_12004:
            case CODE_12006:
            case CODE_12205:
            case CODE_22001:
            case CODE_22002:
            case CODE_22003:
                cause = FailureCause.INVALID_DATA;
                break;
            case CODE_12207:
                cause = FailureCause.INVALID_FIELD_FORMAT;
                break;
            case CODE_12301:
                cause = FailureCause.REFUSED;
                break;
            default:
                cause = FailureCause.PARTNER_UNKNOWN_ERROR;
                break;
        }
        return cause;
    }
}
