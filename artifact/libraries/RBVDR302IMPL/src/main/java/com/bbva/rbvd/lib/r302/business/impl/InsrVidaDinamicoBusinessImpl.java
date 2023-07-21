package com.bbva.rbvd.lib.r302.business.impl;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.InsuranceLifeSimulationBO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDErrors;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDValidation;
import com.bbva.rbvd.lib.r301.RBVDR301;
import com.bbva.rbvd.lib.r302.Transfer.PayloadConfig;
import com.bbva.rbvd.lib.r302.Transfer.PayloadStore;
import com.bbva.rbvd.lib.r302.business.ISeguroVidaDinamico;
import com.bbva.rbvd.lib.r302.impl.util.MockResponse;
import com.bbva.rbvd.lib.r302.transform.list.ListInstallmentPlan;
import com.bbva.rbvd.lib.r302.transform.objects.ModifyQuotationRimac;
import com.bbva.rbvd.lib.r302.transform.objects.QuotationRimac;
import com.bbva.rbvd.lib.r302.util.ValidationUtil;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.Objects;

public class InsrVidaDinamicoBusinessImpl implements ISeguroVidaDinamico {

    private RBVDR301 rbvdR301;

    public InsrVidaDinamicoBusinessImpl(RBVDR301 rbvdR301) {
        this.rbvdR301 = rbvdR301;
    }

    @Override
    public InsuranceLifeSimulationBO executeQuotationRimacService(
            LifeSimulationDTO input, String businessName, CustomerListASO customerListASO, BigDecimal cumulo,
            ApplicationConfigurationService applicationConfigurationService) {

        InsuranceLifeSimulationBO requestRimac = QuotationRimac.mapInRequestRimacLife(input,cumulo);
        requestRimac.getPayload().setProducto(businessName);
        ModifyQuotationRimac.addFieldsDatoParticulares(requestRimac,input,customerListASO);

        InsuranceLifeSimulationBO responseRimac = null;
        if (applicationConfigurationService.getProperty("IS_MOCK_MODIFY_QUOTATION_DYNAMIC").equals("S")) {
            responseRimac = new MockResponse().getMockResponseRimacQuotationService();
        }else{
            responseRimac = rbvdR301.executeSimulationRimacService(requestRimac,input.getTraceId());
        }

        if(Objects.isNull(responseRimac)){
            throw RBVDValidation.build(RBVDErrors.ERROR_FROM_RIMAC);
        }

        return responseRimac;
    }

    @Override
    public InsuranceLifeSimulationBO executeModifyQuotationRimacService(
            LifeSimulationDTO input,CustomerListASO customerListASO,BigDecimal cumulo,
            ApplicationConfigurationService applicationConfigurationService) {

        InsuranceLifeSimulationBO requestRimac = ModifyQuotationRimac.mapInRequestRimacLifeModifyQuotation(input,customerListASO,cumulo);

        InsuranceLifeSimulationBO responseRimac = null;
        if (applicationConfigurationService.getProperty("IS_MOCK_MODIFY_QUOTATION_DYNAMIC").equals("S")) {
            responseRimac = new MockResponse().getMockResponseRimacModifyQuotationService();
        } else {
            responseRimac = rbvdR301.executeSimulationModificationRimacService(requestRimac,input.getExternalSimulationId(),input.getTraceId());
        }

        if(Objects.isNull(responseRimac)){
            throw RBVDValidation.build(RBVDErrors.ERROR_FROM_RIMAC);
        }

        return responseRimac;
    }

    @Override
    public PayloadStore doDynamicLife(ApplicationConfigurationService applicationConfigurationService, PayloadConfig payloadConfig) {
        LifeSimulationDTO response;
        InsuranceLifeSimulationBO responseRimac = null;

        if(ValidationUtil.isFirstCalled(payloadConfig.getInput().getExternalSimulationId())) {
            responseRimac = this.executeQuotationRimacService(
                    payloadConfig.getInput(),
                    payloadConfig.getProductInformation().getInsuranceBusinessName(),
                    payloadConfig.getCustomerListASO(),
                    payloadConfig.getSumCumulus(),
                    applicationConfigurationService);
        }else{
            responseRimac = this.executeModifyQuotationRimacService(
                    payloadConfig.getInput(),
                    payloadConfig.getCustomerListASO(),
                    payloadConfig.getSumCumulus(),
                    applicationConfigurationService
            );
        }

        //construccion de respuesta trx
        response = prepareResponse(applicationConfigurationService, payloadConfig, responseRimac);

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

    @NotNull
    private static LifeSimulationDTO prepareResponse(ApplicationConfigurationService applicationConfigurationService, PayloadConfig payloadConfig, InsuranceLifeSimulationBO responseRimac) {
        LifeSimulationDTO response;
        ListInstallmentPlan listInstallmentPlan = new ListInstallmentPlan();
        listInstallmentPlan.setApplicationConfigurationService(applicationConfigurationService);
        response = payloadConfig.getInput();
        response.getProduct().setName(responseRimac.getPayload().getProducto());
        response.setExternalSimulationId(responseRimac.getPayload().getCotizaciones().get(0).getCotizacion());
        response.getProduct().setPlans(listInstallmentPlan.getPlansNamesAndRecommendedValuesAndInstallmentsPlans(
                payloadConfig.getListInsuranceProductModalityDAO(),
                responseRimac,
                payloadConfig.getProperties().getSegmentLifePlans().get(0),
                payloadConfig.getProperties().getSegmentLifePlans().get(1),
                payloadConfig.getProperties().getSegmentLifePlans().get(2)));
        //Revisar si es necesario esta línea:
        //response.getProduct().setId(payloadConfig.getInput().getProduct().getId());
        response.getHolder().getIdentityDocument().getDocumentType().setId(payloadConfig.getProperties().getDocumentTypeIdAsText());
        return response;
    }


}
