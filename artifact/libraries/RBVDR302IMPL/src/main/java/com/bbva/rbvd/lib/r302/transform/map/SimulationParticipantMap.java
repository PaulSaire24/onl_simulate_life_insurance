package com.bbva.rbvd.lib.r302.transform.map;

import com.bbva.rbvd.dto.lifeinsrc.dao.CommonsLifeDAO;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.HashMap;


public class SimulationParticipantMap {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimulationParticipantMap.class);

    private SimulationParticipantMap() {super();}

    public static Map<String, Object> createArgumentsForSaveParticipant(CommonsLifeDAO commonsLife){
        LOGGER.info("SimulationParticipantMap start - createArgumentsForSaveParticipant");
        Map<String, Object> arguments = new HashMap<>();
        arguments.put(RBVDProperties.FIELD_INSURANCE_SIMULATION_ID.getValue(),commonsLife.getInsuranceSimulationId());
        arguments.put(RBVDProperties.FIELD_OR_FILTER_INSURANCE_PRODUCT_ID.getValue(),commonsLife.getInsuranceProductId());
        arguments.put(RBVDProperties.FIELD_INSURED_AMOUNT.getValue(),commonsLife.getInsuredAmount());
        arguments.put(RBVDProperties.FIELD_PERIOD_TYPE.getValue(),commonsLife.getPeriodType());
        arguments.put(RBVDProperties.FIELD_PERIOD_NUMBER.getValue(),commonsLife.getPeriodNumber());
        arguments.put(RBVDProperties.FIELD_CUSTOMER_ENTRY_DATE.getValue(),commonsLife.getCustomerEntryDate());
        arguments.put(RBVDProperties.FIELD_REFUND_PER.getValue(),commonsLife.getRefundPer());
        arguments.put(RBVDProperties.FIELD_CURRENCY_ID.getValue(),commonsLife.getCurrencyId());
        arguments.put(RBVDProperties.FIELD_TOTAL_RETURN_AMOUNT.getValue(),commonsLife.getTotalReturnAmount());
        arguments.put(RBVDProperties.FIELD_INSURED_ID.getValue(),commonsLife.getInsuredId());
        arguments.put(RBVDProperties.FIELD_CUSTOMER_DOCUMENT_TYPE.getValue(),commonsLife.getCustomerDocumentType());
        arguments.put(RBVDProperties.FIELD_INSURED_CUSTOMER_NAME.getValue(),commonsLife.getInsuredCustomerName());
        arguments.put(RBVDProperties.FIELD_CLIENT_LAST_NAME.getValue(),commonsLife.getClientLastName());
        arguments.put(RBVDProperties.FIELD_PHONE_ID.getValue(),commonsLife.getPhoneId());
        arguments.put(RBVDProperties.FIELD_CUSTOMER_BIRTH_DATE.getValue(),commonsLife.getCustomerBirthDate());
        arguments.put(RBVDProperties.FIELD_PERSONAL_ID.getValue(),commonsLife.getPersonalId());
        arguments.put(RBVDProperties.FIELD_IS_BBVA_CUSTOMER_TYPE.getValue(),commonsLife.getIsBbvaCustomerType());
        arguments.put(RBVDProperties.FIELD_USER_EMAIL_PERSONAL_DESC.getValue(),commonsLife.getUserEmailPersonalDesc());
        arguments.put(RBVDProperties.FIELD_CREATION_USER_ID.getValue(),commonsLife.getCreationUser());
        arguments.put(RBVDProperties.FIELD_USER_AUDIT_ID.getValue(),commonsLife.getUserAudit());
        arguments.put(RBVDProperties.FIELD_PARTICIPANT_ROLE_ID.getValue(),commonsLife.getParticipantRoleId());
        arguments.put(RBVDProperties.FIELD_GENDER_ID.getValue(),commonsLife.getGenderId());
        LOGGER.info("SimulationParticipantMap end");
        return arguments;
    }
}
