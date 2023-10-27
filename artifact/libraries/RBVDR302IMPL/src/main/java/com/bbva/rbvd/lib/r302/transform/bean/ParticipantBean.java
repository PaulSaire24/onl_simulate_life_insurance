package com.bbva.rbvd.lib.r302.transform.bean;

import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.pisd.dto.insurance.bo.customer.CustomerBO;
import com.bbva.rbvd.dto.lifeinsrc.commons.HolderDTO;
import com.bbva.rbvd.dto.lifeinsrc.dao.ContactDetailsDAO;
import com.bbva.rbvd.dto.lifeinsrc.dao.ParticipantDAO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.ParticipantDTO;
import com.bbva.rbvd.lib.r302.transfer.PayloadStore;
import com.bbva.rbvd.lib.r302.util.ConvertUtil;
import com.bbva.rbvd.lib.r302.util.ConstantsUtil;
import com.bbva.rbvd.lib.r302.util.ValidationUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ParticipantBean {
    private ParticipantBean() {}
    public static ParticipantDAO getInformationParticipant (PayloadStore payloadStore){
        ParticipantDAO participantDAO = new ParticipantDAO();
        participantDAO.setParticipantRoleId(BigDecimal.valueOf(ConstantsUtil.Role.INSURED_ID));
        List<ParticipantDTO> participants = payloadStore.getResponse().getParticipants();
        HolderDTO holder = payloadStore.getResponse().getHolder();
        if(!CollectionUtils.isEmpty(participants)){
            ParticipantDTO participant = participants.get(0);
            buildFromParticipant(payloadStore.getDocumentTypeId(), participantDAO, participant);
        }else {
            buildFromHolder(payloadStore.getCustomer(),payloadStore.getDocumentTypeId(), participantDAO, holder);
        }
        return participantDAO;
    }

    private static void buildFromParticipant(String documentTypeId, ParticipantDAO participantDAO, ParticipantDTO participant) {
        participantDAO.setInsuredId(participant.getId());
        participantDAO.setCustomerDocumentType(documentTypeId);
        participantDAO.setInsuredCustomerName(participant.getFirstName());
        participantDAO.setClientLastName(participant.getLastName().concat(ConstantsUtil.RegularExpression.DELIMITER).concat(participant.getSecondLastName()));
        getContactDetails(participantDAO, participant);
        participantDAO.setCustomerBirthDate(ConvertUtil.toLocalDate(participant.getBirthDate()));
        participantDAO.setPersonalId(participant.getIdentityDocument().getDocumentNumber());
        participantDAO.setIsBbvaCustomerType(ValidationUtil.isBBVAClient(participant.getId())? ConstantsUtil.Condition.YES_S: ConstantsUtil.Condition.NO_N);
        participantDAO.setGenderId(participant.getGender().getId().equals(ConstantsUtil.Gender.MALE.getName())? ConstantsUtil.Gender.MALE.getCode():ConstantsUtil.Gender.FEMALE.getCode());
        participantDAO.setCustomerEntryDate(ConvertUtil.toLocalDate(new Date()));
        if(StringUtils.isEmpty(participant.getId())){
            participantDAO.setCustomerEntryDate(null);
        }
    }

    private static void getContactDetails(ParticipantDAO participantDAO, ParticipantDTO participant) {
        participantDAO.setContactDetails(new ContactDetailsDAO());
        participantDAO.getContactDetails().setPhoneId(participant.getContactDetails().get(0).getContact().getNumber());
        participantDAO.getContactDetails().setUserEmailPersonalDesc(participant.getContactDetails().get(1).getContact().getAddress());
    }

    private static void getContactDetails(ParticipantDAO participantDAO) {
        participantDAO.setContactDetails(new ContactDetailsDAO());
        participantDAO.getContactDetails().setPhoneId(null);
        participantDAO.getContactDetails().setUserEmailPersonalDesc(null);
    }

    private static void buildFromHolder(CustomerListASO customers, String documentTypeId, ParticipantDAO participantDAO, HolderDTO holder) {
        participantDAO.setInsuredId(holder.getId());
        participantDAO.setCustomerDocumentType(documentTypeId);
        participantDAO.setInsuredCustomerName(holder.getFirstName());
        participantDAO.setClientLastName(holder.getLastName().replace(" ",ConstantsUtil.RegularExpression.DELIMITER));
        getContactDetails(participantDAO);
        participantDAO.setCustomerBirthDate(null);
        participantDAO.setCustomerEntryDate(ConvertUtil.toLocalDate(new Date()));
        participantDAO.setPersonalId(holder.getIdentityDocument().getDocumentNumber());
        participantDAO.setIsBbvaCustomerType(ConstantsUtil.Condition.YES_S);
        if(Objects.nonNull(customers) && !CollectionUtils.isEmpty(customers.getData())){
            CustomerBO customer = customers.getData().get(0);
            if(Objects.nonNull(customer.getBirthData())){
                participantDAO.setCustomerBirthDate(ConvertUtil.toLocalDate(ModifyQuotationRimac.ParseFecha(customer.getBirthData().getBirthDate())));
            }
            Map<String, String> contactDetails = ConvertUtil.getGroupedByTypeContactDetail(customer);
            participantDAO.getContactDetails().setUserEmailPersonalDesc(contactDetails.get(ConstantsUtil.ContactDetails.EMAIL));
            participantDAO.getContactDetails().setPhoneId(contactDetails.get(ConstantsUtil.ContactDetails.MOBILE_NUMBER));
            participantDAO.setGenderId(customer.getGender().getId().equals(ConstantsUtil.Gender.MALE.getName())? ConstantsUtil.Gender.MALE.getCode():ConstantsUtil.Gender.FEMALE.getCode());
        }
    }
}
