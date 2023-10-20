package com.bbva.rbvd.lib.r302.transform.bean;

import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.pisd.dto.insurance.bo.customer.CustomerBO;
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
        List<ParticipantDTO> participants = payloadStore.getResponse().getParticipants();
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

        if(!CollectionUtils.isEmpty(participants)){
            ParticipantDTO participant = participants.get(0);
            buildFromParticipant(payloadStore.getDocumentTypeId(), simulationParticipant, participant);
        }else {
            buildFromHolder(payloadStore.getDocumentTypeId(),payloadStore.getCustomer(), simulationParticipant, holder);
        }
        simulationParticipant.setParticipantRoleId(BigDecimal.valueOf(ConstantsUtil.Role.INSURED_ID));
        simulationParticipant.setCreationUser(payloadStore.getCreationUser());
        simulationParticipant.setUserAudit(payloadStore.getUserAudit());

        return  simulationParticipant;
    }
    private static void buildFromParticipant(String documentTypeId, SimulationParticipantDAO simulationParticipant, ParticipantDTO participant) {
        simulationParticipant.setInsuredId(participant.getId());
        simulationParticipant.setCustomerDocumentType(documentTypeId);
        simulationParticipant.setInsuredCustomerName(participant.getFirstName());
        simulationParticipant.setClientLastName(participant.getLastName().concat(ConstantsUtil.RegularExpression.DELIMITER).concat(participant.getSecondLastName()));
        simulationParticipant.setPhoneId(participant.getContactDetails().get(0).getContact().getNumber());
        simulationParticipant.setCustomerBirthDate(ConvertUtil.toLocalDate(participant.getBirthDate()));
        simulationParticipant.setPersonalId(participant.getIdentityDocument().getDocumentNumber());
        simulationParticipant.setUserEmailPersonalDesc(participant.getContactDetails().get(1).getContact().getAddress());
        simulationParticipant.setIsBbvaCustomerType(ValidationUtil.isBBVAClient(participant.getId())? ConstantsUtil.Condition.YES_S: ConstantsUtil.Condition.NO_N);
        simulationParticipant.setGenderId(participant.getGender().getId().equals(ConstantsUtil.Gender.MALE.getName())? ConstantsUtil.Gender.MALE.getCode():ConstantsUtil.Gender.FEMALE.getCode());
        if(StringUtils.isEmpty(participant.getId())){
            simulationParticipant.setCustomerEntryDate(null);
        }
    }
    private static void buildFromHolder(String documentTypeId, CustomerListASO customers, SimulationParticipantDAO simulationParticipant, HolderDTO holder) {
        simulationParticipant.setInsuredId(holder.getId());
        simulationParticipant.setCustomerDocumentType(documentTypeId);
        simulationParticipant.setInsuredCustomerName(holder.getFirstName());
        simulationParticipant.setClientLastName(holder.getLastName().replace(" ",ConstantsUtil.RegularExpression.DELIMITER));
        simulationParticipant.setPhoneId(null);
        simulationParticipant.setCustomerBirthDate(null);
        simulationParticipant.setPersonalId(holder.getIdentityDocument().getDocumentNumber());
        simulationParticipant.setIsBbvaCustomerType(ConstantsUtil.Condition.YES_S);
        simulationParticipant.setUserEmailPersonalDesc(null);
        if(Objects.nonNull(customers) && !CollectionUtils.isEmpty(customers.getData())){
            CustomerBO customer = customers.getData().get(0);
            if(Objects.nonNull(customer.getBirthData())){
                simulationParticipant.setCustomerBirthDate(ConvertUtil.toLocalDate(ModifyQuotationRimac.ParseFecha(customer.getBirthData().getBirthDate())));
            }
            Map<String, String> contactDetails = ConvertUtil.getGroupedByTypeContactDetail(customer);
            simulationParticipant.setUserEmailPersonalDesc(contactDetails.get(ConstantsUtil.ContactDetails.EMAIL));
            simulationParticipant.setPhoneId(contactDetails.get(ConstantsUtil.ContactDetails.MOBILE_NUMBER));
            simulationParticipant.setGenderId(customer.getGender().getId().equals(ConstantsUtil.Gender.MALE.getName())? ConstantsUtil.Gender.MALE.getCode():ConstantsUtil.Gender.FEMALE.getCode());
        }
    }

    private static BigDecimal getInsuredAmount(InsuredAmountDTO insuredAmount){
        return Objects.nonNull(insuredAmount)? insuredAmount.getAmount():null;
    }
    private static BigDecimal getPeriodNumber(TermDTO term){
        return Objects.nonNull(term)? BigDecimal.valueOf(term.getNumber()):null;
    }
}
