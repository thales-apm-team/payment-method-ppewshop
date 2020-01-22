package com.payline.payment.ppewshop.service.impl;

import com.payline.payment.ppewshop.MockUtils;
import com.payline.payment.ppewshop.bean.response.CheckStatusResponse;
import com.payline.payment.ppewshop.bean.response.PpewShopResponseKO;
import com.payline.payment.ppewshop.exception.PluginException;
import com.payline.payment.ppewshop.utils.Constants;
import com.payline.payment.ppewshop.utils.PluginUtils;
import com.payline.payment.ppewshop.utils.http.HttpClient;
import com.payline.payment.ppewshop.utils.i18n.I18nService;
import com.payline.payment.ppewshop.utils.properties.ReleaseProperties;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.configuration.ReleaseInformation;
import com.payline.pmapi.bean.configuration.parameter.AbstractParameter;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.bean.payment.ContractProperty;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest.GENERIC_ERROR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

class ConfigurationServiceImplTest {

    @InjectMocks
    private ConfigurationServiceImpl service = new ConfigurationServiceImpl();

    @Mock
    private HttpClient client = HttpClient.getInstance();

    @Mock private ReleaseProperties releaseProperties;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void getParameters() {
        List<AbstractParameter> parameters = service.getParameters(new Locale("FR"));
        Assertions.assertEquals(3, parameters.size());

    }

    @Test
    void checkOK() {
        // create mock
        PluginException exception = new PluginException(PpewShopResponseKO.ErrorCode._21999, FailureCause.INVALID_DATA);
        Mockito.doThrow(exception).when(client).checkStatus(any(), any());

        ContractParametersCheckRequest request = MockUtils.aContractParametersCheckRequest();
        Map<String, String> errors = service.check(request);

        Assertions.assertTrue(errors.isEmpty());
    }

    @Test
    void checkEmptyFields() {
        ContractParametersCheckRequest request = MockUtils.aContractParametersCheckRequest();
        request.getAccountInfo().put(Constants.ContractConfigurationKeys.MERCHANT_CODE, null);
        request.getAccountInfo().put(Constants.ContractConfigurationKeys.DISTRIBUTOR_NUMBER, null);

        Map<String, String> errors = service.check(request);

        Assertions.assertTrue(errors.containsKey(Constants.ContractConfigurationKeys.MERCHANT_CODE));
        Assertions.assertTrue(errors.containsKey(Constants.ContractConfigurationKeys.DISTRIBUTOR_NUMBER));
    }

    @Test
    void checkWrongLength() {
        ContractParametersCheckRequest request = MockUtils.aContractParametersCheckRequest();
        request.getAccountInfo().put(Constants.ContractConfigurationKeys.MERCHANT_CODE, "foo");
        request.getAccountInfo().put(Constants.ContractConfigurationKeys.DISTRIBUTOR_NUMBER, "bar");

        Map<String, String> errors = service.check(request);

        Assertions.assertTrue(errors.containsKey(Constants.ContractConfigurationKeys.MERCHANT_CODE));
        Assertions.assertTrue(errors.containsKey(Constants.ContractConfigurationKeys.DISTRIBUTOR_NUMBER));
    }

    @Test
    void checkMerchantCodeKO() {
        // create mock
        PluginException exception = new PluginException(PpewShopResponseKO.ErrorCode._22002, FailureCause.INVALID_DATA);
        Mockito.doThrow(exception).when(client).checkStatus(any(), any());

        ContractParametersCheckRequest request = MockUtils.aContractParametersCheckRequest();
        Map<String, String> errors = service.check(request);

        Assertions.assertTrue(errors.containsKey(Constants.ContractConfigurationKeys.MERCHANT_CODE));
        Assertions.assertFalse(errors.containsKey(Constants.ContractConfigurationKeys.DISTRIBUTOR_NUMBER));
    }

    @Test
    void checkDistributorNumberKO() {
        // create mock
        PluginException exception = new PluginException(PpewShopResponseKO.ErrorCode._12006, FailureCause.INVALID_DATA);
        Mockito.doThrow(exception).when(client).checkStatus(any(), any());

        ContractParametersCheckRequest request = MockUtils.aContractParametersCheckRequest();
        Map<String, String> errors = service.check(request);

        Assertions.assertFalse(errors.containsKey(Constants.ContractConfigurationKeys.MERCHANT_CODE));
        Assertions.assertTrue(errors.containsKey(Constants.ContractConfigurationKeys.DISTRIBUTOR_NUMBER));
    }

    @Test
    void checkKOOther() {
        // create mock
        PluginException exception = new PluginException(PpewShopResponseKO.ErrorCode._22003, FailureCause.INVALID_DATA);
        Mockito.doThrow(exception).when(client).checkStatus(any(), any());

        ContractParametersCheckRequest request = MockUtils.aContractParametersCheckRequest();
        Map<String, String> errors = service.check(request);

        Assertions.assertFalse(errors.containsKey(Constants.ContractConfigurationKeys.MERCHANT_CODE));
        Assertions.assertFalse(errors.containsKey(Constants.ContractConfigurationKeys.DISTRIBUTOR_NUMBER));
        Assertions.assertTrue(errors.containsKey(GENERIC_ERROR));
    }

    @Test
    void getReleaseInformation() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String version = "M.m.p";

        // given: the release properties are OK
        doReturn( version ).when( releaseProperties ).get("release.version");
        Calendar cal = new GregorianCalendar();
        cal.set(2019, Calendar.AUGUST, 19);
        doReturn( formatter.format( cal.getTime() ) ).when( releaseProperties ).get("release.date");

        // when: calling the method getReleaseInformation
        ReleaseInformation releaseInformation = service.getReleaseInformation();

        // then: releaseInformation contains the right values
        assertEquals(version, releaseInformation.getVersion());
        assertEquals(2019, releaseInformation.getDate().getYear());
        assertEquals(Month.AUGUST, releaseInformation.getDate().getMonth());
        assertEquals(19, releaseInformation.getDate().getDayOfMonth());
    }

    @Test
    void getName() {
        String name = service.getName(Locale.FRANCE);
        Assertions.assertNotNull(name);
        Assertions.assertFalse(name.isEmpty());
    }
}