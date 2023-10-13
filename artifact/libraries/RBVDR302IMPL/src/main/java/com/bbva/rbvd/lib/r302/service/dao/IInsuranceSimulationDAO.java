package com.bbva.rbvd.lib.r302.service.dao;

import java.math.BigDecimal;
import java.util.Map;

public interface IInsuranceSimulationDAO {
    BigDecimal getSimulationNextVal();
    void insertInsuranceSimulation(Map<String, Object> argumentsForSaveSimulation);
    void insertSimulationParticipant(Map<String, Object> argumentForSaveParticipant);
}
