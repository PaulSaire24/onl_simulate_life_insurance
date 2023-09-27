package com.bbva.rbvd.lib.r302.business.impl;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.rbvd.dto.lifeinsrc.commons.CoverageDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.InsuredAmountDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.RefundsDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.UnitDTO;
import com.bbva.rbvd.dto.lifeinsrc.rimac.commons.CoberturaBO;
import com.bbva.rbvd.dto.lifeinsrc.rimac.commons.FinanciamientoBO;
import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.InsuranceLifeSimulationBO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.InsuranceLimitsDTO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDErrors;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDValidation;
import com.bbva.rbvd.lib.r301.RBVDR301;
import com.bbva.rbvd.lib.r302.transfer.PayloadConfig;
import com.bbva.rbvd.lib.r302.transfer.PayloadStore;
import com.bbva.rbvd.lib.r302.business.IInsrDynamicLifeBusiness;
import com.bbva.rbvd.lib.r302.transform.list.IListInstallmentPlan;
import com.bbva.rbvd.lib.r302.transform.bean.ModifyQuotationRimac;
import com.bbva.rbvd.lib.r302.transform.bean.QuotationRimac;
import com.bbva.rbvd.lib.r302.transform.list.impl.ListInstallmentPlanDynamicLife;
import com.bbva.rbvd.lib.r302.util.ConstantsUtil;
import com.bbva.rbvd.lib.r302.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class InsrVidaDinamicoBusinessImpl implements IInsrDynamicLifeBusiness {

    private static final Logger LOGGER = LoggerFactory.getLogger(InsrVidaDinamicoBusinessImpl.class);


    private final RBVDR301 rbvdR301;
    private final ApplicationConfigurationService applicationConfigurationService;

    public InsrVidaDinamicoBusinessImpl(RBVDR301 rbvdR301, ApplicationConfigurationService applicationConfigurationService) {
        this.rbvdR301 = rbvdR301;
        this.applicationConfigurationService = applicationConfigurationService;
    }

    public InsuranceLifeSimulationBO executeQuotationRimacService(PayloadConfig payloadConfig) {

        LOGGER.info("***** InsrVidaDinamicoBusinessImpl - executeQuotationRimacService START *****");

        InsuranceLifeSimulationBO requestRimac = QuotationRimac.mapInRequestRimacDynamicLife(
                payloadConfig.getInput(),payloadConfig.getSumCumulus(),payloadConfig.getProductInformation().getInsuranceBusinessName(),payloadConfig.isParticipant());
        ModifyQuotationRimac.addFieldsDatoParticulares(requestRimac,payloadConfig.getInput(),payloadConfig.getCustomerListASO(),payloadConfig.isParticipant());
        LOGGER.info("***** InsrVidaDinamicoBusinessImpl - executeQuotationRimacService | requestRimac: {} *****",requestRimac);

        InsuranceLifeSimulationBO responseRimac = this.rbvdR301.executeSimulationRimacService(requestRimac,payloadConfig.getInput().getTraceId());
        LOGGER.info("***** InsrVidaDinamicoBusinessImpl - executeQuotationRimacService | responseRimac: {} *****",responseRimac);

        if(Objects.isNull(responseRimac)){
            throw RBVDValidation.build(RBVDErrors.ERROR_FROM_RIMAC);
        }

        return responseRimac;
    }


    public InsuranceLifeSimulationBO executeModifyQuotationRimacService(PayloadConfig payloadConfig){
        LOGGER.info("***** InsrVidaDinamicoBusinessImpl - executeModifyQuotationRimacService START *****");

        InsuranceLifeSimulationBO requestRimac = ModifyQuotationRimac.mapInRequestRimacLifeModifyQuotation(payloadConfig.getInput(),
                payloadConfig.getCustomerListASO(),payloadConfig.getSumCumulus(),payloadConfig.isParticipant());
        requestRimac.getPayload().setProducto(payloadConfig.getProductInformation().getInsuranceBusinessName());
        requestRimac.getPayload().setCoberturas(getAddtionalCoverages(payloadConfig.getInput()));

        validateConstructionInstallmenPlan(payloadConfig.getInput(),requestRimac);

        LOGGER.info("***** InsrVidaDinamicoBusinessImpl - executeModifyQuotationRimacService | requestRimac: {} *****",requestRimac);

        InsuranceLifeSimulationBO responseRimac = this.rbvdR301.executeSimulationModificationRimacService(requestRimac,
                payloadConfig.getInput().getExternalSimulationId(),payloadConfig.getInput().getTraceId());

        LOGGER.info("***** InsrVidaDinamicoBusinessImpl - executeModifyQuotationRimacService | responseRimac: {} *****",responseRimac);

        if(Objects.isNull(responseRimac)){
                throw RBVDValidation.build(RBVDErrors.ERROR_FROM_RIMAC);
        }

        return responseRimac;
    }

    @Override
    public PayloadStore doDynamicLife(PayloadConfig payloadConfig) {
        LOGGER.info("***** InsrVidaDinamicoBusinessImpl - doDynamicLife |  payloadConfig: {} *****",payloadConfig.toString());
        LifeSimulationDTO response;
        InsuranceLifeSimulationBO responseRimac = null;

        if(ValidationUtil.isFirstCalled(payloadConfig.getInput().getExternalSimulationId())) {
            responseRimac = this.executeQuotationRimacService(payloadConfig);
        }else{
            responseRimac = this.executeModifyQuotationRimacService(payloadConfig);
        }
        LOGGER.info("***** InsrVidaDinamicoBusinessImpl - doDynamicLife |  responseRimac: {} *****",responseRimac.getPayload());

        //construccion de respuesta trx
        response = prepareResponse(this.applicationConfigurationService, payloadConfig, responseRimac);
        String documentTypeId = getDocumentTypeId(payloadConfig.getInput());
        //guardar en bd

        PayloadStore payloadStore = new PayloadStore();
        payloadStore.setCreationUser(payloadConfig.getInput().getCreationUser());
        payloadStore.setUserAudit(payloadConfig.getInput().getUserAudit());
        payloadStore.setResponseRimac(responseRimac);
        payloadStore.setResponse(response);
        payloadStore.setDocumentTypeId(documentTypeId);
        payloadStore.setProductInformation(payloadConfig.getProductInformation());
        return payloadStore;
    }

    public String getDocumentTypeId(LifeSimulationDTO input){
        if(!CollectionUtils.isEmpty(input.getParticipants())){
            return input.getParticipants().get(0).getIdentityDocument().getDocumentType().getId();
        }else{
            return input.getHolder().getIdentityDocument().getDocumentType().getId();
        }
    }
    public void validateConstructionInstallmenPlan(LifeSimulationDTO input, InsuranceLifeSimulationBO requestRimac){

        if(!CollectionUtils.isEmpty(input.getProduct().getPlans()) && !CollectionUtils.isEmpty(input.getProduct().getPlans().get(0).getInstallmentPlans())){
            List<FinanciamientoBO> financiamientoBOList = new ArrayList<>();
            FinanciamientoBO financiamientoBO = new FinanciamientoBO();
            String totalNumberInstallments = this.applicationConfigurationService.getProperty(ConstantsUtil.CUOTA + input.getProduct().getPlans().get(0).getInstallmentPlans().get(0).getPeriod().getId());
            financiamientoBO.setNumCuota(Long.valueOf(totalNumberInstallments));
            String frecuencia = this.applicationConfigurationService.getProperty(input.getProduct().getPlans().get(0).getInstallmentPlans().get(0).getPeriod().getId());
            financiamientoBO.setFrecuencia(frecuencia);
            financiamientoBOList.add(financiamientoBO);
            requestRimac.getPayload().setFinanciamiento(financiamientoBOList);
        }
    }

    private static LifeSimulationDTO prepareResponse(ApplicationConfigurationService applicationConfigurationService, PayloadConfig payloadConfig, InsuranceLifeSimulationBO responseRimac) {
        LOGGER.info("***** InsrVidaDinamicoBusinessImpl - prepareResponse START *****");

        LifeSimulationDTO response;
        IListInstallmentPlan listInstallmentPlan = new ListInstallmentPlanDynamicLife(applicationConfigurationService);

        response = payloadConfig.getInput();
        response.getProduct().setName(responseRimac.getPayload().getProducto());
        response.setExternalSimulationId(responseRimac.getPayload().getCotizaciones().get(0).getCotizacion());
        response.getProduct().setPlans(listInstallmentPlan.getPlansNamesAndRecommendedValuesAndInstallmentsPlans(
                payloadConfig.getListInsuranceProductModalityDAO(),
                responseRimac,
                payloadConfig.getProperties().getSegmentLifePlans()));

        modifyRefundAmount(responseRimac,response);
        response.setInsuranceLimits(getInsuranceLimits(responseRimac));

        LOGGER.info("***** InsrVidaDinamicoBusinessImpl - prepareResponse response {} *****",response);
        return response;
    }

    private static List<CoberturaBO> getAddtionalCoverages(LifeSimulationDTO input){
        if(!CollectionUtils.isEmpty(input.getProduct().getPlans()) && !CollectionUtils.isEmpty(input.getProduct().getPlans().get(0).getCoverages())){
            return input.getProduct().getPlans().get(0).getCoverages().stream()
                    .map(InsrVidaDinamicoBusinessImpl::mapAdditionalCoverageForRequest).collect(Collectors.toList());
        }else{
            return Collections.emptyList();
        }
    }

    private static CoberturaBO mapAdditionalCoverageForRequest(CoverageDTO coverage){
        CoberturaBO coberturaBO = new CoberturaBO();

        coberturaBO.setCodigoCobertura(Long.parseLong(coverage.getId()));
        coberturaBO.setIndSeleccionar(ConstantsUtil.YES_CONSTANT);
        coberturaBO.setSumaAsegurada(coverage.getInsuredAmount() != null ? coverage.getInsuredAmount().getAmount() : new BigDecimal("0"));

        return coberturaBO;
    }

    private static void modifyRefundAmount(InsuranceLifeSimulationBO responseRimac,LifeSimulationDTO response){
        if(responseRimac.getPayload().getCotizaciones() != null &&
            Objects.nonNull(responseRimac.getPayload().getCotizaciones().get(0).getPlan().getMontoDevolucion())){
            RefundsDTO montoDevolucion = new RefundsDTO();
            UnitDTO unit = new UnitDTO();
            unit.setUnitType(ConstantsUtil.REFUNDS_UNITTYPE_AMOUNT);
            unit.setAmount(responseRimac.getPayload().getCotizaciones().get(0).getPlan().getMontoDevolucion());
            unit.setCurrency(responseRimac.getPayload().getCotizaciones().get(0).getPlan().getMoneda());
            montoDevolucion.setUnit(unit);
            response.getListRefunds().add(montoDevolucion);
        }
    }


    private static InsuranceLimitsDTO getInsuranceLimits(InsuranceLifeSimulationBO responseRimac){

        if(responseRimac != null && responseRimac.getPayload().getCotizaciones() != null &&
                responseRimac.getPayload().getCotizaciones().get(0).getPlan().getSumaAseguradaMinima() != null &&
                responseRimac.getPayload().getCotizaciones().get(0).getPlan().getSumaAseguradaMaxima() != null){

            InsuranceLimitsDTO insuranceLimits = new InsuranceLimitsDTO();

            InsuredAmountDTO sumaAseguradaMinima = new InsuredAmountDTO();
            InsuredAmountDTO sumaAseguradaMaxima = new InsuredAmountDTO();

            sumaAseguradaMinima.setAmount(responseRimac.getPayload().getCotizaciones().get(0).getPlan().getSumaAseguradaMinima());
            sumaAseguradaMinima.setCurrency(responseRimac.getPayload().getCotizaciones().get(0).getPlan().getMoneda());

            sumaAseguradaMaxima.setAmount(responseRimac.getPayload().getCotizaciones().get(0).getPlan().getSumaAseguradaMaxima());
            sumaAseguradaMaxima.setCurrency(responseRimac.getPayload().getCotizaciones().get(0).getPlan().getMoneda());

            insuranceLimits.setMinimumAmount(sumaAseguradaMinima);
            insuranceLimits.setMaximumAmount(sumaAseguradaMaxima);

            return insuranceLimits;

        } else {
            return null;
        }
    }


}
