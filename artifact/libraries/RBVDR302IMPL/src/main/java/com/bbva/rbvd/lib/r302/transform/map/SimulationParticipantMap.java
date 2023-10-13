package com.bbva.rbvd.lib.r302.transform.map;

import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.rbvd.dto.lifeinsrc.dao.SimulationParticipantDAO;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDProperties;
import com.bbva.rbvd.lib.r302.util.ConstantsUtil;
import com.bbva.rbvd.lib.r302.transform.bean.ModifyQuotationRimac;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

import static com.bbva.rbvd.lib.r302.util.ConstantsUtil.EMAIL;
import static com.bbva.rbvd.lib.r302.util.ConstantsUtil.MOBILE_NUMBER;
import static com.bbva.rbvd.lib.r302.util.ConvertUtil.validateContactDetails;
import static com.bbva.rbvd.lib.r302.util.ValidationUtil.isBBVAClient;


public class SimulationParticipantMap {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimulationParticipantMap.class);

    private SimulationParticipantMap() {super();}

    public static Map<String, Object> createArgumentsForSaveParticipant(SimulationParticipantDAO simulationParticipant, CustomerListASO customer){
        LOGGER.info("SimulationParticipantMap start - createArgumentsForSaveParticipant");
        Map<String, Object> arguments = new HashMap<>();
        arguments.put(RBVDProperties.FIELD_INSURANCE_SIMULATION_ID.getValue(),simulationParticipant.getInsuranceSimulationId());
        arguments.put(RBVDProperties.FIELD_OR_FILTER_INSURANCE_PRODUCT_ID.getValue(),simulationParticipant.getInsuranceProductId());
        arguments.put(RBVDProperties.FIELD_INSURED_AMOUNT.getValue(),simulationParticipant.getResponse().getInsuredAmount().getAmount());
        arguments.put(RBVDProperties.FIELD_PERIOD_TYPE.getValue(),"A");
        arguments.put(RBVDProperties.FIELD_PERIOD_NUMBER.getValue(),simulationParticipant.getResponse().getTerm().getNumber());
        arguments.put(RBVDProperties.FIELD_CUSTOMER_ENTRY_DATE.getValue(),new Date());

        if(!CollectionUtils.isEmpty(simulationParticipant.getResponse().getListRefunds())){
            arguments.put(RBVDProperties.FIELD_REFUND_PER.getValue(),simulationParticipant.getResponse().getListRefunds().get(0).getUnit().getPercentage());
            arguments.put(RBVDProperties.FIELD_CURRENCY_ID.getValue(),simulationParticipant.getResponse().getListRefunds().get(1).getUnit().getCurrency());
            arguments.put(RBVDProperties.FIELD_TOTAL_RETURN_AMOUNT.getValue(),simulationParticipant.getResponse().getListRefunds().get(1).getUnit().getAmount());
        }
        if(!CollectionUtils.isEmpty(simulationParticipant.getResponse().getParticipants())){
            arguments.put(RBVDProperties.FIELD_INSURED_ID.getValue(),simulationParticipant.getResponse().getParticipants().get(0).getId());
            arguments.put(RBVDProperties.FIELD_CUSTOMER_DOCUMENT_TYPE.getValue(),simulationParticipant.getResponse().getParticipants().get(0).getIdentityDocument().getDocumentType().getId());
            arguments.put(RBVDProperties.FIELD_INSURED_CUSTOMER_NAME.getValue(),simulationParticipant.getResponse().getParticipants().get(0).getFirstName());
            arguments.put(RBVDProperties.FIELD_CLIENT_LAST_NAME.getValue(),simulationParticipant.getResponse().getParticipants().get(0).getLastName());
            arguments.put(RBVDProperties.FIELD_PHONE_ID.getValue(),simulationParticipant.getResponse().getParticipants().get(0).getContactDetails().get(0).getContact().getNumber());
            arguments.put(RBVDProperties.FIELD_CUSTOMER_BIRTH_DATE.getValue(),simulationParticipant.getResponse().getParticipants().get(0).getBirthDate());
            arguments.put(RBVDProperties.FIELD_PERSONAL_ID.getValue(),simulationParticipant.getResponse().getParticipants().get(0).getIdentityDocument().getDocumentNumber());
            arguments.put(RBVDProperties.FIELD_IS_BBVA_CUSTOMER_TYPE.getValue(),isBBVAClient(simulationParticipant.getResponse().getParticipants().get(0).getId())? ConstantsUtil.YES_S : ConstantsUtil.NO_N);

            if(StringUtils.isEmpty(simulationParticipant.getResponse().getParticipants().get(0).getId())){
                arguments.put(RBVDProperties.FIELD_CUSTOMER_ENTRY_DATE.getValue(),null);
            }
            arguments.put(RBVDProperties.FIELD_USER_EMAIL_PERSONAL_DESC.getValue(), simulationParticipant.getResponse().getParticipants().get(0).getContactDetails().get(1).getContact().getAddress());

        }else{
            arguments.put(RBVDProperties.FIELD_INSURED_ID.getValue(),simulationParticipant.getResponse().getHolder().getId());
            arguments.put(RBVDProperties.FIELD_CUSTOMER_DOCUMENT_TYPE.getValue(),simulationParticipant.getResponse().getHolder().getIdentityDocument().getDocumentType().getId());
            arguments.put(RBVDProperties.FIELD_INSURED_CUSTOMER_NAME.getValue(),simulationParticipant.getResponse().getHolder().getFirstName());
            arguments.put(RBVDProperties.FIELD_CLIENT_LAST_NAME.getValue(),simulationParticipant.getResponse().getHolder().getLastName());
            arguments.put(RBVDProperties.FIELD_PERSONAL_ID.getValue(),simulationParticipant.getResponse().getHolder().getIdentityDocument().getDocumentNumber());
            arguments.put(RBVDProperties.FIELD_USER_EMAIL_PERSONAL_DESC.getValue(),null);
            arguments.put(RBVDProperties.FIELD_PHONE_ID.getValue(),null);
            arguments.put(RBVDProperties.FIELD_CUSTOMER_BIRTH_DATE.getValue(),null);
            arguments.put(RBVDProperties.FIELD_IS_BBVA_CUSTOMER_TYPE.getValue(),ConstantsUtil.YES_S);

            if(Objects.nonNull(customer) && StringUtils.isNotEmpty(customer.getData().get(0).getBirthData().getBirthDate())){
                arguments.put(RBVDProperties.FIELD_CUSTOMER_BIRTH_DATE.getValue(),ModifyQuotationRimac.ParseFecha(customer.getData().get(0).getBirthData().getBirthDate()));
                Map<String, String> contactDetails = validateContactDetails(customer.getData().get(0));
                arguments.put(RBVDProperties.FIELD_USER_EMAIL_PERSONAL_DESC.getValue(),contactDetails.get(EMAIL));
                arguments.put(RBVDProperties.FIELD_PHONE_ID.getValue(),contactDetails.get(MOBILE_NUMBER));
            }
        }
        arguments.put(RBVDProperties.FIELD_CREATION_USER_ID.getValue(),simulationParticipant.getCreationUser());
        arguments.put(RBVDProperties.FIELD_USER_AUDIT_ID.getValue(),simulationParticipant.getUserAudit());
        arguments.put(RBVDProperties.FIELD_PARTICIPANT_ROLE_ID.getValue(),ConstantsUtil.IS_INSURED);
        LOGGER.info("SimulationParticipantMap end");
        return arguments;
    }
}
