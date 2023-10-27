package com.bbva.rbvd.lib.r302.transform.bean;

import com.bbva.rbvd.dto.lifeinsrc.commons.InsuredAmountDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.RefundsDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.TermDTO;
import com.bbva.rbvd.dto.lifeinsrc.dao.InsuredLifeDAO;
import com.bbva.rbvd.lib.r302.transfer.PayloadStore;
import com.bbva.rbvd.lib.r302.util.ConstantsUtil;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;


public class InsuredLifeBean {
    private InsuredLifeBean() {}
    public static InsuredLifeDAO createSimulationInsuredLife(BigDecimal insuranceSimulationId, PayloadStore payloadStore){
        InsuredLifeDAO simulationInsuredLife = new InsuredLifeDAO();
        simulationInsuredLife.setInsuranceSimulationId(insuranceSimulationId);
        simulationInsuredLife.setInsuranceProductId(payloadStore.getProductInformation().getInsuranceProductId());
        simulationInsuredLife.setInsuredAmount(getInsuredAmount(payloadStore.getResponse().getInsuredAmount()));
        simulationInsuredLife.setTerm(getTerm(payloadStore.getResponse().getTerm()));
        simulationInsuredLife.setRefunds(getRefunds(payloadStore.getResponse().getListRefunds()));
        simulationInsuredLife.setCreationUser(payloadStore.getCreationUser());
        simulationInsuredLife.setUserAudit(payloadStore.getUserAudit());

        return  simulationInsuredLife;
    }
    private static BigDecimal getInsuredAmount(InsuredAmountDTO insuredAmount){
        return Objects.nonNull(insuredAmount)? insuredAmount.getAmount():null;
    }
    private static InsuredLifeDAO.TermDAO getTerm(TermDTO term){
        InsuredLifeDAO insuredLife = new InsuredLifeDAO();
        InsuredLifeDAO.TermDAO termDAO = insuredLife.new TermDAO();
        if(Objects.nonNull(term)){
            termDAO.setPeriodNumber(BigDecimal.valueOf(term.getNumber()));
            termDAO.setPeriodType(ConstantsUtil.Period.ANNUAL.getCode());
        }
        return termDAO;
    }
    private static InsuredLifeDAO.RefundsDAO getRefunds(List<RefundsDTO> refunds){
        InsuredLifeDAO insuredLife = new InsuredLifeDAO();
        InsuredLifeDAO.RefundsDAO refundsDAO = insuredLife.new RefundsDAO();
        if(!CollectionUtils.isEmpty(refunds)){
            refundsDAO.setRefundPer(refunds.get(0).getUnit().getPercentage());
            refundsDAO.setCurrencyId(refunds.get(1).getUnit().getCurrency());
            refundsDAO.setTotalReturnAmount(refunds.get(1).getUnit().getAmount());
        }
        return refundsDAO;
    }
}
