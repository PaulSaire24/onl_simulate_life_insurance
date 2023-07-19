package com.bbva.rbvd.lib.r302.business;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.InsuranceLifeSimulationBO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;

import java.math.BigDecimal;

public interface ISeguroVidaDinamico {

    InsuranceLifeSimulationBO executeQuotationRimacService(LifeSimulationDTO input, String businessName, CustomerListASO customerListASO, BigDecimal cumulo, ApplicationConfigurationService applicationConfigurationService);
    InsuranceLifeSimulationBO executeModifyQuotationRimacService(LifeSimulationDTO input,CustomerListASO customerListASO,BigDecimal cumulo, ApplicationConfigurationService applicationConfigurationService);

}
