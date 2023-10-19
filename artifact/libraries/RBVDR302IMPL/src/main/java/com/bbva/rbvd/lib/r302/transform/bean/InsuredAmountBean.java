package com.bbva.rbvd.lib.r302.transform.bean;

import com.bbva.rbvd.dto.lifeinsrc.commons.InsuredAmountDTO;
import com.bbva.rbvd.dto.lifeinsrc.rimac.commons.PlanBO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.InsuranceLimitsDTO;

public class InsuredAmountBean {

    public static InsuranceLimitsDTO getInsuranceLimitsDTO(PlanBO plan) {
        InsuranceLimitsDTO insuranceLimits = new InsuranceLimitsDTO();

        InsuredAmountDTO sumaAseguradaMinima = new InsuredAmountDTO();
        InsuredAmountDTO sumaAseguradaMaxima = new InsuredAmountDTO();

        sumaAseguradaMinima.setAmount(plan.getSumaAseguradaMinima());
        sumaAseguradaMinima.setCurrency(plan.getMoneda());

        sumaAseguradaMaxima.setAmount(plan.getSumaAseguradaMaxima());
        sumaAseguradaMaxima.setCurrency(plan.getMoneda());

        insuranceLimits.setMinimumAmount(sumaAseguradaMinima);
        insuranceLimits.setMaximumAmount(sumaAseguradaMaxima);
        return insuranceLimits;
    }
}
