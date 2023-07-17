package com.bbva.rbvd.lib.r302.business;

import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.InsuranceLifeSimulationBO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;

import java.math.BigDecimal;

public interface ISeguroEasyYes {

    void serviceAddGifole(LifeSimulationDTO response, CustomerListASO responseListCustomers);
    InsuranceLifeSimulationBO executeQuotationRimacService(LifeSimulationDTO input, InsuranceLifeSimulationBO requestRimac);

}
