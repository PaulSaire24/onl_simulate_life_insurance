package com.bbva.rbvd.lib.r302.transform.map;

import com.bbva.rbvd.dto.lifeinsrc.dao.InsuredLifeDAO;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.HashMap;


public class SimulationParticipantMap {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimulationParticipantMap.class);

    private SimulationParticipantMap() {super();}

    public static Map<String, Object> createArgumentsForSaveParticipant(InsuredLifeDAO insuredLife){
        LOGGER.info("SimulationParticipantMap start - createArgumentsForSaveParticipant");
        Map<String, Object> arguments = new HashMap<>();
        arguments.put(RBVDProperties.FIELD_INSURANCE_SIMULATION_ID.getValue(),insuredLife.getInsuranceSimulationId());
        arguments.put(RBVDProperties.FIELD_OR_FILTER_INSURANCE_PRODUCT_ID.getValue(),insuredLife.getInsuranceProductId());
        arguments.put(RBVDProperties.FIELD_INSURED_AMOUNT.getValue(),insuredLife.getInsuredAmount());
        arguments.put(RBVDProperties.FIELD_PERIOD_TYPE.getValue(),insuredLife.getTerm().getPeriodType());
        arguments.put(RBVDProperties.FIELD_PERIOD_NUMBER.getValue(),insuredLife.getTerm().getPeriodNumber());
        arguments.put(RBVDProperties.FIELD_CUSTOMER_ENTRY_DATE.getValue(),insuredLife.getParticipant().getCustomerEntryDate());
        arguments.put(RBVDProperties.FIELD_REFUND_PER.getValue(),insuredLife.getRefunds().getRefundPer());
        arguments.put(RBVDProperties.FIELD_CURRENCY_ID.getValue(),insuredLife.getRefunds().getCurrencyId());
        arguments.put(RBVDProperties.FIELD_TOTAL_RETURN_AMOUNT.getValue(),insuredLife.getRefunds().getTotalReturnAmount());
        arguments.put(RBVDProperties.FIELD_INSURED_ID.getValue(),insuredLife.getParticipant().getInsuredId());
        arguments.put(RBVDProperties.FIELD_CUSTOMER_DOCUMENT_TYPE.getValue(),insuredLife.getParticipant().getCustomerDocumentType());
        arguments.put(RBVDProperties.FIELD_INSURED_CUSTOMER_NAME.getValue(),insuredLife.getParticipant().getInsuredCustomerName());
        arguments.put(RBVDProperties.FIELD_CLIENT_LAST_NAME.getValue(),insuredLife.getParticipant().getClientLastName());
        arguments.put(RBVDProperties.FIELD_PHONE_ID.getValue(),insuredLife.getParticipant().getContactDetails().getPhoneId());
        arguments.put(RBVDProperties.FIELD_CUSTOMER_BIRTH_DATE.getValue(),insuredLife.getParticipant().getCustomerBirthDate());
        arguments.put(RBVDProperties.FIELD_PERSONAL_ID.getValue(),insuredLife.getParticipant().getPersonalId());
        arguments.put(RBVDProperties.FIELD_IS_BBVA_CUSTOMER_TYPE.getValue(),insuredLife.getParticipant().getIsBbvaCustomerType());
        arguments.put(RBVDProperties.FIELD_USER_EMAIL_PERSONAL_DESC.getValue(),insuredLife.getParticipant().getContactDetails().getUserEmailPersonalDesc());
        arguments.put(RBVDProperties.FIELD_CREATION_USER_ID.getValue(),insuredLife.getCreationUser());
        arguments.put(RBVDProperties.FIELD_USER_AUDIT_ID.getValue(),insuredLife.getUserAudit());
        arguments.put(RBVDProperties.FIELD_PARTICIPANT_ROLE_ID.getValue(),insuredLife.getParticipant().getParticipantRoleId());
        arguments.put(RBVDProperties.FIELD_GENDER_ID.getValue(),insuredLife.getParticipant().getGenderId());
        LOGGER.info("insuredLifeMap end");
        return arguments;
    }
}
