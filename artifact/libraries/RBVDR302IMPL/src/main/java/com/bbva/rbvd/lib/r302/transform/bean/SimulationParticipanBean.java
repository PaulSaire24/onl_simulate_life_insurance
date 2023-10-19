package com.bbva.rbvd.lib.r302.transform.bean;

import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.rbvd.dto.lifeinsrc.commons.InsuredAmountDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.TermDTO;
import com.bbva.rbvd.dto.lifeinsrc.dao.SimulationParticipantDAO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;
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

    public static SimulationParticipantDAO createSimulationParticipant (BigDecimal insuranceSimulationId, LifeSimulationDTO insuranceSimulation, String creationUser, String userAudit, BigDecimal productId, CustomerListASO customer){
        SimulationParticipantDAO simulationParticipant = new SimulationParticipantDAO();
        simulationParticipant.setInsuranceSimulationId(insuranceSimulationId);
        simulationParticipant.setInsuranceProductId(productId);
        simulationParticipant.setInsuredAmount(getInsuredAmount(insuranceSimulation.getInsuredAmount()));
        simulationParticipant.setPeriodNumber(getPeriodNumber(insuranceSimulation.getTerm()));
        simulationParticipant.setPeriodType("A");
        simulationParticipant.setCustomerEntryDate(toLocalDate(new Date()));

        if(!CollectionUtils.isEmpty(insuranceSimulation.getListRefunds())){
            simulationParticipant.setRefundPer(insuranceSimulation.getListRefunds().get(0).getUnit().getPercentage());
            simulationParticipant.setCurrencyId(insuranceSimulation.getListRefunds().get(1).getUnit().getCurrency());
            simulationParticipant.setTotalReturnAmount(insuranceSimulation.getListRefunds().get(1).getUnit().getAmount());
        }

        if(!CollectionUtils.isEmpty(insuranceSimulation.getParticipants())){
            simulationParticipant.setInsuredId(insuranceSimulation.getParticipants().get(0).getId());
            simulationParticipant.setCustomerDocumentType(insuranceSimulation.getParticipants().get(0).getIdentityDocument().getDocumentType().getId());
            simulationParticipant.setInsuredCustomerName(insuranceSimulation.getParticipants().get(0).getFirstName());
            simulationParticipant.setClientLastName(insuranceSimulation.getParticipants().get(0).getLastName().concat(ConstantsUtil.DELIMITER).concat(insuranceSimulation.getParticipants().get(0).getSecondLastName()));
            simulationParticipant.setPhoneId(insuranceSimulation.getParticipants().get(0).getContactDetails().get(0).getContact().getNumber());
            simulationParticipant.setCustomerBirthDate(toLocalDate(insuranceSimulation.getParticipants().get(0).getBirthDate()));
            simulationParticipant.setPersonalId(insuranceSimulation.getParticipants().get(0).getIdentityDocument().getDocumentNumber());
            simulationParticipant.setUserEmailPersonalDesc(insuranceSimulation.getParticipants().get(0).getContactDetails().get(1).getContact().getAddress());
            simulationParticipant.setIsBbvaCustomerType(isBBVAClient(insuranceSimulation.getParticipants().get(0).getId())? ConstantsUtil.YES_S : ConstantsUtil.NO_N);
            simulationParticipant.setGenderId(insuranceSimulation.getParticipants().get(0).getGender().getId().equals(ConstantsUtil.MALE)? ConstantsUtil.M:ConstantsUtil.F);
            if(StringUtils.isEmpty(insuranceSimulation.getParticipants().get(0).getId())){
                simulationParticipant.setCustomerEntryDate(null);
            }
        }else {
            simulationParticipant.setInsuredId(insuranceSimulation.getHolder().getId());
            simulationParticipant.setCustomerDocumentType(insuranceSimulation.getHolder().getIdentityDocument().getDocumentType().getId());
            simulationParticipant.setInsuredCustomerName(insuranceSimulation.getHolder().getFirstName());
            simulationParticipant.setClientLastName(insuranceSimulation.getHolder().getLastName().replace(" ",ConstantsUtil.DELIMITER));
            simulationParticipant.setPhoneId(null);
            simulationParticipant.setCustomerBirthDate(null);
            simulationParticipant.setPersonalId(insuranceSimulation.getHolder().getIdentityDocument().getDocumentNumber());
            simulationParticipant.setIsBbvaCustomerType(ConstantsUtil.YES_S);
            simulationParticipant.setUserEmailPersonalDesc(null);
            if(Objects.nonNull(customer) && StringUtils.isNotEmpty(customer.getData().get(0).getBirthData().getBirthDate())){
                simulationParticipant.setCustomerBirthDate(toLocalDate(ModifyQuotationRimac.ParseFecha(customer.getData().get(0).getBirthData().getBirthDate())));
                Map<String, String> contactDetails = getGroupedByTypeContactDetail(customer.getData().get(0));
                simulationParticipant.setUserEmailPersonalDesc(contactDetails.get(EMAIL));
                simulationParticipant.setPhoneId(contactDetails.get(MOBILE_NUMBER));
                simulationParticipant.setGenderId(customer.getData().get(0).getGender().getId().equals(ConstantsUtil.MALE)? ConstantsUtil.M:ConstantsUtil.F);
            }
        }
        simulationParticipant.setParticipantRoleId(BigDecimal.valueOf(ConstantsUtil.IS_INSURED));
        simulationParticipant.setCreationUser(creationUser);
        simulationParticipant.setUserAudit(userAudit);

        return  simulationParticipant;
    }
    private static BigDecimal getInsuredAmount(InsuredAmountDTO insuredAmount){
        return Objects.nonNull(insuredAmount)? insuredAmount.getAmount():null;
    }
    private static BigDecimal getPeriodNumber(TermDTO term){
        return Objects.nonNull(term)? BigDecimal.valueOf(term.getNumber()):null;
    }
}
