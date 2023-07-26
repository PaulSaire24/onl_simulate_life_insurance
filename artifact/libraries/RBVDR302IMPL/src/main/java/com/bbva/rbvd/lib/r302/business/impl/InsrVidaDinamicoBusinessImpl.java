package com.bbva.rbvd.lib.r302.business.impl;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.rbvd.dto.lifeinsrc.dao.InsuranceProductModalityDAO;
import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.InsuranceLifeSimulationBO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDErrors;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDValidation;
import com.bbva.rbvd.lib.r301.RBVDR301;
import com.bbva.rbvd.lib.r302.transfer.PayloadConfig;
import com.bbva.rbvd.lib.r302.transfer.PayloadStore;
import com.bbva.rbvd.lib.r302.business.IInsrDynamicLifeBusiness;
import com.bbva.rbvd.lib.r302.impl.util.MockResponse;
import com.bbva.rbvd.lib.r302.transform.list.ListInstallmentPlan;
import com.bbva.rbvd.lib.r302.transform.bean.ModifyQuotationRimac;
import com.bbva.rbvd.lib.r302.transform.bean.QuotationRimac;
import com.bbva.rbvd.lib.r302.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class InsrVidaDinamicoBusinessImpl extends InsrCommonFields implements IInsrDynamicLifeBusiness {

    private static final Logger LOGGER = LoggerFactory.getLogger(InsrVidaDinamicoBusinessImpl.class);

    public InsrVidaDinamicoBusinessImpl(RBVDR301 rbvdR301, ApplicationConfigurationService applicationConfigurationService) {
        super(rbvdR301, applicationConfigurationService);
    }

    public InsuranceLifeSimulationBO executeQuotationRimacService(
            LifeSimulationDTO input, String businessName, CustomerListASO customerListASO, BigDecimal cumulo, List<InsuranceProductModalityDAO> planes) {

        LOGGER.info("***** InsrVidaDinamicoBusinessImpl - executeQuotationRimacService START *****");

        InsuranceLifeSimulationBO requestRimac = QuotationRimac.mapInRequestRimacLife(input,cumulo);
        requestRimac.getPayload().setProducto(businessName);
        ModifyQuotationRimac.addFieldsDatoParticulares(requestRimac,input,customerListASO);
        requestRimac.getPayload().setPlanes(ModifyQuotationRimac.planesToRequestRimac(planes));
        LOGGER.info("***** InsrVidaDinamicoBusinessImpl - executeQuotationRimacService | requestRimac: {} *****",requestRimac);

        InsuranceLifeSimulationBO responseRimac = null;
        if (getApplicationConfigurationService().getProperty("IS_MOCK_MODIFY_QUOTATION_DYNAMIC").equals("S")) {
            responseRimac = new MockResponse().getMockResponseRimacQuotationService();
        }else{
            responseRimac = getRbvdR301().executeSimulationRimacService(requestRimac,input.getTraceId());
        }
        LOGGER.info("***** InsrVidaDinamicoBusinessImpl - executeQuotationRimacService | responseRimac: {} *****",responseRimac);

        if(Objects.isNull(responseRimac)){
            throw RBVDValidation.build(RBVDErrors.ERROR_FROM_RIMAC);
        }

        return responseRimac;
    }


    public InsuranceLifeSimulationBO executeModifyQuotationRimacService(
            LifeSimulationDTO input,String businessName,CustomerListASO customerListASO,BigDecimal cumulo,List<InsuranceProductModalityDAO> planes){
        LOGGER.info("***** InsrVidaDinamicoBusinessImpl - executeModifyQuotationRimacService START *****");

        InsuranceLifeSimulationBO requestRimac = ModifyQuotationRimac.mapInRequestRimacLifeModifyQuotation(input,customerListASO,cumulo);
        requestRimac.getPayload().setProducto(businessName);
        requestRimac.getPayload().setPlanes(ModifyQuotationRimac.planesToRequestRimac(planes));
        LOGGER.info("***** InsrVidaDinamicoBusinessImpl - executeModifyQuotationRimacService | requestRimac: {} *****",requestRimac);

        InsuranceLifeSimulationBO responseRimac = null;
        if (getApplicationConfigurationService().getProperty("IS_MOCK_MODIFY_QUOTATION_DYNAMIC").equals("S")) {
            responseRimac = new MockResponse().getMockResponseRimacModifyQuotationService();
        } else {
            responseRimac = getRbvdR301().executeSimulationModificationRimacService(requestRimac,input.getExternalSimulationId(),input.getTraceId());
        }
        LOGGER.info("***** InsrVidaDinamicoBusinessImpl - executeModifyQuotationRimacService | responseRimac: {} *****",responseRimac);

        if(Objects.isNull(responseRimac)){
                throw RBVDValidation.build(RBVDErrors.ERROR_FROM_RIMAC);
        }

        return responseRimac;
    }

    @Override
    public PayloadStore doDynamicLife(PayloadConfig payloadConfig) {
        LOGGER.info("***** InsrVidaDinamicoBusinessImpl - doDynamicLife |  payloadConfig: {} *****",payloadConfig);

        LifeSimulationDTO response;
        InsuranceLifeSimulationBO responseRimac = null;

        if(ValidationUtil.isFirstCalled(payloadConfig.getInput().getExternalSimulationId())) {
            responseRimac = this.executeQuotationRimacService(
                    payloadConfig.getInput(),
                    payloadConfig.getProductInformation().getInsuranceBusinessName(),
                    payloadConfig.getCustomerListASO(),
                    payloadConfig.getSumCumulus(),
                    payloadConfig.getListInsuranceProductModalityDAO()
                    );
        }else{
            responseRimac = this.executeModifyQuotationRimacService(
                    payloadConfig.getInput(),
                    payloadConfig.getProductInformation().getInsuranceBusinessName(),
                    payloadConfig.getCustomerListASO(),
                    payloadConfig.getSumCumulus(),
                    payloadConfig.getListInsuranceProductModalityDAO()
            );
        }
        LOGGER.info("***** InsrVidaDinamicoBusinessImpl - doDynamicLife |  responseRimac: {} *****",responseRimac);

        //construccion de respuesta trx
        response = prepareResponse(getApplicationConfigurationService(), payloadConfig, responseRimac);

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

        //response.getHolder().getIdentityDocument().getDocumentType().setId(payloadConfig.getProperties().getDocumentTypeIdAsText());

        LOGGER.info("***** InsrVidaDinamicoBusinessImpl - prepareResponse response {} *****",response);
        return response;
    }


}
