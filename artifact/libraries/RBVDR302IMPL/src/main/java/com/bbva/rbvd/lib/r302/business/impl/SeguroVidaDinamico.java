package com.bbva.rbvd.lib.r302.business.impl;

import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.InsuranceLifeSimulationBO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;
import com.bbva.rbvd.lib.r301.RBVDR301;
import com.bbva.rbvd.lib.r302.business.ISeguroVidaDinamico;

import java.math.BigDecimal;

public class SeguroVidaDinamico implements ISeguroVidaDinamico {

    private RBVDR301 rbvdR301;
    @Override
    public InsuranceLifeSimulationBO executeQuotationRimacService(LifeSimulationDTO input, InsuranceLifeSimulationBO requestRimac) {
        InsuranceLifeSimulationBO responseRimac = rbvdR301.executeSimulationRimacService(requestRimac,input.getTraceId());
        return responseRimac;
    }

    @Override
    public InsuranceLifeSimulationBO execuetModifyQuotationRimacService(InsuranceLifeSimulationBO payload, String quotationId, String traceId) {
        return rbvdR301.executeSimulationModificationRimacService(payload,quotationId,traceId);
    }

    public void setRbvdR301(RBVDR301 rbvdR301) {
        this.rbvdR301 = rbvdR301;
    }
}
