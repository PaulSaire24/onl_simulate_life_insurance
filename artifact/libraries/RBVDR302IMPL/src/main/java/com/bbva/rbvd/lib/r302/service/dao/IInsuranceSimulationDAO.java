package com.bbva.rbvd.lib.r302.service.dao;

import java.math.BigDecimal;
import java.util.Map;

public interface IInsuranceSimulationDAO {
    BigDecimal getSimulationNextVal();

    void getInsertInsuranceSimulation (Map<String, Object> argumentsForSaveSimulation);
    void getInsertSimulationParticipant(Map<String, Object> argumentForSaveParticipant);
}
