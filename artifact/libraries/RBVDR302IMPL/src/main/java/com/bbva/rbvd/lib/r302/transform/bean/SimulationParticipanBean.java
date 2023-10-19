package com.bbva.rbvd.lib.r302.transform.bean;

import com.bbva.rbvd.dto.lifeinsrc.commons.InsuredAmountDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.TermDTO;
import com.bbva.rbvd.dto.lifeinsrc.dao.SimulationParticipantDAO;
import com.bbva.rbvd.lib.r302.transfer.PayloadStore;
import com.bbva.rbvd.lib.r302.util.ConstantsUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

import static com.bbva.rbvd.lib.r302.util.ConstantsUtil.EMAIL;
import static com.bbva.rbvd.lib.r302.util.ConstantsUtil.MOBILE_NUMBER;
import static com.bbva.rbvd.lib.r302.util.ConvertUtil.getGroupedByTypeContactDetail;
import static com.bbva.rbvd.lib.r302.util.ConvertUtil.toLocalDate;
import static com.bbva.rbvd.lib.r302.util.ValidationUtil.isBBVAClient;


public class SimulationParticipanBean {

    private SimulationParticipanBean() {
    }

    public static SimulationParticipantDAO createSimulationParticipant (BigDecimal insuranceSimulationId,PayloadStore payloadStore){
        SimulationParticipantDAO simulationParticipant = new SimulationParticipantDAO();
        simulationParticipant.setInsuranceSimulationId(insuranceSimulationId);
        simulationParticipant.setInsuranceProductId(payloadStore.getProductInformation().getInsuranceProductId());
        simulationParticipant.setInsuredAmount(getInsuredAmount(payloadStore.getResponse().getInsuredAmount()));
        simulationParticipant.setPeriodNumber(getPeriodNumber(payloadStore.getResponse().getTerm()));
        simulationParticipant.setPeriodType("A");
        simulationParticipant.setCustomerEntryDate(toLocalDate(new Date()));

        if(!CollectionUtils.isEmpty(payloadStore.getResponse().getListRefunds())){
            simulationParticipant.setRefundPer(payloadStore.getResponse().getListRefunds().get(0).getUnit().getPercentage());
            simulationParticipant.setCurrencyId(payloadStore.getResponse().getListRefunds().get(1).getUnit().getCurrency());
            simulationParticipant.setTotalReturnAmount(payloadStore.getResponse().getListRefunds().get(1).getUnit().getAmount());
        }

        if(!CollectionUtils.isEmpty(payloadStore.getResponse().getParticipants())){
            simulationParticipant.setInsuredId(payloadStore.getResponse().getParticipants().get(0).getId());
            simulationParticipant.setCustomerDocumentType(payloadStore.getResponse().getParticipants().get(0).getIdentityDocument().getDocumentType().getId());
            simulationParticipant.setInsuredCustomerName(payloadStore.getResponse().getParticipants().get(0).getFirstName());
            simulationParticipant.setClientLastName(payloadStore.getResponse().getParticipants().get(0).getLastName().concat(ConstantsUtil.DELIMITER).concat(payloadStore.getResponse().getParticipants().get(0).getSecondLastName()));
            simulationParticipant.setPhoneId(payloadStore.getResponse().getParticipants().get(0).getContactDetails().get(0).getContact().getNumber());
            simulationParticipant.setCustomerBirthDate(toLocalDate(payloadStore.getResponse().getParticipants().get(0).getBirthDate()));
            simulationParticipant.setPersonalId(payloadStore.getResponse().getParticipants().get(0).getIdentityDocument().getDocumentNumber());
            simulationParticipant.setUserEmailPersonalDesc(payloadStore.getResponse().getParticipants().get(0).getContactDetails().get(1).getContact().getAddress());
            simulationParticipant.setIsBbvaCustomerType(isBBVAClient(payloadStore.getResponse().getParticipants().get(0).getId())? ConstantsUtil.YES_S : ConstantsUtil.NO_N);
            simulationParticipant.setGenderId(payloadStore.getResponse().getParticipants().get(0).getGender().getId().equals(ConstantsUtil.MALE)? ConstantsUtil.M:ConstantsUtil.F);
            if(StringUtils.isEmpty(payloadStore.getResponse().getParticipants().get(0).getId())){
                simulationParticipant.setCustomerEntryDate(null);
            }
        }else {
            simulationParticipant.setInsuredId(payloadStore.getResponse().getHolder().getId());
            simulationParticipant.setCustomerDocumentType(payloadStore.getResponse().getHolder().getIdentityDocument().getDocumentType().getId());
            simulationParticipant.setInsuredCustomerName(payloadStore.getResponse().getHolder().getFirstName());
            simulationParticipant.setClientLastName(payloadStore.getResponse().getHolder().getLastName().replace(" ",ConstantsUtil.DELIMITER));
            simulationParticipant.setPhoneId(null);
            simulationParticipant.setCustomerBirthDate(null);
            simulationParticipant.setPersonalId(payloadStore.getResponse().getHolder().getIdentityDocument().getDocumentNumber());
            simulationParticipant.setIsBbvaCustomerType(ConstantsUtil.YES_S);
            simulationParticipant.setUserEmailPersonalDesc(null);
            if(Objects.nonNull(payloadStore.getCustomer()) && StringUtils.isNotEmpty(payloadStore.getCustomer().getData().get(0).getBirthData().getBirthDate())){
                simulationParticipant.setCustomerBirthDate(toLocalDate(ModifyQuotationRimac.ParseFecha(payloadStore.getCustomer().getData().get(0).getBirthData().getBirthDate())));
                Map<String, String> contactDetails = getGroupedByTypeContactDetail(payloadStore.getCustomer().getData().get(0));
                simulationParticipant.setUserEmailPersonalDesc(contactDetails.get(EMAIL));
                simulationParticipant.setPhoneId(contactDetails.get(MOBILE_NUMBER));
                simulationParticipant.setGenderId(payloadStore.getCustomer().getData().get(0).getGender().getId().equals(ConstantsUtil.MALE)? ConstantsUtil.M:ConstantsUtil.F);
            }
        }
        simulationParticipant.setParticipantRoleId(BigDecimal.valueOf(ConstantsUtil.IS_INSURED));
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
