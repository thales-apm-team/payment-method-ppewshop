package com.payline.payment.ppewshop.service.impl;

import com.payline.payment.ppewshop.bean.common.MerchantInformation;
import com.payline.payment.ppewshop.bean.configuration.RequestConfiguration;
import com.payline.payment.ppewshop.bean.request.CheckStatusRequest;
import com.payline.payment.ppewshop.bean.response.PpewShopResponseKO;
import com.payline.payment.ppewshop.exception.PluginException;
import com.payline.payment.ppewshop.utils.Constants;
import com.payline.payment.ppewshop.utils.PluginUtils;
import com.payline.payment.ppewshop.utils.http.HttpClient;
import com.payline.payment.ppewshop.utils.i18n.I18nService;
import com.payline.payment.ppewshop.utils.properties.ReleaseProperties;
import com.payline.pmapi.bean.configuration.ReleaseInformation;
import com.payline.pmapi.bean.configuration.parameter.AbstractParameter;
import com.payline.pmapi.bean.configuration.parameter.impl.InputParameter;
import com.payline.pmapi.bean.configuration.parameter.impl.ListBoxParameter;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.logger.LogManager;
import com.payline.pmapi.service.ConfigurationService;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest.GENERIC_ERROR;

public class ConfigurationServiceImpl implements ConfigurationService {
    private static final Logger LOGGER = LogManager.getLogger(ConfigurationServiceImpl.class);

    private ReleaseProperties releaseProperties = ReleaseProperties.getInstance();
    private I18nService i18n = I18nService.getInstance();
        private HttpClient client = HttpClient.getInstance();


    private static final int LENGTH = 10;

    private static final String MERCHANT_CODE_LABEL = "merchantCode.label";
    private static final String MERCHANT_CODE_DESCRIPTION = "merchantCode.description";
    private static final String MERCHANT_CODE_ERROR_EMPTY = "merchantCode.error.empty";
    private static final String MERCHANT_CODE_ERROR_LENGTH = "merchantCode.error.length";
    private static final String MERCHANT_CODE_ERROR_INVALID = "merchantCode.error.invalid";
    private static final String DISTRIBUTOR_NUMBER_LABEL = "distributorNumber.label";
    private static final String DISTRIBUTOR_NUMBER_DESCRIPTION = "distributorNumber.description";
    private static final String DISTRIBUTOR_NUMBER_ERROR_EMPTY = "distributorNumber.error.empty";
    private static final String DISTRIBUTOR_NUMBER_ERROR_LENGTH = "distributorNumber.error.length";
    private static final String DISTRIBUTOR_NUMBER_ERROR_INVALID = "distributorNumber.error.invalid";
    private static final String COUNTRY_CODE_LABEL = "countryCode.label";
    private static final String COUNTRY_CODE_DESCRIPTION = "countryCode.description";
    private static final String FRA_KEY = "FRA";
    private static final String FRA_VAL = "country.fra";
    private static final String GBR_KEY = "GBR";
    private static final String GBR_VAL = "country.gbr";
    private static final String IRL_KEY = "IRL";
    private static final String IRL_VAL = "country.irl";
    private static final String ITA_KEY = "ITA";
    private static final String ITA_VAL = "country.ita";
    private static final String ESP_KEY = "ESP";
    private static final String ESP_VAL = "country.esp";

    @Override
    public List<AbstractParameter> getParameters(Locale locale) {
        List<AbstractParameter> parameters = new ArrayList<>();

        // merchantCode inputParameter
        AbstractParameter merchantCode = new InputParameter();
        merchantCode.setKey(Constants.ContractConfigurationKeys.MERCHANT_CODE);
        merchantCode.setLabel(i18n.getMessage(MERCHANT_CODE_LABEL, locale));
        merchantCode.setDescription(i18n.getMessage(MERCHANT_CODE_DESCRIPTION, locale));
        merchantCode.setRequired(true);
        parameters.add(merchantCode);

        // merchantCode inputParameter
        AbstractParameter distributorNumber = new InputParameter();
        distributorNumber.setKey(Constants.ContractConfigurationKeys.DISTRIBUTOR_NUMBER);
        distributorNumber.setLabel(i18n.getMessage(DISTRIBUTOR_NUMBER_LABEL, locale));
        distributorNumber.setDescription(i18n.getMessage(DISTRIBUTOR_NUMBER_DESCRIPTION, locale));
        distributorNumber.setRequired(true);
        parameters.add(distributorNumber);

        // countryCode ListBoxParameter
        Map<String, String> countryCodes = new HashMap<>();
        countryCodes.put(FRA_KEY, i18n.getMessage(FRA_VAL, locale));
        countryCodes.put(GBR_KEY, i18n.getMessage(GBR_VAL, locale));
        countryCodes.put(IRL_KEY, i18n.getMessage(IRL_VAL, locale));
        countryCodes.put(ITA_KEY, i18n.getMessage(ITA_VAL, locale));
        countryCodes.put(ESP_KEY, i18n.getMessage(ESP_VAL, locale));

        ListBoxParameter countryCode = new ListBoxParameter();
        countryCode.setKey(Constants.ContractConfigurationKeys.COUNTRY_CODE);
        countryCode.setLabel(i18n.getMessage(COUNTRY_CODE_LABEL, locale));
        countryCode.setDescription(i18n.getMessage(COUNTRY_CODE_DESCRIPTION, locale));
        countryCode.setRequired(true);
        countryCode.setList(countryCodes);
        countryCode.setValue(FRA_KEY);
        parameters.add(countryCode);

        return parameters;
    }

    @Override
    public Map<String, String> check(ContractParametersCheckRequest request) {
        final Locale locale = request.getLocale();
        final Map<String, String> errors = new HashMap<>();
        final RequestConfiguration configuration = new RequestConfiguration(
                request.getContractConfiguration()
                , request.getEnvironment()
                , request.getPartnerConfiguration()
        );
        try {
            Map<String, String> infos = request.getAccountInfo();
            // verify fields length
            if (PluginUtils.isEmpty(infos.get(Constants.ContractConfigurationKeys.MERCHANT_CODE))) {
                errors.put(Constants.ContractConfigurationKeys.MERCHANT_CODE
                        , i18n.getMessage(MERCHANT_CODE_ERROR_EMPTY, locale));
            } else if (infos.get(Constants.ContractConfigurationKeys.MERCHANT_CODE).length() != LENGTH) {
                errors.put(Constants.ContractConfigurationKeys.MERCHANT_CODE
                        , i18n.getMessage(MERCHANT_CODE_ERROR_LENGTH, locale));
            }

            if (PluginUtils.isEmpty(infos.get(Constants.ContractConfigurationKeys.DISTRIBUTOR_NUMBER))) {
                errors.put(Constants.ContractConfigurationKeys.DISTRIBUTOR_NUMBER
                        , i18n.getMessage(DISTRIBUTOR_NUMBER_ERROR_EMPTY, locale));
            } else if (infos.get(Constants.ContractConfigurationKeys.MERCHANT_CODE).length() != LENGTH) {
                errors.put(Constants.ContractConfigurationKeys.DISTRIBUTOR_NUMBER
                        , i18n.getMessage(DISTRIBUTOR_NUMBER_ERROR_LENGTH, locale));
            }

            // do a test call
            MerchantInformation merchantInformation = MerchantInformation.Builder.aMerchantInformation()
                    .withMerchantCode(infos.get(Constants.ContractConfigurationKeys.MERCHANT_CODE))
                    .withDistributorNumber(infos.get(Constants.ContractConfigurationKeys.DISTRIBUTOR_NUMBER))
                    .withCountryCode(infos.get(Constants.ContractConfigurationKeys.COUNTRY_CODE))
                    .build();

            CheckStatusRequest checkStatusRequest = new CheckStatusRequest();
            checkStatusRequest.getCheckStatusIn().setTransactionId("0");
            checkStatusRequest.getCheckStatusIn().setMerchantInformation(merchantInformation);

            // call in order to get the intended ErrorMessage
            client.checkStatus(configuration, checkStatusRequest);

        } catch (PluginException e) {
            if (!PpewShopResponseKO.ErrorCode.CODE_21999.equalsIgnoreCase(e.getErrorCode())) {
                if (PpewShopResponseKO.ErrorCode.CODE_22002.equalsIgnoreCase(e.getErrorCode())) {
                    // wrong merchant code
                    errors.put(Constants.ContractConfigurationKeys.MERCHANT_CODE
                            , i18n.getMessage(MERCHANT_CODE_ERROR_INVALID, locale));

                } else if (PpewShopResponseKO.ErrorCode.CODE_12006.equalsIgnoreCase(e.getErrorCode())) {
                    // wrong distributor number
                    errors.put(Constants.ContractConfigurationKeys.DISTRIBUTOR_NUMBER
                            , i18n.getMessage(DISTRIBUTOR_NUMBER_ERROR_INVALID, locale));
                } else {
                    // another unintended error
                    errors.put(GENERIC_ERROR, e.getErrorCode());
                }
            }
        } catch (RuntimeException e) {
            LOGGER.error("Unexpected plugin error", e);
            errors.put(GENERIC_ERROR, e.getMessage());
        }
        return errors;
    }

    @Override
    public ReleaseInformation getReleaseInformation() {
        return ReleaseInformation.ReleaseBuilder.aRelease()
                .withDate(LocalDate.parse(releaseProperties.get("release.date"), DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .withVersion(releaseProperties.get("release.version"))
                .build();
    }

    @Override
    public String getName(Locale locale) {
        return i18n.getMessage("paymentMethod.name", locale);
    }
}
