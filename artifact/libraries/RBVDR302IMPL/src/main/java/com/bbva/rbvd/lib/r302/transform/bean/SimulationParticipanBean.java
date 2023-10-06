package com.bbva.rbvd.lib.r302.transform.bean;

import com.bbva.rbvd.dto.lifeinsrc.dao.SimulationParticipantDAO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;

import java.math.BigDecimal;

public class SimulationParticipanBean {
    public SimulationParticipanBean() {
    }

    public static SimulationParticipantDAO createSimulationParticipant (BigDecimal insuranceSimulationId, LifeSimulationDTO insuranceSimulationDTO,String creationUser, String userAudit, BigDecimal productId){
        SimulationParticipantDAO simulationParticipant = new SimulationParticipantDAO();
        simulationParticipant.setInsuranceSimulationId(insuranceSimulationId);
        simulationParticipant.setInsuranceProductId(productId);
        simulationParticipant.setResponse(insuranceSimulationDTO);
        simulationParticipant.setCreationUser(creationUser);
        simulationParticipant.setUserAudit(userAudit);
        return  simulationParticipant;
    }
}
