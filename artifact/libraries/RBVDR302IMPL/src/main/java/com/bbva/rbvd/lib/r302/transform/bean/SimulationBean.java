package com.bbva.rbvd.lib.r302.transform.bean;

import com.bbva.rbvd.dto.lifeinsrc.dao.SimulationDAO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SimulationBean {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    private SimulationBean(){}

    public static SimulationDAO createSimulationDAO(BigDecimal insuranceSimulationId, final Date maturityDate, LifeSimulationDTO insuranceSimulationDTO) {
        SimulationDAO simulationDAO = new SimulationDAO();
        simulationDAO.setInsuranceSimulationId(insuranceSimulationId);
        simulationDAO.setInsrncCompanySimulationId(insuranceSimulationDTO.getExternalSimulationId());
        simulationDAO.setCustomerId(insuranceSimulationDTO.getHolder().getId());
        simulationDAO.setCustomerSimulationDate(dateFormat.format(new Date()));
        simulationDAO.setCustSimulationExpiredDate(dateFormat.format(maturityDate));
        simulationDAO.setParticipantPersonalId(insuranceSimulationDTO.getHolder().getIdentityDocument().getDocumentNumber());

        return simulationDAO;
    }

}
