package com.payline.payment.ppewshop.service;

import com.payline.payment.ppewshop.bean.common.Warning;
import com.payline.payment.ppewshop.bean.configuration.RequestConfiguration;
import com.payline.payment.ppewshop.bean.request.CheckStatusRequest;
import com.payline.payment.ppewshop.bean.request.InitDossierRequest;
import com.payline.payment.ppewshop.bean.response.CheckStatusResponse;
import com.payline.payment.ppewshop.bean.response.InitDossierResponse;
import com.payline.payment.ppewshop.bean.response.PpewShopResponseKO;
import com.payline.payment.ppewshop.exception.PluginException;
import com.payline.payment.ppewshop.utils.Constants;
import com.payline.payment.ppewshop.utils.FailureBusiness;
import com.payline.payment.ppewshop.utils.PluginUtils;
import com.payline.payment.ppewshop.utils.http.HttpClient;
import com.payline.payment.ppewshop.utils.http.StringResponse;
import com.payline.pmapi.logger.LogManager;
import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;

public class HttpService {

    //Headers
    private static final String CONTENT_TYPE_KEY = "Content-Type";
    private static final String CONTENT_TYPE_VALUE = "application/xml";
    private static final String CHARSET_KEY = "charset";
    private static final String CHARSET_VALUE = "UTF-8";

    private HttpClient client = HttpClient.getInstance();
    private static final Logger LOGGER = LogManager.getLogger(HttpService.class);


    private HttpService() {
    }

    private static class Holder {
        private static final HttpService instance = new HttpService();
    }


    public static HttpService getInstance() {
        return Holder.instance;
    }
    // --- Singleton Holder pattern + initialization END

    private Header[] createHeaders() {
        Header[] headers = new Header[2];
        headers[0] = new BasicHeader(CONTENT_TYPE_KEY, CONTENT_TYPE_VALUE);
        headers[1] = new BasicHeader(CHARSET_KEY, CHARSET_VALUE);
        return headers;
    }

    /**
     * @param configuration contains all request info
     * @param request       request object needed to create the body
     * @return the response body of the API call
     */
    public CheckStatusResponse checkStatus(RequestConfiguration configuration, CheckStatusRequest request) {

        String body = request.toXml();
        String url = configuration.getPartnerConfiguration().getProperty(Constants.PartnerConfigurationKeys.URL);
        Header[] headers = createHeaders();

        StringResponse stringResponse = client.post(url, headers, new StringEntity(body, StandardCharsets.UTF_8));

        if (stringResponse.isSuccess()) {
            return CheckStatusResponse.fromXml(stringResponse.getContent());
        } else {
            PpewShopResponseKO responseKO = PpewShopResponseKO.fromXml(stringResponse.getContent());
            LOGGER.error(responseKO.getErrorDescription());
            throw new PluginException(responseKO.getErrorCode().code, FailureBusiness.getFailureCauseFromErrorCode(responseKO.getErrorCode()));
        }
    }

    /**
     * @param configuration contains all request info
     * @param request       request object needed to create the body
     * @return the response body of the API call
     */
    public InitDossierResponse initDossier(RequestConfiguration configuration, InitDossierRequest request) {

        String body = request.toXml();
        String url = configuration.getPartnerConfiguration().getProperty(Constants.PartnerConfigurationKeys.URL);
        Header[] headers = createHeaders();

        StringResponse stringResponse = client.post(url, headers, new StringEntity(body, StandardCharsets.UTF_8));

        if (stringResponse.isSuccess()) {
            InitDossierResponse initDossierResponse = InitDossierResponse.fromXml(stringResponse.getContent());
            Warning warning = initDossierResponse.getInitDossierOut().getWarning();
            if (warning != null && !PluginUtils.isEmpty(warning.getWarningCode())) {
                LOGGER.warn("{}: {}", warning.getClass(), warning.getWarningDescription());
            }
            return initDossierResponse;
        } else {
            PpewShopResponseKO responseKO = PpewShopResponseKO.fromXml(stringResponse.getContent());
            LOGGER.error(responseKO.getErrorDescription());
            throw new PluginException(responseKO.getErrorCode().code, FailureBusiness.getFailureCauseFromErrorCode(responseKO.getErrorCode()));
        }
    }

}
