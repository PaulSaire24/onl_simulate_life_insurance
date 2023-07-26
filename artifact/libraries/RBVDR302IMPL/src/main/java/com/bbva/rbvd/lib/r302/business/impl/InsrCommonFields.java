package com.bbva.rbvd.lib.r302.business.impl;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.rbvd.lib.r301.RBVDR301;

public class InsrCommonFields {
    private final RBVDR301 rbvdR301;
    private final ApplicationConfigurationService applicationConfigurationService;

    public InsrCommonFields(RBVDR301 rbvdR301, ApplicationConfigurationService applicationConfigurationService) {
        this.rbvdR301 = rbvdR301;
        this.applicationConfigurationService = applicationConfigurationService;
    }

    public RBVDR301 getRbvdR301() {
        return rbvdR301;
    }

    public ApplicationConfigurationService getApplicationConfigurationService() {
        return applicationConfigurationService;
    }

}
