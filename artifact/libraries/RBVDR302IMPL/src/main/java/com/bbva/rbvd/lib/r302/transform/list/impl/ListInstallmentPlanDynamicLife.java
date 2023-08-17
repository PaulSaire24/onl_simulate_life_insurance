package com.bbva.rbvd.lib.r302.transform.list.impl;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.rbvd.dto.lifeinsrc.commons.InsurancePlanDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.InstallmentsDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.CoverageDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.TotalInstallmentDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.PeriodDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.PaymentAmountDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.UnitDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.InsuredAmountDTO;
import com.bbva.rbvd.dto.lifeinsrc.dao.InsuranceProductModalityDAO;
import com.bbva.rbvd.dto.lifeinsrc.rimac.commons.CoberturaBO;
import com.bbva.rbvd.dto.lifeinsrc.rimac.commons.FinanciamientoBO;
import com.bbva.rbvd.dto.lifeinsrc.rimac.commons.PlanBO;
import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.CotizacionBO;
import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.InsuranceLifeSimulationBO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.CoverageTypeDTO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.InsuranceLimitsDTO;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDProperties;
import com.bbva.rbvd.lib.r302.transform.list.IListInstallmentPlan;
import com.bbva.rbvd.lib.r302.util.ConstantsUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

public class ListInstallmentPlanDynamicLife implements IListInstallmentPlan {

    private final ApplicationConfigurationService applicationConfigurationService;

    public ListInstallmentPlanDynamicLife(ApplicationConfigurationService applicationConfigurationService) {
        this.applicationConfigurationService = applicationConfigurationService;
    }

    @Override
    public List<InsurancePlanDTO> getPlansNamesAndRecommendedValuesAndInstallmentsPlans(List<InsuranceProductModalityDAO> productModalities, InsuranceLifeSimulationBO responseRimac, List<Boolean> seglifePlans) {
        List<CotizacionBO> quotations = responseRimac.getPayload().getCotizaciones();
        return productModalities.stream().
                map(modality -> createProductModalityDTO(modality, quotations)).
                filter(Objects::nonNull).
                collect(toList());
    }

    private InsurancePlanDTO createProductModalityDTO(InsuranceProductModalityDAO modalityDao, List<CotizacionBO> quotations) {

        InsurancePlanDTO plan = null;

        String rimacPlanCodeFromDB = modalityDao.getInsuranceCompanyModalityId();

        CotizacionBO cotizacion = quotations.stream().filter(quotation -> quotation.getPlan().getPlan().toString().equals(rimacPlanCodeFromDB)).findFirst().orElse(null);

        if(Objects.nonNull(cotizacion)) {

            plan = new InsurancePlanDTO();

            plan.setId(modalityDao.getInsuranceModalityType());
            plan.setName(cotizacion.getPlan().getDescripcionPlan());
            plan.setIsAvailable(indicadorBloqueo(cotizacion.getIndicadorBloqueo()));

            PlanBO rimacPlan = cotizacion.getPlan();

            FinanciamientoBO monthlyFinancing = cotizacion.getPlan().getFinanciamientos().stream().
                    filter(financing -> "Mensual".equals(financing.getPeriodicidad())).findFirst().orElse(null);

            FinanciamientoBO annualFinancing = cotizacion.getPlan().getFinanciamientos().stream().
                    filter(financing -> "Anual".equals(financing.getPeriodicidad())).findFirst().orElse(null);

            FinanciamientoBO biMonthlyFinancing = cotizacion.getPlan().getFinanciamientos().stream().
                    filter(financing -> "Semestral".equals(financing.getPeriodicidad())).findFirst().orElse(null);

            FinanciamientoBO quarterlyFinancing = cotizacion.getPlan().getFinanciamientos().stream().
                    filter(financing -> "Trimestral".equals(financing.getPeriodicidad())).findFirst().orElse(null);

            List<InstallmentsDTO> installments = new ArrayList<>();

            if(monthlyFinancing != null){
                InstallmentsDTO installmentPlanMonthly = this.getInstallmentPlan(monthlyFinancing);
                installments.add(installmentPlanMonthly);
            }

            if(annualFinancing != null){
                InstallmentsDTO installmentPlanAnnual = this.getInstallmentPlan(annualFinancing);
                installments.add(installmentPlanAnnual);
            }

            if(biMonthlyFinancing != null){
                InstallmentsDTO installmentPlanBiMonthly = this.getInstallmentPlan(biMonthlyFinancing);
                installments.add(installmentPlanBiMonthly);
            }

            if(quarterlyFinancing != null){
                InstallmentsDTO installmentPlanQuarterly = this.getInstallmentPlan(quarterlyFinancing);
                installments.add(installmentPlanQuarterly);
            }

            plan.setInstallmentPlans(installments);
            plan.setTotalInstallment(getTotalInstallmentDTO(rimacPlan));

            List<CoverageDTO> coverages = cotizacion.getPlan().getCoberturas().stream()
                    .map(this::createCoverageDTO).
                    collect(toList());

            plan.setCoverages(coverages);

        }
        return plan;
    }

    private static TotalInstallmentDTO getTotalInstallmentDTO(PlanBO rimacPlan) {
        TotalInstallmentDTO totalInstallmentPlan = new TotalInstallmentDTO();

        PeriodDTO periodAnual = new PeriodDTO();
        totalInstallmentPlan.setAmount(rimacPlan.getPrimaBruta());
        periodAnual.setId(ConstantsUtil.ANNUAL_PERIOD_ID);
        periodAnual.setName(ConstantsUtil.ANNUAL_PERIOD_NAME);
        totalInstallmentPlan.setPeriod(periodAnual);
        totalInstallmentPlan.setCurrency(rimacPlan.getMoneda());

        return totalInstallmentPlan;
    }

    private InstallmentsDTO getInstallmentPlan(FinanciamientoBO financing) {
        InstallmentsDTO installmentPlan = new InstallmentsDTO();

        PeriodDTO period = new PeriodDTO();
        String periodicity = financing.getPeriodicidad();
        period.setId(this.applicationConfigurationService.getProperty(periodicity));
        period.setName(periodicity.toUpperCase());

        PaymentAmountDTO amount = new PaymentAmountDTO();
        amount.setAmount(financing.getCuotasFinanciamiento().get(0).getMonto());
        amount.setCurrency(financing.getCuotasFinanciamiento().get(0).getMoneda());

        installmentPlan.setPaymentsTotalNumber(financing.getNumeroCuotas());
        installmentPlan.setPeriod(period);
        installmentPlan.setPaymentAmount(amount);

        return installmentPlan;
    }

    private boolean indicadorBloqueo(Long indicadorBloqueo) {
        boolean result = false;
        result = (0==indicadorBloqueo);
        return result;
    }

    private CoverageDTO createCoverageDTO(CoberturaBO coverage) {
        CoverageDTO coverageDTO = new CoverageDTO();

        coverageDTO.setId(coverage.getCobertura().toString());
        coverageDTO.setName(Objects.nonNull(coverage.getDescripcionCobertura()) ? coverage.getDescripcionCobertura() : "");
        coverageDTO.setIsSelected(ConstantsUtil.YES_CONSTANT.equalsIgnoreCase(coverage.getIndSeleccionar()));
        coverageDTO.setDescription(Objects.nonNull(coverage.getObservacionCobertura()) ? coverage.getObservacionCobertura() : "");
        coverageDTO.setUnit(createUnit(coverage));
        coverageDTO.setCoverageType(coverageType(coverage));
        coverageDTO.setFeePaymentAmount(createPaymentAmount(coverage));
        coverageDTO.setInsuredAmount(createInsuredAmount(coverage));
        coverageDTO.setCoverageLimits(createInsuranceLimits(coverage));

        return coverageDTO;
    }

    private UnitDTO createUnit(CoberturaBO coverage){
        UnitDTO unit = new UnitDTO();
        unit.setUnitType(ConstantsUtil.AMOUNT_UNIT_TYPE);
        unit.setAmount(coverage.getSumaAsegurada());
        unit.setCurrency(coverage.getMoneda());

        return unit;
    }

    private CoverageTypeDTO coverageType(CoberturaBO coverage){
        CoverageTypeDTO coverageTypeDTO = new CoverageTypeDTO();
        switch(coverage.getCondicion()) {
            case "INC":
                coverageTypeDTO.setId(RBVDProperties.ID_INCLUDED_COVERAGE.getValue());
                coverageTypeDTO.setName(RBVDProperties.NAME_INCLUDED_COVERAGE.getValue());
                break;
            case "OBL":
                coverageTypeDTO.setId(RBVDProperties.ID_MANDATORY_COVERAGE.getValue());
                coverageTypeDTO.setName(RBVDProperties.NAME_MANDATORY_COVERAGE.getValue());
                break;
            case "OPC":
                coverageTypeDTO.setId(RBVDProperties.ID_OPTIONAL_COVERAGE.getValue());
                coverageTypeDTO.setName(RBVDProperties.NAME_OPTIONAL_COVERAGE.getValue());
                break;
            default:
                break;
        }
        return coverageTypeDTO;
    }

    private InsuranceLimitsDTO createInsuranceLimits (CoberturaBO coverage) {

        if(coverage != null && Objects.nonNull(coverage.getSumaAseguradaMinima()) && Objects.nonNull(coverage.getSumaAseguradaMaxima())){
            InsuranceLimitsDTO insuranceLimits = new InsuranceLimitsDTO();
            InsuredAmountDTO minimumAmount = new InsuredAmountDTO();
            InsuredAmountDTO maximumAmount = new InsuredAmountDTO();

            minimumAmount.setCurrency(coverage.getMoneda());
            minimumAmount.setAmount(coverage.getSumaAseguradaMinima());

            maximumAmount.setCurrency(coverage.getMoneda());
            maximumAmount.setAmount(coverage.getSumaAseguradaMaxima());

            insuranceLimits.setMaximumAmount(maximumAmount);
            insuranceLimits.setMinimumAmount(minimumAmount);

            return insuranceLimits;
        }else{
            return null;
        }

    }

    private static PaymentAmountDTO createPaymentAmount(CoberturaBO coverage){

        if(coverage != null && Objects.nonNull(coverage.getPrimaBruta())){
            PaymentAmountDTO paymentAmountDTO = new PaymentAmountDTO();

            paymentAmountDTO.setAmount(coverage.getPrimaBruta());
            paymentAmountDTO.setCurrency(coverage.getMoneda());
            return paymentAmountDTO;
        }else{
            return null;
        }

    }

    private static InsuredAmountDTO createInsuredAmount(CoberturaBO coverage){

        if(coverage != null && Objects.nonNull(coverage.getSumaAsegurada())){
            InsuredAmountDTO insurancePlanDTO = new InsuredAmountDTO();

            insurancePlanDTO.setAmount(coverage.getSumaAsegurada());
            insurancePlanDTO.setCurrency(coverage.getMoneda());

            return insurancePlanDTO;
        }else{
            return null;
        }

    }

}
