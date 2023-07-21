package com.bbva.rbvd.lib.r302.service.dao.impl;

import com.bbva.pisd.lib.r350.PISDR350;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDErrors;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDProperties;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDValidation;
import com.bbva.rbvd.lib.r302.service.dao.IInsuranceSimulationDAO;
import com.bbva.rbvd.lib.r302.util.ValidationUtil;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


public class InsuranceSimulationDAOImpl implements IInsuranceSimulationDAO {
    private final PISDR350 pisdR350;

    public InsuranceSimulationDAOImpl(PISDR350 pisdR350) {
        this.pisdR350 = pisdR350;
    }

    @Override
    public BigDecimal getSimulationNextVal() {

        Map<String, Object> responseGetInsuranceSimulationMap = this.pisdR350.executeGetASingleRow(RBVDProperties.QUERY_SELECT_INSURANCE_SIMULATION_ID.getValue(),new HashMap<>());

        return (BigDecimal) responseGetInsuranceSimulationMap.get(RBVDProperties.FIELD_Q_PISD_SIMULATION_ID0_NEXTVAL.getValue());
    }

    @Override
    public void getInsertInsuranceSimulation(Map<String, Object> argumentsForSaveSimulation) {

        int idNewSimulation = this.pisdR350.executeInsertSingleRow(RBVDProperties.QUERY_INSERT_INSURANCE_SIMULATION.getValue(),argumentsForSaveSimulation);
        if(idNewSimulation != 1) {
            throw RBVDValidation.build(RBVDErrors.INSERTION_ERROR_IN_SIMULATION_TABLE);
        }
    }
}
