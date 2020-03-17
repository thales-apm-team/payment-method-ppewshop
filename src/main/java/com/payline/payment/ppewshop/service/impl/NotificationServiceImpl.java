package com.payline.payment.ppewshop.service.impl;

import com.payline.payment.ppewshop.bean.NotificationResponseHandler;
import com.payline.payment.ppewshop.bean.PaymentResponseByNotificationResponseHandler;
import com.payline.payment.ppewshop.bean.TransactionStateChangedResponseHandler;
import com.payline.payment.ppewshop.bean.common.CheckStatusIn;
import com.payline.payment.ppewshop.bean.common.CheckStatusOut;
import com.payline.payment.ppewshop.bean.common.MerchantInformation;
import com.payline.payment.ppewshop.bean.configuration.RequestConfiguration;
import com.payline.payment.ppewshop.bean.request.CheckStatusRequest;
import com.payline.payment.ppewshop.bean.response.CheckStatusResponse;
import com.payline.payment.ppewshop.exception.InvalidDataException;
import com.payline.payment.ppewshop.exception.PluginException;
import com.payline.payment.ppewshop.utils.Constants;
import com.payline.payment.ppewshop.utils.PluginUtils;
import com.payline.payment.ppewshop.utils.http.HttpClient;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.notification.request.NotificationRequest;
import com.payline.pmapi.bean.notification.response.NotificationResponse;
import com.payline.pmapi.bean.payment.request.NotifyTransactionStatusRequest;
import com.payline.pmapi.logger.LogManager;
import com.payline.pmapi.service.NotificationService;
import org.apache.logging.log4j.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NotificationServiceImpl implements NotificationService {
    private static final Logger LOGGER = LogManager.getLogger(NotificationServiceImpl.class);
    private HttpClient client = HttpClient.getInstance();

    private static final Pattern pattern = Pattern.compile("transactionDeId=(.{13})");
    public static final int CREATED = 204;


    @Override
    public NotificationResponse parse(NotificationRequest request) {
        NotificationResponse notificationResponse;
        RequestConfiguration configuration = RequestConfiguration.build(request);
        final String transactionId = request.getTransactionId();
        NotificationResponseHandler notificationResponseHandler;
        String partnerTransactionId = "UNKNOWN";

        if (transactionId == null) {
            // transaction does not exist yet in Payline -> PaymentResponseByNotificationResponse will be returned.
            notificationResponseHandler = new PaymentResponseByNotificationResponseHandler();
        } else {
            // transaction exists in Payline -> TransactionStateChangedResponse will be returned.
            notificationResponseHandler = new TransactionStateChangedResponseHandler();
        }

        try {
            // get the partner transactionId
            partnerTransactionId = getTransactionIdFromURL(request.getPathInfo());
            CheckStatusRequest checkStatusRequest = createCheckStatusRequest(request, partnerTransactionId);

            // do the http call to get the final status
            CheckStatusResponse checkStatusResponse = client.checkStatus(configuration, checkStatusRequest);

            // check the status and create the right notificationResponse
            notificationResponse = createNotificationResponseFromStatusCode(transactionId
                    , checkStatusResponse
                    , checkStatusResponse.getCheckStatusOut().getStatusCode()
                    , notificationResponseHandler);

        } catch (PluginException e) {
            notificationResponse = notificationResponseHandler.handlePluginException(transactionId, partnerTransactionId, e);
        } catch (RuntimeException e) {
            LOGGER.error("Unexpected plugin error", e);
            notificationResponse = notificationResponseHandler.handleRuntimeException(transactionId, partnerTransactionId, e);
        }

        return notificationResponse;
    }

    @Override
    public void notifyTransactionStatus(NotifyTransactionStatusRequest notifyTransactionStatusRequest) {
        // does nothing
    }

    private NotificationResponse createNotificationResponseFromStatusCode(String transactionId
            , CheckStatusResponse response
            , CheckStatusOut.StatusCode code
            , NotificationResponseHandler handler) {
        NotificationResponse notificationResponse;
        switch (code) {
            case A:
                notificationResponse = handler.successResponse(transactionId, response);
                break;
            case E:
                notificationResponse = handler.onHoldResponse(transactionId, response);
                break;
            case I:
            case R:
                notificationResponse = handler.failureResponse(transactionId, response, FailureCause.REFUSED);
                break;
            case C:
                notificationResponse = handler.failureResponse(transactionId, response, FailureCause.CANCEL);
                break;
            default:
                notificationResponse = handler.failureResponse(transactionId, response, FailureCause.INVALID_DATA);
                break;
        }
        return notificationResponse;
    }

    private CheckStatusRequest createCheckStatusRequest(NotificationRequest request, String partnerTransactionId) {
        MerchantInformation merchantInformation = MerchantInformation.Builder.aMerchantInformation()
                .withMerchantCode(request.getContractConfiguration().getProperty(Constants.ContractConfigurationKeys.MERCHANT_CODE).getValue())
                .withDistributorNumber(request.getContractConfiguration().getProperty(Constants.ContractConfigurationKeys.DISTRIBUTOR_NUMBER).getValue())
                .withCountryCode(request.getContractConfiguration().getProperty(Constants.ContractConfigurationKeys.COUNTRY_CODE).getValue())
                .build();

        CheckStatusIn checkStatusIn = CheckStatusIn.Builder
                .aCheckStatusIn()
                .withTransactionId(partnerTransactionId)
                .withMerchantInformation(merchantInformation)
                .build();

        return new CheckStatusRequest(checkStatusIn);
    }

    /**
     * Search the transactionId in the given url
     * transactionId is 13 length and preceded by "transactionDeId="
     *
     * @param url the url where the transactionIs is
     * @return the transactionId
     */
    public String getTransactionIdFromURL(String url) {
        if (PluginUtils.isEmpty(url)) {
            throw new InvalidDataException("No url to parse");
        }

        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            throw new InvalidDataException("No transactionId in url");
        }
    }
}
