package com.bbva.rbvd.lib.r302.service.api;

import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.InsuranceLifeSimulationBO;
import com.bbva.rbvd.lib.r301.RBVDR301;

public class ConsumerExternalService {

    private final RBVDR301 rbvdR301;

    public ConsumerExternalService(RBVDR301 rbvdR301) {
        this.rbvdR301 = rbvdR301;
    }

   public InsuranceLifeSimulationBO executeSimulationRimacService(InsuranceLifeSimulationBO requestRimac,String traceId){
        return this.rbvdR301.executeSimulationRimacService(requestRimac,traceId);
   }

   public InsuranceLifeSimulationBO executeSimulationModificationRimacService(InsuranceLifeSimulationBO requestRimac,String externalSimulationId,String traceId){
       return this.rbvdR301.executeSimulationModificationRimacService(requestRimac,externalSimulationId,traceId);
   }
}
