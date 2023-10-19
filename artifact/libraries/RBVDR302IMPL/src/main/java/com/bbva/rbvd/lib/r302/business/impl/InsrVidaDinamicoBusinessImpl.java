package com.bbva.rbvd.lib.r302.business.impl;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.rbvd.dto.lifeinsrc.commons.CoverageDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.InsurancePlanDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.RefundsDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.UnitDTO;
import com.bbva.rbvd.dto.lifeinsrc.rimac.commons.CoberturaBO;
import com.bbva.rbvd.dto.lifeinsrc.rimac.commons.FinanciamientoBO;
import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.AseguradoBO;
import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.CotizacionBO;
import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.InsuranceLifeSimulationBO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.InsuranceLimitsDTO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDErrors;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDValidation;
import com.bbva.rbvd.lib.r301.RBVDR301;
import com.bbva.rbvd.lib.r302.business.IInsrDynamicLifeBusiness;
import com.bbva.rbvd.lib.r302.service.api.ConsumerExternalService;
import com.bbva.rbvd.lib.r302.transfer.PayloadConfig;
import com.bbva.rbvd.lib.r302.transfer.PayloadStore;
import com.bbva.rbvd.lib.r302.transform.bean.InsuranceBean;
import com.bbva.rbvd.lib.r302.transform.bean.InsuredAmountBean;
import com.bbva.rbvd.lib.r302.transform.bean.ModifyQuotationRimac;
import com.bbva.rbvd.lib.r302.transform.bean.QuotationRimac;
import com.bbva.rbvd.lib.r302.transform.list.IListInstallmentPlan;
import com.bbva.rbvd.lib.r302.transform.list.impl.ListInstallmentPlanDynamicLife;
import com.bbva.rbvd.lib.r302.util.ConstantsUtil;
import com.bbva.rbvd.lib.r302.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Collections;
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

        ConsumerExternalService consumerExternalService = new ConsumerExternalService(this.rbvdR301);
        InsuranceLifeSimulationBO responseRimac = consumerExternalService.executeSimulationRimacService(requestRimac,payloadConfig.getInput().getTraceId());
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
        requestRimac.getPayload().setCoberturas(getAdditionalCoverages(payloadConfig.getInput()));
        //asegurado
        requestRimac.getPayload().setAsegurado(buildInsurance(payloadConfig));
        //financiamiento
        validateConstructionInstallmenPlan(payloadConfig.getInput(),requestRimac);

        LOGGER.info("***** InsrVidaDinamicoBusinessImpl - executeModifyQuotationRimacService | requestRimac: {} *****",requestRimac);

        ConsumerExternalService consumerExternalService = new ConsumerExternalService(this.rbvdR301);
        InsuranceLifeSimulationBO responseRimac = consumerExternalService.executeSimulationModificationRimacService(requestRimac,
                payloadConfig.getInput().getExternalSimulationId(),payloadConfig.getInput().getTraceId());

        LOGGER.info("***** InsrVidaDinamicoBusinessImpl - executeModifyQuotationRimacService | responseRimac: {} *****",responseRimac);

        if(Objects.isNull(responseRimac)){
                throw RBVDValidation.build(RBVDErrors.ERROR_FROM_RIMAC);
        }

        return responseRimac;
    }

    @Override
    public PayloadStore doDynamicLife(PayloadConfig payloadConfig) {
        LOGGER.info("***** InsrVidaDinamicoBusinessImpl - doDynamicLife |  payloadConfig: {0} ", payloadConfig);
        LifeSimulationDTO response;
        InsuranceLifeSimulationBO responseRimac = null;
        validatePlanWithRefundPercentage(payloadConfig.getInput());
        payloadConfig.setParticipant(ValidationUtil.isParticipant(payloadConfig.getInput()));

        if(ValidationUtil.isFirstCalled(payloadConfig.getInput().getExternalSimulationId())) {
            responseRimac = this.executeQuotationRimacService(payloadConfig);
        }else{
            responseRimac = this.executeModifyQuotationRimacService(payloadConfig);
        }
        LOGGER.info("***** InsrVidaDinamicoBusinessImpl - doDynamicLife |  responseRimac: {} *****",responseRimac.getPayload());

        //construccion de respuesta trx
        response = prepareResponse(this.applicationConfigurationService, payloadConfig, responseRimac);
        String documentTypeId = getDocumentTypeId(payloadConfig.getInput());

        //Actualizacion tipo documento en salida trx
        if(payloadConfig.isParticipant()){
            response.getParticipants().get(0).getIdentityDocument().getDocumentType().setId(payloadConfig.getProperties().getDocumentTypeIdAsText());
        }else{
            response.getHolder().getIdentityDocument().getDocumentType().setId(payloadConfig.getProperties().getDocumentTypeIdAsText());
        }


        return PayloadStore.Builder.an()
                .creationUser(payloadConfig.getInput().getCreationUser())
                .userAudit(payloadConfig.getInput().getCreationUser())
                .responseRimac(responseRimac)
                .response(response)
                .customer(payloadConfig.getCustomerListASO())
                .documentTypeId(documentTypeId)
                .productInformation(payloadConfig.getProductInformation())
                .build();

    }

    public String getDocumentTypeId(LifeSimulationDTO input){
        if(!CollectionUtils.isEmpty(input.getParticipants())){
            return input.getParticipants().get(0).getIdentityDocument().getDocumentType().getId();
        }else{
            return input.getHolder().getIdentityDocument().getDocumentType().getId();
        }
    }

    public void validateConstructionInstallmenPlan(LifeSimulationDTO input, InsuranceLifeSimulationBO requestRimac){
        List<FinanciamientoBO> financiamiento = new ArrayList<>();
        FinanciamientoBO financiamientoBO = new FinanciamientoBO();
        if(!CollectionUtils.isEmpty(input.getProduct().getPlans()) && !CollectionUtils.isEmpty(input.getProduct().getPlans().get(0).getInstallmentPlans())){
            String numeroCuotas = this.applicationConfigurationService.getProperty(ConstantsUtil.CUOTA + input.getProduct().getPlans().get(0).getInstallmentPlans().get(0).getPeriod().getId());
            financiamientoBO.setNumeroCuotas(Long.valueOf(numeroCuotas));
            String frecuencia = this.applicationConfigurationService.getProperty(input.getProduct().getPlans().get(0).getInstallmentPlans().get(0).getPeriod().getId());
            financiamientoBO.setFrecuencia(frecuencia);
        }else{
            financiamientoBO.setNumeroCuotas(ConstantsUtil.DEFAULT_NUM_CUOTAS);
            financiamientoBO.setFrecuencia(ConstantsUtil.DEFAULT_FREQUENCY);
        }
        financiamiento.add(financiamientoBO);
        requestRimac.getPayload().setFinanciamiento(financiamiento);
    }

    public AseguradoBO buildInsurance(PayloadConfig plaPayloadConfig){
        AseguradoBO insurance;

        if(plaPayloadConfig.isParticipant()){
            insurance = InsuranceBean.buildInsuranceFromParticipant(plaPayloadConfig.getInput().getParticipants().get(0));
        }else{
            String documentType = this.applicationConfigurationService.getProperty(plaPayloadConfig.getCustomerListASO().getData().get(0).getIdentityDocuments().get(0).getDocumentType().getId());
            insurance = InsuranceBean.buildInsuranceFromCustomer(plaPayloadConfig.getCustomerListASO().getData().get(0), documentType);
        }
        return insurance;
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

    private static List<CoberturaBO> getAdditionalCoverages(LifeSimulationDTO input){
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
        coberturaBO.setIndSeleccionar(ConstantsUtil.YES_S);
        coberturaBO.setSumaAsegurada(coverage.getInsuredAmount() != null ? coverage.getInsuredAmount().getAmount() : new BigDecimal("0"));

        return coberturaBO;
    }

    private static void modifyRefundAmount(InsuranceLifeSimulationBO responseRimac,LifeSimulationDTO response){
        if(!CollectionUtils.isEmpty(responseRimac.getPayload().getCotizaciones()) ){

            Optional<CotizacionBO> firstQuotation = responseRimac.getPayload().getCotizaciones().stream().findFirst();

            if(firstQuotation.isPresent() && firstQuotation.get().getPlan()!=null &&
                Objects.nonNull(firstQuotation.get().getPlan().getMontoDevolucion())){

                RefundsDTO montoDevolucion = new RefundsDTO();
                UnitDTO unit = new UnitDTO();
                unit.setUnitType(ConstantsUtil.REFUNDS_UNIT_TYPE_AMOUNT);
                unit.setAmount(firstQuotation.get().getPlan().getMontoDevolucion());
                unit.setCurrency(firstQuotation.get().getPlan().getMoneda());
                montoDevolucion.setUnit(unit);
                response.getListRefunds().add(montoDevolucion);
            }

        }
    }


    private static InsuranceLimitsDTO getInsuranceLimits(InsuranceLifeSimulationBO responseRimac){

        if(responseRimac != null && !CollectionUtils.isEmpty(responseRimac.getPayload().getCotizaciones()) &&
                responseRimac.getPayload().getCotizaciones().get(0).getPlan().getSumaAseguradaMinima() != null &&
                responseRimac.getPayload().getCotizaciones().get(0).getPlan().getSumaAseguradaMaxima() != null){

            return InsuredAmountBean.getInsuranceLimitsDTO(responseRimac.getPayload().getCotizaciones().get(0).getPlan());

        } else {
            return null;
        }
    }

    private static void validatePlanWithRefundPercentage(LifeSimulationDTO input) {
        if(input.getListRefunds() != null && CollectionUtils.isEmpty(input.getProduct().getPlans())){
            BigDecimal percentage = input.getListRefunds().stream()
                    .filter(refundsDTO -> refundsDTO.getUnit().getUnitType().equals(ConstantsUtil.REFUND_UNIT_PERCENTAGE))
                    .map(refundsDTO -> refundsDTO.getUnit().getPercentage()).collect(Collectors.toList()).get(0);

            if(percentage.compareTo(BigDecimal.ZERO) == ConstantsUtil.Numero.CERO){
                InsurancePlanDTO plan01 = new InsurancePlanDTO();
                plan01.setId(ConstantsUtil.Plan.UNO);
                List<InsurancePlanDTO> plans = new ArrayList<>();
                plans.add(plan01);
                input.getProduct().setPlans(plans);
            }else{
                InsurancePlanDTO plan02 = new InsurancePlanDTO();
                plan02.setId(ConstantsUtil.Plan.DOS);
                List<InsurancePlanDTO> plans = new ArrayList<>();
                plans.add(plan02);
                input.getProduct().setPlans(plans);
            }
        }
    }


}
