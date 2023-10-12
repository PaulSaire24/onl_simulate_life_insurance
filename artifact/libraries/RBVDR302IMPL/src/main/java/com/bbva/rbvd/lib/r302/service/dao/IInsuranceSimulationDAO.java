package com.bbva.rbvd.lib.r302.service.dao;

import java.math.BigDecimal;
import java.util.Map;

public interface IInsuranceSimulationDAO {
    BigDecimal getSimulationNextVal();
    void InsertInsuranceSimulation (Map<String, Object> argumentsForSaveSimulation);
    void InsertSimulationParticipant(Map<String, Object> argumentForSaveParticipant);
}
