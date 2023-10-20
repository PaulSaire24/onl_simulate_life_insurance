package com.bbva.rbvd.lib.r302.transform.bean;

import com.bbva.rbvd.dto.lifeinsrc.commons.HolderDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.InsuredAmountDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.TermDTO;
import com.bbva.rbvd.dto.lifeinsrc.dao.SimulationParticipantDAO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.ParticipantDTO;
import com.bbva.rbvd.lib.r302.transfer.PayloadStore;
import com.bbva.rbvd.lib.r302.util.ConstantsUtil;
import com.bbva.rbvd.lib.r302.util.ConvertUtil;
import com.bbva.rbvd.lib.r302.util.ValidationUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class SimulationParticipanBean {

    private SimulationParticipanBean() {
    }

    public static SimulationParticipantDAO createSimulationParticipant (BigDecimal insuranceSimulationId,PayloadStore payloadStore){
        SimulationParticipantDAO simulationParticipant = new SimulationParticipantDAO();
        List<ParticipantDTO> participant = payloadStore.getResponse().getParticipants();
        HolderDTO holder = payloadStore.getResponse().getHolder();
        simulationParticipant.setInsuranceSimulationId(insuranceSimulationId);
        simulationParticipant.setInsuranceProductId(payloadStore.getProductInformation().getInsuranceProductId());
        simulationParticipant.setInsuredAmount(getInsuredAmount(payloadStore.getResponse().getInsuredAmount()));
        simulationParticipant.setPeriodNumber(getPeriodNumber(payloadStore.getResponse().getTerm()));
        simulationParticipant.setPeriodType(ConstantsUtil.Period.ANNUAL.getCode());
        simulationParticipant.setCustomerEntryDate(ConvertUtil.toLocalDate(new Date()));

        if(!CollectionUtils.isEmpty(payloadStore.getResponse().getListRefunds())){
            simulationParticipant.setRefundPer(payloadStore.getResponse().getListRefunds().get(0).getUnit().getPercentage());
            simulationParticipant.setCurrencyId(payloadStore.getResponse().getListRefunds().get(1).getUnit().getCurrency());
            simulationParticipant.setTotalReturnAmount(payloadStore.getResponse().getListRefunds().get(1).getUnit().getAmount());
        }

        if(!CollectionUtils.isEmpty(participant)){
            simulationParticipant.setInsuredId(participant.get(0).getId());
            simulationParticipant.setCustomerDocumentType(payloadStore.getDocumentTypeId());
            simulationParticipant.setInsuredCustomerName(participant.get(0).getFirstName());
            simulationParticipant.setClientLastName(participant.get(0).getLastName().concat(ConstantsUtil.RegularExpression.DELIMITER).concat(participant.get(0).getSecondLastName()));
            simulationParticipant.setPhoneId(participant.get(0).getContactDetails().get(0).getContact().getNumber());
            simulationParticipant.setCustomerBirthDate(ConvertUtil.toLocalDate(participant.get(0).getBirthDate()));
            simulationParticipant.setPersonalId(participant.get(0).getIdentityDocument().getDocumentNumber());
            simulationParticipant.setUserEmailPersonalDesc(participant.get(0).getContactDetails().get(1).getContact().getAddress());
            simulationParticipant.setIsBbvaCustomerType(ValidationUtil.isBBVAClient(participant.get(0).getId())? ConstantsUtil.ConditionalExpressions.YES_S:ConstantsUtil.ConditionalExpressions.NO_N);
            simulationParticipant.setGenderId(participant.get(0).getGender().getId().equals(ConstantsUtil.Gender.MALE.getName())? ConstantsUtil.Gender.MALE.getCode():ConstantsUtil.Gender.FEMALE.getCode());
            if(StringUtils.isEmpty(participant.get(0).getId())){
                simulationParticipant.setCustomerEntryDate(null);
            }
        }else {
            simulationParticipant.setInsuredId(holder.getId());
            simulationParticipant.setCustomerDocumentType(payloadStore.getDocumentTypeId());
            simulationParticipant.setInsuredCustomerName(holder.getFirstName());
            simulationParticipant.setClientLastName(holder.getLastName().replace(" ",ConstantsUtil.RegularExpression.DELIMITER));
            simulationParticipant.setPhoneId(null);
            simulationParticipant.setCustomerBirthDate(null);
            simulationParticipant.setPersonalId(holder.getIdentityDocument().getDocumentNumber());
            simulationParticipant.setIsBbvaCustomerType(ConstantsUtil.ConditionalExpressions.YES_S);
            simulationParticipant.setUserEmailPersonalDesc(null);
            if(Objects.nonNull(payloadStore.getCustomer()) && StringUtils.isNotEmpty(payloadStore.getCustomer().getData().get(0).getBirthData().getBirthDate())){
                simulationParticipant.setCustomerBirthDate(ConvertUtil.toLocalDate(ModifyQuotationRimac.ParseFecha(payloadStore.getCustomer().getData().get(0).getBirthData().getBirthDate())));
                Map<String, String> contactDetails = ConvertUtil.getGroupedByTypeContactDetail(payloadStore.getCustomer().getData().get(0));
                simulationParticipant.setUserEmailPersonalDesc(contactDetails.get(ConstantsUtil.ContactDetails.EMAIL));
                simulationParticipant.setPhoneId(contactDetails.get(ConstantsUtil.ContactDetails.MOBILE_NUMBER));
                simulationParticipant.setGenderId(payloadStore.getCustomer().getData().get(0).getGender().getId().equals(ConstantsUtil.Gender.MALE.getName())? ConstantsUtil.Gender.MALE.getCode():ConstantsUtil.Gender.FEMALE.getCode());
            }
        }
        simulationParticipant.setParticipantRoleId(BigDecimal.valueOf(ConstantsUtil.RoleId.IS_INSURED));
        simulationParticipant.setCreationUser(payloadStore.getCreationUser());
        simulationParticipant.setUserAudit(payloadStore.getUserAudit());

        return  simulationParticipant;
    }
    private static BigDecimal getInsuredAmount(InsuredAmountDTO insuredAmount){
        return Objects.nonNull(insuredAmount)? insuredAmount.getAmount():null;
    }
    private static BigDecimal getPeriodNumber(TermDTO term){
        return Objects.nonNull(term)? BigDecimal.valueOf(term.getNumber()):null;
    }
}
