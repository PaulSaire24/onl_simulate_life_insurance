package com.bbva.rbvd.lib.r302.transform.map;

import com.bbva.rbvd.dto.lifeinsrc.dao.SimulationParticipantDAO;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDProperties;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;
import java.util.Date;


public class SimulationParticipantMap {

    private SimulationParticipantMap() {super();}
    private static final ZoneId ZONE_ID = ZoneId.of("GMT");
    public static Map<String, Object> createArgumentsForSaveParticipant(SimulationParticipantDAO simulationParticipant){
        Map<String, Object> arguments = new HashMap<>();
        arguments.put(RBVDProperties.FIELD_INSURANCE_SIMULATION_ID.getValue(),simulationParticipant.getInsuranceSimulationId());
        arguments.put(RBVDProperties.FIELD_OR_FILTER_INSURANCE_PRODUCT_ID.getValue(),simulationParticipant.getInsuranceProductId());
        arguments.put(RBVDProperties.FIELD_OR_FILTER_INSURANCE_PRODUCT_ID.getValue(),null);
        arguments.put(RBVDProperties.FIELD_INSURED_AMOUNT.getValue(),simulationParticipant.getResponse().getInsuredAmount().getAmount());
        if(!CollectionUtils.isEmpty(simulationParticipant.getResponse().getProduct().getPlans())
                && Objects.nonNull(simulationParticipant.getResponse().getProduct().getPlans().get(0).getInstallmentPlans())
                && Objects.nonNull(simulationParticipant.getResponse().getProduct().getPlans().get(0).getInstallmentPlans().get(0).getPeriod())){

            arguments.put(RBVDProperties.FIELD_PERIOD_TYPE.getValue(),simulationParticipant.getResponse().getProduct().getPlans().get(0).getInstallmentPlans().get(0).getPeriod().getId());
            arguments.put(RBVDProperties.FIELD_PERIOD_NUMBER.getValue(),null);
        }else {
            arguments.put(RBVDProperties.FIELD_PERIOD_TYPE.getValue(), null);
            arguments.put(RBVDProperties.FIELD_PERIOD_NUMBER.getValue(),null);
        }
        if(!CollectionUtils.isEmpty(simulationParticipant.getResponse().getListRefunds())){
            arguments.put(RBVDProperties.FIELD_REFUND_PER.getValue(),simulationParticipant.getResponse().getListRefunds().get(0).getUnit().getPercentage());
            arguments.put(RBVDProperties.FIELD_PREMIUM_CURRENCY_ID.getValue(),null);
        }
        arguments.put(RBVDProperties.FIELD_TOTAL_RETURN_AMOUNT.getValue(),null);

        if(!CollectionUtils.isEmpty(simulationParticipant.getResponse().getParticipants())){
            arguments.put(RBVDProperties.FIELD_INSURED_ID.getValue(),simulationParticipant.getResponse().getParticipants().get(0).getParticipantType().getId());
            arguments.put(RBVDProperties.FIELD_CUSTOMER_DOCUMENT_TYPE.getValue(),simulationParticipant.getResponse().getParticipants().get(0).getIdentityDocument().getDocumentType().getId());
            arguments.put(RBVDProperties.FIELD_INSURED_CUSTOMER_NAME.getValue(),simulationParticipant.getResponse().getParticipants().get(0).getFirstName());
            arguments.put(RBVDProperties.FIELD_CLIENT_LAST_NAME.getValue(),simulationParticipant.getResponse().getParticipants().get(0).getLastName());
            arguments.put(RBVDProperties.FIELD_PHONE_ID.getValue(),simulationParticipant.getResponse().getParticipants().get(0).getContactDetails().get(0).getContact().getNumber());
            arguments.put(RBVDProperties.FIELD_CUSTOMER_BIRTH_DATE.getValue(),toISO8601(simulationParticipant.getResponse().getParticipants().get(0).getBirthDate()));
            if(Objects.nonNull(simulationParticipant.getResponse().getParticipants().get(0).getContactDetails().get(1))) {
                arguments.put(RBVDProperties.FIELD_USER_EMAIL_PERSONAL_DESC.getValue(), simulationParticipant.getResponse().getParticipants().get(0).getContactDetails().get(1).getContact().getAddress());
            }else{
                arguments.put(RBVDProperties.FIELD_USER_EMAIL_PERSONAL_DESC.getValue(),null);
            }
        }else{
            arguments.put(RBVDProperties.FIELD_INSURED_ID.getValue(),null);
            arguments.put(RBVDProperties.FIELD_CUSTOMER_DOCUMENT_TYPE.getValue(),simulationParticipant.getResponse().getHolder().getIdentityDocument().getDocumentType().getId());
            arguments.put(RBVDProperties.FIELD_INSURED_CUSTOMER_NAME.getValue(),simulationParticipant.getResponse().getHolder().getFirstName());
            arguments.put(RBVDProperties.FIELD_CLIENT_LAST_NAME.getValue(),simulationParticipant.getResponse().getHolder().getLastName());
            arguments.put(RBVDProperties.FIELD_USER_EMAIL_PERSONAL_DESC.getValue(),null);

        }
        arguments.put(RBVDProperties.FIELD_PERSONAL_ID.getValue(),null);
        arguments.put(RBVDProperties.FIELD_IS_BBVA_CUSTOMER_TYPE.getValue(),null);
        arguments.put(RBVDProperties.FIELD_CUSTOMER_ENTRY_DATE.getValue(),null);
        arguments.put(RBVDProperties.FIELD_PARTICIPANT_ROLE_ID.getValue(),null);
        arguments.put(RBVDProperties.FIELD_CREATION_USER_ID.getValue(),simulationParticipant.getCreationUser());
        arguments.put(RBVDProperties.FIELD_USER_AUDIT_ID.getValue(),simulationParticipant.getUserAudit());
        return arguments;
    }

    public static LocalDateTime toISO8601(Date date) {
        if(Objects.isNull(date)){
            return null;
        }
        return date.toInstant().atZone(ZONE_ID).toLocalDateTime();
    }
}
