package com.payline.payment.ppewshop.service;

import com.payline.payment.ppewshop.exception.PluginException;
import com.payline.payment.ppewshop.utils.PluginUtils;
import com.payline.payment.ppewshop.utils.i18n.I18nService;
import com.payline.payment.ppewshop.utils.properties.ConfigProperties;
import com.payline.pmapi.bean.paymentform.bean.PaymentFormLogo;
import com.payline.pmapi.bean.paymentform.request.PaymentFormLogoRequest;
import com.payline.pmapi.bean.paymentform.response.logo.PaymentFormLogoResponse;
import com.payline.pmapi.bean.paymentform.response.logo.impl.PaymentFormLogoResponseFile;
import com.payline.pmapi.logger.LogManager;
import com.payline.pmapi.service.PaymentFormConfigurationService;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

public abstract class LogoPaymentFormConfigurationService implements PaymentFormConfigurationService {

    private static final Logger LOGGER = LogManager.getLogger(LogoPaymentFormConfigurationService.class);

    private I18nService i18n = I18nService.getInstance();
    private ConfigProperties config = ConfigProperties.getInstance();

    @Override
    public PaymentFormLogoResponse getPaymentFormLogo(PaymentFormLogoRequest paymentFormLogoRequest) {
        Locale locale = paymentFormLogoRequest.getLocale();
        int height;
        int width;
        try {
            height = Integer.parseInt(config.get("logo.height"));
            width = Integer.parseInt(config.get("logo.width"));
        } catch (NumberFormatException e) {
            throw new PluginException("Plugin config error: logo height and width must be integers", e);
        }

        return PaymentFormLogoResponseFile.PaymentFormLogoResponseFileBuilder.aPaymentFormLogoResponseFile()
                .withHeight(height)
                .withWidth(width)
                .withTitle(i18n.getMessage("paymentMethod.name", locale))
                .withAlt(i18n.getMessage("paymentMethod.name", locale) + " logo")
                .build();
    }

    @Override
    public PaymentFormLogo getLogo(String paymentMethodIdentifier, Locale locale) {
        String filename = config.get("logo.filename");
        if (PluginUtils.isEmpty(filename)){
            LOGGER.error("No file name for the logo");
            throw new PluginException("Plugin error: No file name for the logo");
        }
        String format = config.get("logo.format");
        if (PluginUtils.isEmpty(format)){
            LOGGER.error("no format defined for file {}", filename);
            throw new PluginException("Plugin error: No file format for the logo");
        }

        String contentType = config.get("logo.contentType");
        if (PluginUtils.isEmpty(format)){
            LOGGER.error("no content type defined for file {}", filename);
            throw new PluginException("Plugin error: No content type for the logo");
        }

        try (InputStream input = this.getClass().getClassLoader().getResourceAsStream(filename)){
            if (input == null) {
                LOGGER.error("Unable to load file {}", filename);
                throw new PluginException("Plugin error: unable to load the logo file");
            }
            // Read logo file
            BufferedImage logo = ImageIO.read(input);

            // Recover byte array from image
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(logo, format, baos);
            baos.close();

            return PaymentFormLogo.PaymentFormLogoBuilder.aPaymentFormLogo()
                    .withFile(baos.toByteArray())
                    .withContentType(contentType)
                    .build();
        } catch (IOException e) {
            throw new PluginException("Plugin error: unable to read the logo", e);
        }
    }

}
