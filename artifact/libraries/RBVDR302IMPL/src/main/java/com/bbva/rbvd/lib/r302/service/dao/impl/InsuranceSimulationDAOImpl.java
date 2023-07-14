package com.bbva.rbvd.lib.r302.service.dao.impl;

import com.bbva.pisd.lib.r350.PISDR350;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDErrors;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDProperties;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDValidation;
import com.bbva.rbvd.lib.r302.service.dao.IInsuranceSimulationDAO;
import com.bbva.rbvd.lib.r302.util.ValidationUtil;

import java.math.BigDecimal;
import java.util.Map;

import static org.springframework.util.CollectionUtils.isEmpty;

public class InsuranceSimulationDAOImpl implements IInsuranceSimulationDAO {
    private final PISDR350 pisdR350;
    private ValidationUtil validationUtil;

    public InsuranceSimulationDAOImpl(PISDR350 pisdR350) {
        this.pisdR350 = pisdR350;
    }

    @Override
    public BigDecimal getSimulationNextVal() {
        Map<String, Object> responseGetInsuranceSimulationId = this.pisdR350.executeGetASingleRow(RBVDProperties.QUERY_SELECT_INSURANCE_SIMULATION_ID.getValue(),null);

        if(isEmpty(responseGetInsuranceSimulationId)) {
            throw RBVDValidation.build(RBVDErrors.WRONG_PLAN_CODES);
        }

        BigDecimal insuranceSimulationId = (BigDecimal) responseGetInsuranceSimulationId.get(RBVDProperties.FIELD_Q_PISD_SIMULATION_ID0_NEXTVAL.getValue());

        return insuranceSimulationId;
    }

    @Override
    public void getInsertInsuranceSimulation(Map<String, Object> argumentsForSaveSimulation) {
        //LOGGER.info("***** PISDR302Impl - Invoking PISDR350 QUERY_INSERT_INSURANCE_SIMULATION *****");
        validationUtil.validateInsertion
                (
                        this.pisdR350.executeInsertSingleRow
                        (
                                RBVDProperties.QUERY_INSERT_INSURANCE_SIMULATION.getValue(),
                                argumentsForSaveSimulation
                        ),
                        RBVDErrors.INSERTION_ERROR_IN_SIMULATION_TABLE
                );
    }
}
