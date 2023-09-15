package com.bbva.rbvd.lib.r302.business.impl;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.rbvd.dto.lifeinsrc.commons.CoverageDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.InsuredAmountDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.RefundsDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.UnitDTO;
import com.bbva.rbvd.dto.lifeinsrc.rimac.commons.CoberturaBO;
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

    public InsuranceLifeSimulationBO executeQuotationRimacService(
            LifeSimulationDTO input, String businessName, CustomerListASO customerListASO, BigDecimal cumulo, boolean isParticipant) {

        LOGGER.info("***** InsrVidaDinamicoBusinessImpl - executeQuotationRimacService START *****");

        InsuranceLifeSimulationBO requestRimac = QuotationRimac.mapInRequestRimacDynamicLife(input,cumulo,businessName,isParticipant);
        ModifyQuotationRimac.addFieldsDatoParticulares(requestRimac,input,customerListASO,isParticipant);
        LOGGER.info("***** InsrVidaDinamicoBusinessImpl - executeQuotationRimacService | requestRimac: {} *****",requestRimac.getPayload().getAsegurado().getTipoDocumento());

        InsuranceLifeSimulationBO responseRimac = this.rbvdR301.executeSimulationRimacService(requestRimac,input.getTraceId());
        LOGGER.info("***** InsrVidaDinamicoBusinessImpl - executeQuotationRimacService | responseRimac: {} *****",responseRimac);

        if(Objects.isNull(responseRimac)){
            throw RBVDValidation.build(RBVDErrors.ERROR_FROM_RIMAC);
        }

        return responseRimac;
    }


    public InsuranceLifeSimulationBO executeModifyQuotationRimacService(
            LifeSimulationDTO input,String businessName,CustomerListASO customerListASO,BigDecimal cumulo, boolean isParticipant){
        LOGGER.info("***** InsrVidaDinamicoBusinessImpl - executeModifyQuotationRimacService START *****");

        InsuranceLifeSimulationBO requestRimac = ModifyQuotationRimac.mapInRequestRimacLifeModifyQuotation(input,customerListASO,cumulo,isParticipant);
        requestRimac.getPayload().setProducto(businessName);
        requestRimac.getPayload().setCoberturas(getAddtionalCoverages(input));
        LOGGER.info("***** InsrVidaDinamicoBusinessImpl - executeModifyQuotationRimacService | requestRimac: {} *****",requestRimac);

        InsuranceLifeSimulationBO responseRimac = this.rbvdR301.executeSimulationModificationRimacService(requestRimac,input.getExternalSimulationId(),input.getTraceId());

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
            responseRimac = this.executeQuotationRimacService(
                    payloadConfig.getInput(),
                    payloadConfig.getProductInformation().getInsuranceBusinessName(),
                    payloadConfig.getCustomerListASO(),
                    payloadConfig.getSumCumulus(),
                    payloadConfig.isParticipant()
                    );
        }else{
            responseRimac = this.executeModifyQuotationRimacService(
                    payloadConfig.getInput(),
                    payloadConfig.getProductInformation().getInsuranceBusinessName(),
                    payloadConfig.getCustomerListASO(),
                    payloadConfig.getSumCumulus(),
                    payloadConfig.isParticipant()
            );
        }
        LOGGER.info("***** InsrVidaDinamicoBusinessImpl - doDynamicLife |  responseRimac: {} *****",responseRimac);

        //construccion de respuesta trx
        response = prepareResponse(this.applicationConfigurationService, payloadConfig, responseRimac);

        //guardar en bd
        return new PayloadStore(
                payloadConfig.getInput().getCreationUser(),
                payloadConfig.getInput().getUserAudit(),
                responseRimac,
                response,
                payloadConfig.getInput().getHolder().getIdentityDocument().getDocumentType().getId(),
                payloadConfig.getProductInformation()
        );

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

        LOGGER.info("***** InsrVidaDinamicoBusinessImpl - prepareResponse response {} *****",response.getEndorsed());
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
