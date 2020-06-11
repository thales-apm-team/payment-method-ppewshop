package com.payline.payment.ppewshop.utils.http;


import com.payline.payment.ppewshop.exception.InvalidDataException;
import com.payline.payment.ppewshop.exception.PluginException;
import com.payline.payment.ppewshop.utils.properties.ConfigProperties;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.logger.LogManager;
import org.apache.http.Header;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


public class HttpClient {
    private static final Logger LOGGER = LogManager.getLogger(HttpClient.class);

    // Exceptions messages
    private static final String SERVICE_URL_ERROR = "Service URL is invalid";

    /**
     * The number of time the client must retry to send the request if it doesn't obtain a response.
     */
    private int retries;

    private org.apache.http.client.HttpClient client;

    // --- Singleton Holder pattern + initialization BEGIN

    /**
     * ------------------------------------------------------------------------------------------------------------------
     */
    private HttpClient() {
        int connectionRequestTimeout;
        int connectTimeout;
        int socketTimeout;
        try {
            // request config timeouts (in seconds)
            ConfigProperties config = ConfigProperties.getInstance();
            connectionRequestTimeout = Integer.parseInt(config.get("http.connectionRequestTimeout"));
            connectTimeout = Integer.parseInt(config.get("http.connectTimeout"));
            socketTimeout = Integer.parseInt(config.get("http.socketTimeout"));

            // retries
            this.retries = Integer.parseInt(config.get("http.retries"));
        } catch (NumberFormatException e) {
            throw new PluginException("plugin error: http.* properties must be integers", e);
        }

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(connectionRequestTimeout * 1000)
                .setConnectTimeout(connectTimeout * 1000)
                .setSocketTimeout(socketTimeout * 1000)
                .build();

        // instantiate Apache HTTP client
        this.client = HttpClientBuilder.create()
                .useSystemProperties()
                .setDefaultRequestConfig(requestConfig)
                .setSSLSocketFactory(new SSLConnectionSocketFactory(HttpsURLConnection.getDefaultSSLSocketFactory(), SSLConnectionSocketFactory.getDefaultHostnameVerifier()))
                .build();

    }

    /**
     * ------------------------------------------------------------------------------------------------------------------
     */
    private static class Holder {
        private static final HttpClient instance = new HttpClient();
    }


    public static HttpClient getInstance() {
        return Holder.instance;
    }
    // --- Singleton Holder pattern + initialization END

    /**
     * Send the request, with a retry system in case the client does not obtain a proper response from the server.
     *
     * @param httpRequest The request to send.
     * @return The response converted as a {@link StringResponse}.
     * @throws PluginException If an error repeatedly occurs and no proper response is obtained.
     */
    StringResponse execute(HttpRequestBase httpRequest) {
        StringResponse strResponse = null;
        int attempts = 1;

        while (strResponse == null && attempts <= this.retries) {
            LOGGER.info("Start call to partner API [{} {}] (attempt {})", httpRequest.getMethod(), httpRequest.getURI(), attempts);
            try (CloseableHttpResponse httpResponse = (CloseableHttpResponse) this.client.execute(httpRequest)) {
                strResponse = StringResponse.fromHttpResponse(httpResponse);
            } catch (IOException e) {
                LOGGER.error("An error occurred during the HTTP call :", e);
                strResponse = null;
            } finally {
                attempts++;
            }
        }

        if (strResponse == null) {
            throw new PluginException("Failed to contact the partner API", FailureCause.COMMUNICATION_ERROR);
        }
        LOGGER.info("Response obtained from partner API [{} {}]", strResponse.getStatusCode(), strResponse.getStatusMessage());
        return strResponse;
    }

    /**
     * Manage Post API call
     *
     * @param url     the url to call
     * @param headers header(s) of the request
     * @param body    the body of the request
     * @return
     */
    public StringResponse post(String url, Header[] headers, StringEntity body) {
        URI uri;
        try {
            // Add the createOrderId to the url
            uri = new URI(url);
        } catch (URISyntaxException e) {
            throw new InvalidDataException(SERVICE_URL_ERROR, e);
        }

        final HttpPost httpPost = new HttpPost(uri);
        httpPost.setHeaders(headers);
        httpPost.setEntity(body);

        // Execute request
        return this.execute(httpPost);
    }

}
