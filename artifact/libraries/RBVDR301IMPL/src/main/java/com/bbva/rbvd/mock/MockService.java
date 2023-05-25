package com.bbva.rbvd.mock;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.pisd.dto.insurance.aso.tier.TierASO;
import com.bbva.pisd.dto.insurance.utils.PISDProperties;
import com.bbva.rbvd.lib.r301.impl.util.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MockService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MockService.class);

    protected ApplicationConfigurationService applicationConfigurationService;

    public boolean isEnabledTierMock() {
        String value = this.applicationConfigurationService.getProperty(PISDProperties.ASO_MOCK_TIER_ENABLED.getValue());
        boolean result = Boolean.parseBoolean(value);
        if(result) LOGGER.info("***** mockService: TIER SERVICE MOCK ENABLED *****");
        return result;
    }

    public TierASO getTierASOMock() {
        LOGGER.info("***** mockService getTierASOMock *****");
        return JsonHelper.getInstance().fromString(
                applicationConfigurationService.getProperty(PISDProperties.ASO_GET_TIER_MOCK.getValue()),
                TierASO.class);
    }

    public void setApplicationConfigurationService(ApplicationConfigurationService applicationConfigurationService) {
        this.applicationConfigurationService = applicationConfigurationService;
    }
}
