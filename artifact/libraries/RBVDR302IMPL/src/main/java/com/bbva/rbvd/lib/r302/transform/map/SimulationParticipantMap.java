package com.bbva.rbvd.lib.r302.transform.map;

import com.bbva.rbvd.dto.lifeinsrc.dao.SimulationParticipantDAO;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.HashMap;


public class SimulationParticipantMap {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimulationParticipantMap.class);

    private SimulationParticipantMap() {super();}

    public static Map<String, Object> createArgumentsForSaveParticipant(SimulationParticipantDAO simulationParticipant){
        LOGGER.info("SimulationParticipantMap start - createArgumentsForSaveParticipant");
        Map<String, Object> arguments = new HashMap<>();
        arguments.put(RBVDProperties.FIELD_INSURANCE_SIMULATION_ID.getValue(),simulationParticipant.getInsuranceSimulationId());
        arguments.put(RBVDProperties.FIELD_OR_FILTER_INSURANCE_PRODUCT_ID.getValue(),simulationParticipant.getInsuranceProductId());
        arguments.put(RBVDProperties.FIELD_INSURED_AMOUNT.getValue(),simulationParticipant.getInsuredAmount());
        arguments.put(RBVDProperties.FIELD_PERIOD_TYPE.getValue(),simulationParticipant.getPeriodType());
        arguments.put(RBVDProperties.FIELD_PERIOD_NUMBER.getValue(),simulationParticipant.getPeriodNumber());
        arguments.put(RBVDProperties.FIELD_CUSTOMER_ENTRY_DATE.getValue(),simulationParticipant.getCustomerEntryDate());
        arguments.put(RBVDProperties.FIELD_REFUND_PER.getValue(),simulationParticipant.getRefundPer());
        arguments.put(RBVDProperties.FIELD_CURRENCY_ID.getValue(),simulationParticipant.getCurrencyId());
        arguments.put(RBVDProperties.FIELD_TOTAL_RETURN_AMOUNT.getValue(),simulationParticipant.getTotalReturnAmount());
        arguments.put(RBVDProperties.FIELD_INSURED_ID.getValue(),simulationParticipant.getInsuredId());
        arguments.put(RBVDProperties.FIELD_CUSTOMER_DOCUMENT_TYPE.getValue(),simulationParticipant.getCustomerDocumentType());
        arguments.put(RBVDProperties.FIELD_INSURED_CUSTOMER_NAME.getValue(),simulationParticipant.getInsuredCustomerName());
        arguments.put(RBVDProperties.FIELD_CLIENT_LAST_NAME.getValue(),simulationParticipant.getClientLastName());
        arguments.put(RBVDProperties.FIELD_PHONE_ID.getValue(),simulationParticipant.getPhoneId());
        arguments.put(RBVDProperties.FIELD_CUSTOMER_BIRTH_DATE.getValue(),simulationParticipant.getCustomerBirthDate());
        arguments.put(RBVDProperties.FIELD_PERSONAL_ID.getValue(),simulationParticipant.getPersonalId());
        arguments.put(RBVDProperties.FIELD_IS_BBVA_CUSTOMER_TYPE.getValue(),simulationParticipant.getIsBbvaCustomerType());
        arguments.put(RBVDProperties.FIELD_USER_EMAIL_PERSONAL_DESC.getValue(),simulationParticipant.getUserEmailPersonalDesc());
        arguments.put(RBVDProperties.FIELD_CREATION_USER_ID.getValue(),simulationParticipant.getCreationUser());
        arguments.put(RBVDProperties.FIELD_USER_AUDIT_ID.getValue(),simulationParticipant.getUserAudit());
        arguments.put(RBVDProperties.FIELD_PARTICIPANT_ROLE_ID.getValue(),simulationParticipant.getParticipantRoleId());
        arguments.put(RBVDProperties.FIELD_GENDER_ID.getValue(),simulationParticipant.getGenderId());
        LOGGER.info("SimulationParticipantMap end");
        return arguments;
    }
}
