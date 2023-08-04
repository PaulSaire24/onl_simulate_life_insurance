package com.bbva.rbvd.lib.r302.transform.bean;

import com.bbva.rbvd.dto.lifeinsrc.dao.SimulationProductDAO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;

import java.math.BigDecimal;

public class SimulationProductBean {

    private SimulationProductBean(){}

    public static SimulationProductDAO createSimulationProductDAO(BigDecimal insuranceSimulationId, BigDecimal productId, String creationUser, String userAudit, LifeSimulationDTO insuranceSimulationDto) {
        SimulationProductDAO simulationProductDAO = new SimulationProductDAO();
        simulationProductDAO.setInsuranceSimulationId(insuranceSimulationId);
        simulationProductDAO.setInsuranceProductId(productId);
        simulationProductDAO.setSaleChannelId(insuranceSimulationDto.getSaleChannelId());
        simulationProductDAO.setCreationUser(creationUser);
        simulationProductDAO.setUserAudit(userAudit);
        return simulationProductDAO;
    }

}
