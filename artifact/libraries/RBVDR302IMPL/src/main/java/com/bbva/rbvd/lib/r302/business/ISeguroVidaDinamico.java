package com.bbva.rbvd.lib.r302.business;

import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.InsuranceLifeSimulationBO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;

import java.math.BigDecimal;

public interface ISeguroVidaDinamico {

    InsuranceLifeSimulationBO executeQuotationRimacService(LifeSimulationDTO input, BigDecimal sumCumulus);
    InsuranceLifeSimulationBO execuetModifyQuotationRimacService(InsuranceLifeSimulationBO payload, String quotationId, String traceId);

}
