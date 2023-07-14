package com.bbva.rbvd.lib.r302.business.impl;

import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.InsuranceLifeSimulationBO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;
import com.bbva.rbvd.lib.r302.business.ISeguroVidaDinamico;

import java.math.BigDecimal;

public class SeguroVidaDinamico implements ISeguroVidaDinamico {


    @Override
    public InsuranceLifeSimulationBO executeQuotationRimacService(LifeSimulationDTO input, BigDecimal sumCumulus) {
        return null;
    }

    @Override
    public InsuranceLifeSimulationBO execuetModifyQuotationRimacService(InsuranceLifeSimulationBO payload, String quotationId, String traceId) {
        return null;
    }
}
