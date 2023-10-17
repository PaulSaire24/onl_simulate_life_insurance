package com.bbva.rbvd.lib.r302.transform.bean;

import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.rbvd.dto.lifeinsrc.dao.CommonsLifeDAO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;
import com.bbva.rbvd.lib.r302.util.ConstantsUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

import static com.bbva.rbvd.lib.r302.util.ConstantsUtil.EMAIL;
import static com.bbva.rbvd.lib.r302.util.ConstantsUtil.MOBILE_NUMBER;
import static com.bbva.rbvd.lib.r302.util.ConvertUtil.getGroupedByTypeContactDetail;
import static com.bbva.rbvd.lib.r302.util.ValidationUtil.isBBVAClient;


public class SimulationParticipanBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimulationParticipanBean.class);

    private SimulationParticipanBean() {
    }

    public static CommonsLifeDAO createSimulationParticipant (BigDecimal insuranceSimulationId, LifeSimulationDTO insuranceSimulation, String creationUser, String userAudit, BigDecimal productId, CustomerListASO customer){
        CommonsLifeDAO commonsLife = new CommonsLifeDAO();
        commonsLife.setInsuranceSimulationId(insuranceSimulationId);
        commonsLife.setInsuranceProductId(productId);
        commonsLife.setInsuredAmount(insuranceSimulation.getInsuredAmount().getAmount());
        commonsLife.setPeriodNumber(BigDecimal.valueOf(insuranceSimulation.getTerm().getNumber()));
        commonsLife.setPeriodType("A");
        commonsLife.setCustomerEntryDate(new Date());

        if(!CollectionUtils.isEmpty(insuranceSimulation.getListRefunds())){
            commonsLife.setRefundPer(insuranceSimulation.getListRefunds().get(0).getUnit().getPercentage());
            commonsLife.setCurrencyId(insuranceSimulation.getListRefunds().get(1).getUnit().getCurrency());
            commonsLife.setTotalReturnAmount(insuranceSimulation.getListRefunds().get(1).getUnit().getAmount());
        }

        if(!CollectionUtils.isEmpty(insuranceSimulation.getParticipants())){
            commonsLife.setInsuredId(insuranceSimulation.getParticipants().get(0).getId());
            commonsLife.setCustomerDocumentType(insuranceSimulation.getParticipants().get(0).getIdentityDocument().getDocumentType().getId());
            commonsLife.setInsuredCustomerName(insuranceSimulation.getParticipants().get(0).getFirstName());
            commonsLife.setClientLastName(insuranceSimulation.getParticipants().get(0).getLastName());
            commonsLife.setPhoneId(insuranceSimulation.getParticipants().get(0).getContactDetails().get(0).getContact().getNumber());
            commonsLife.setCustomerBirthDate(insuranceSimulation.getParticipants().get(0).getBirthDate());
            commonsLife.setPersonalId(insuranceSimulation.getParticipants().get(0).getIdentityDocument().getDocumentNumber());
            commonsLife.setUserEmailPersonalDesc(insuranceSimulation.getParticipants().get(0).getContactDetails().get(1).getContact().getAddress());
            commonsLife.setIsBbvaCustomerType(isBBVAClient(insuranceSimulation.getParticipants().get(0).getId())? ConstantsUtil.YES_S : ConstantsUtil.NO_N);
            commonsLife.setGenderId(insuranceSimulation.getParticipants().get(0).getGender().getId().equals(ConstantsUtil.MALE)? ConstantsUtil.M:ConstantsUtil.F);
            if(StringUtils.isEmpty(insuranceSimulation.getParticipants().get(0).getId())){
                commonsLife.setCustomerEntryDate(null);
            }
        }else {
            commonsLife.setInsuredId(insuranceSimulation.getHolder().getId());
            commonsLife.setCustomerDocumentType(insuranceSimulation.getHolder().getIdentityDocument().getDocumentType().getId());
            commonsLife.setInsuredCustomerName(insuranceSimulation.getHolder().getFirstName());
            commonsLife.setClientLastName(insuranceSimulation.getHolder().getLastName());
            commonsLife.setPhoneId(null);
            commonsLife.setCustomerBirthDate(null);
            commonsLife.setPersonalId(insuranceSimulation.getHolder().getIdentityDocument().getDocumentNumber());
            commonsLife.setIsBbvaCustomerType(ConstantsUtil.YES_S);
            commonsLife.setUserEmailPersonalDesc(null);
            if(Objects.nonNull(customer) && StringUtils.isNotEmpty(customer.getData().get(0).getBirthData().getBirthDate())){
                commonsLife.setCustomerBirthDate(ModifyQuotationRimac.ParseFecha(customer.getData().get(0).getBirthData().getBirthDate()));
                LOGGER.info("birthDay Customer - {}",ModifyQuotationRimac.ParseFecha(customer.getData().get(0).getBirthData().getBirthDate()));
                Map<String, String> contactDetails = getGroupedByTypeContactDetail(customer.getData().get(0));
                commonsLife.setUserEmailPersonalDesc(contactDetails.get(EMAIL));
                commonsLife.setPhoneId(contactDetails.get(MOBILE_NUMBER));
                commonsLife.setGenderId(customer.getData().get(0).getGender().getId().equals(ConstantsUtil.MALE)? ConstantsUtil.M:ConstantsUtil.F);
            }
        }
        commonsLife.setParticipantRoleId(BigDecimal.valueOf(ConstantsUtil.IS_INSURED));
        commonsLife.setCreationUser(creationUser);
        commonsLife.setUserAudit(userAudit);

        return  commonsLife;
    }
}
