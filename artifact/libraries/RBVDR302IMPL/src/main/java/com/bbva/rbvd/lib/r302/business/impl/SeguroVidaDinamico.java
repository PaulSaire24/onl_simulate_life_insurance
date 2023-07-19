package com.bbva.rbvd.lib.r302.business.impl;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.InsuranceLifeSimulationBO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDErrors;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDValidation;
import com.bbva.rbvd.lib.r301.RBVDR301;
import com.bbva.rbvd.lib.r302.business.ISeguroVidaDinamico;
import com.bbva.rbvd.lib.r302.impl.util.MockResponse;
import com.bbva.rbvd.lib.r302.transform.objects.ModifyQuotationRimac;
import com.bbva.rbvd.lib.r302.transform.objects.QuotationRimac;

import java.math.BigDecimal;
import java.util.Objects;

public class SeguroVidaDinamico implements ISeguroVidaDinamico {

    private RBVDR301 rbvdR301;

    public SeguroVidaDinamico(RBVDR301 rbvdR301) {
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


}
