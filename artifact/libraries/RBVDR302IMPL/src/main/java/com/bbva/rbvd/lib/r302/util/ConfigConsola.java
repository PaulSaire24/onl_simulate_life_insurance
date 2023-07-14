package com.bbva.rbvd.lib.r302.util;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;

public class ConfigConsola {
    private ApplicationConfigurationService applicationConfigurationService;

    public ConfigConsola(ApplicationConfigurationService applicationConfigurationService) {
        this.applicationConfigurationService = applicationConfigurationService;
    }

    public void getConfigConsola(LifeSimulationDTO input){
        String documentTypeId = this.applicationConfigurationService.getProperty(input.getHolder().getIdentityDocument().getDocumentType().getId());
    }

    public String getPlanesLife(){
        return this.applicationConfigurationService.getProperty("plansLife");
    }

}
