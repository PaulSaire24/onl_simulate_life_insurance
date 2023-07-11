package com.bbva.rbvd.lib.r302.transform.map;

import com.bbva.rbvd.dto.lifeinsrc.dao.SimulationDAO;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDProperties;

import java.util.HashMap;
import java.util.Map;

public class SimulationMap {

    private SimulationMap(){}

    public static Map<String, Object> createArgumentsForSaveSimulation(SimulationDAO simulationDAO, String creationUser, String userAudit, String documentTypeId) {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put(RBVDProperties.FIELD_INSURANCE_SIMULATION_ID.getValue(), simulationDAO.getInsuranceSimulationId());
        arguments.put(RBVDProperties.FIELD_INSRNC_COMPANY_SIMULATION_ID.getValue(), simulationDAO.getInsrncCompanySimulationId());
        arguments.put(RBVDProperties.FIELD_CUSTOMER_ID.getValue(), simulationDAO.getCustomerId());
        arguments.put(RBVDProperties.FIELD_CUSTOMER_SIMULATION_DATE.getValue(), simulationDAO.getCustomerSimulationDate());
        arguments.put(RBVDProperties.FIELD_CUST_SIMULATION_EXPIRED_DATE.getValue(), simulationDAO.getCustSimulationExpiredDate());
        arguments.put(RBVDProperties.FIELD_BANK_FACTOR_TYPE.getValue(), simulationDAO.getBankFactorType());
        arguments.put(RBVDProperties.FIELD_BANK_FACTOR_AMOUNT.getValue(), simulationDAO.getBankFactorAmount());
        arguments.put(RBVDProperties.FIELD_BANK_FACTOR_PER.getValue(), simulationDAO.getBankFactorPer());
        arguments.put(RBVDProperties.FIELD_SOURCE_BRANCH_ID.getValue(), simulationDAO.getSourceBranchId());
        arguments.put(RBVDProperties.FIELD_CREATION_USER_ID.getValue(), creationUser);
        arguments.put(RBVDProperties.FIELD_USER_AUDIT_ID.getValue(), userAudit);
        arguments.put(RBVDProperties.FIELD_PERSONAL_DOC_TYPE.getValue(), documentTypeId);
        arguments.put(RBVDProperties.FIELD_PARTICIPANT_PERSONAL_ID.getValue(), simulationDAO.getParticipantPersonalId());
        arguments.put(RBVDProperties.FIELD_INSURED_CUSTOMER_NAME.getValue(), null);
        arguments.put(RBVDProperties.FIELD_CLIENT_LAST_NAME.getValue(), null);
        arguments.put(RBVDProperties.FIELD_CUSTOMER_SEGMENT_NAME.getValue(), null);
        return arguments;
    }

}
