package com.bbva.rbvd.lib.r302.business.impl;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.InsuranceLifeSimulationBO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDErrors;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDValidation;
import com.bbva.rbvd.lib.r301.RBVDR301;
import com.bbva.rbvd.lib.r302.business.IInsrEasyYesBusiness;
import com.bbva.rbvd.lib.r302.transfer.PayloadConfig;
import com.bbva.rbvd.lib.r302.transfer.PayloadStore;
import com.bbva.rbvd.lib.r302.transform.bean.QuotationRimac;
import com.bbva.rbvd.lib.r302.transform.list.ListInstallmentPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Objects;

public class InsrEasyYesBusinessImpl implements IInsrEasyYesBusiness {

    private static final Logger LOGGER = LoggerFactory.getLogger(InsrEasyYesBusinessImpl.class);
    private final RBVDR301 rbvdR301;
    private final ApplicationConfigurationService applicationConfigurationService;

    public InsrEasyYesBusinessImpl(RBVDR301 rbvdR301, ApplicationConfigurationService applicationConfigurationService) {
        this.rbvdR301 = rbvdR301;
        this.applicationConfigurationService = applicationConfigurationService;
    }

    public PayloadStore doEasyYes(PayloadConfig payloadConfig) {

        LOGGER.info("***** InsrEasyYesBusinessImpl - doEasyYes | argument payloadConfig: {} *****",payloadConfig);

        //ejecucion servicio rimac
        InsuranceLifeSimulationBO responseRimac = this.callQuotationRimacService(
                payloadConfig.getInput(), payloadConfig.getSumCumulus(), payloadConfig.getProductInformation().getInsuranceBusinessName());
        LOGGER.info("***** InsrEasyYesBusinessImpl - doEasyYes | responseRimac: {} *****",responseRimac);

        //construccion de respuesta trx
        LifeSimulationDTO response = prepareResponse(this.applicationConfigurationService, payloadConfig, responseRimac);
        LOGGER.info("***** InsrEasyYesBusinessImpl - doEasyYes | response trx: {} *****",response);

        PayloadStore payloadStore = new PayloadStore();
        payloadStore.setCreationUser(payloadConfig.getInput().getCreationUser());
        payloadStore.setUserAudit(payloadConfig.getInput().getUserAudit());
        payloadStore.setResponseRimac(responseRimac);
        payloadStore.setResponse(response);
        payloadStore.setDocumentTypeId( payloadConfig.getInput().getHolder().getIdentityDocument().getDocumentType().getId());
        payloadStore.setProductInformation(payloadConfig.getProductInformation());

        LOGGER.info("***** InsrEasyYesBusinessImpl - doEasyYes END | payloadStore: {} *****",payloadStore);
        return payloadStore;
    }

    private LifeSimulationDTO prepareResponse(ApplicationConfigurationService applicationConfigurationService, PayloadConfig payloadConfig, InsuranceLifeSimulationBO responseRimac) {
        LOGGER.info("***** InsrEasyYesBusinessImpl - prepareResponse START *****");

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


        LOGGER.info("***** InsrEasyYesBusinessImpl - prepareResponse END | response: {} *****",response);
        return response;
    }

    private InsuranceLifeSimulationBO callQuotationRimacService(LifeSimulationDTO input, BigDecimal cumulo, String productInformation){
        LOGGER.info("***** InsrEasyYesBusinessImpl - callQuotationRimacService START *****");

        InsuranceLifeSimulationBO requestRimac = QuotationRimac.mapInRequestRimacLife(input,cumulo);
        requestRimac.getPayload().setProducto(productInformation);
        LOGGER.info("***** InsrEasyYesBusinessImpl - callQuotationRimacService | requestRimac: {} *****",requestRimac);

        InsuranceLifeSimulationBO responseRimac = this.rbvdR301.executeSimulationRimacService(requestRimac,input.getTraceId());
        LOGGER.info("***** InsrEasyYesBusinessImpl - callQuotationRimacService | responseRimac: {} *****",responseRimac);

        if(Objects.isNull(responseRimac)){
            throw RBVDValidation.build(RBVDErrors.ERROR_FROM_RIMAC);
        }

        return responseRimac;
    }

}
