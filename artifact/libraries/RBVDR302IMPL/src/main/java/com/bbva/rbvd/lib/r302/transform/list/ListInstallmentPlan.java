package com.bbva.rbvd.lib.r302.transform.list;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.rbvd.dto.lifeinsrc.commons.InsurancePlanDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.InstallmentsDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.PeriodDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.PaymentAmountDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.TotalInstallmentDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.CoverageDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.InsuredAmountDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.UnitDTO;
import com.bbva.rbvd.dto.lifeinsrc.dao.InsuranceProductModalityDAO;
import com.bbva.rbvd.dto.lifeinsrc.rimac.commons.CoberturaBO;
import com.bbva.rbvd.dto.lifeinsrc.rimac.commons.FinanciamientoBO;
import com.bbva.rbvd.dto.lifeinsrc.rimac.commons.PlanBO;
import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.CotizacionBO;
import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.InsuranceLifeSimulationBO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.CoverageTypeDTO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.InsuranceLimitsDTO;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

public class ListInstallmentPlan {

    private static final String AMOUNT_UNIT_TYPE = "AMOUNT";
    private static final String ANNUAL_PERIOD_ID = "ANNUAL";
    private static final String ANNUAL_PERIOD_NAME = "ANUAL";
    private static final String YES_CONSTANT = "S";
    private static final String PLANUNO = "01";

    private static final String PLANDOS = "02";

    private static final String PLANTRES = "03";


    private ApplicationConfigurationService applicationConfigurationService;


    public List<InsurancePlanDTO> getPlansNamesAndRecommendedValuesAndInstallmentsPlans(List<InsuranceProductModalityDAO> productModalities,
                                                                                        InsuranceLifeSimulationBO responseRimac,
                                                                                        Boolean seglifePlan1, Boolean seglifePlan2, Boolean seglifePlan3) {
        List<CotizacionBO> quotations = responseRimac.getPayload().getCotizaciones();
        return productModalities.stream().
                map(modality -> createProductModalityDTO(modality, quotations, seglifePlan1, seglifePlan2, seglifePlan3)).
                filter(Objects::nonNull).
                collect(toList());
    }

    private InsurancePlanDTO createProductModalityDTO(InsuranceProductModalityDAO modalityDao, List<CotizacionBO> quotations,
                                                      Boolean seglifePlan1, Boolean seglifePlan2, Boolean seglifePlan3) {

        InsurancePlanDTO plan = null;

        String rimacPlanCodeFromDB = modalityDao.getInsuranceCompanyModalityId();

        CotizacionBO cotizacion = quotations.stream().filter(quotation -> quotation.getPlan().getPlan().toString().equals(rimacPlanCodeFromDB)).findFirst().orElse(null);

        if(Objects.nonNull(cotizacion)) {

            plan = new InsurancePlanDTO();

            plan.setId(modalityDao.getInsuranceModalityType());
            plan.setName(cotizacion.getPlan().getDescripcionPlan());
            plan.setIsRecommended(setValueRecommended(modalityDao, seglifePlan1, seglifePlan2, seglifePlan3));
            plan.setIsAvailable(indicadorBloqueo(cotizacion.getIndicadorBloqueo()));
            InstallmentsDTO installmentPlan = new InstallmentsDTO();

            PeriodDTO period = new PeriodDTO();

            PlanBO rimacPlan = cotizacion.getPlan();

            FinanciamientoBO monthlyFinancing = cotizacion.getPlan().getFinanciamientos().stream().
                    filter(financing -> "Mensual".equals(financing.getPeriodicidad())).findFirst().orElse(new FinanciamientoBO());

            PaymentAmountDTO amount = new PaymentAmountDTO();

            FinanciamientoBO annualFinancing = cotizacion.getPlan().getFinanciamientos().stream().
                    filter(financing -> "Anual".equals(financing.getPeriodicidad())).findFirst().orElse(new FinanciamientoBO());

            List<InstallmentsDTO> installments = new ArrayList<>();

            String periodicity = monthlyFinancing.getPeriodicidad();
            period.setId(this.applicationConfigurationService.getProperty(periodicity));
            period.setName(periodicity.toUpperCase());
            amount.setAmount(monthlyFinancing.getCuotasFinanciamiento().get(0).getMonto());
            amount.setCurrency(cotizacion.getPlan().getMoneda());
            installmentPlan.setPaymentsTotalNumber(monthlyFinancing.getNumeroCuotas());
            installmentPlan.setPeriod(period);
            installmentPlan.setPaymentAmount(amount);
            installments.add(installmentPlan);


            InstallmentsDTO installmentPlanAnnual = new InstallmentsDTO();
            PeriodDTO periodAnnual = new PeriodDTO();
            String periodicityAnnual = annualFinancing.getPeriodicidad();
            periodAnnual.setId(this.applicationConfigurationService.getProperty(periodicityAnnual));
            periodAnnual.setName(periodicityAnnual.toUpperCase());
            PaymentAmountDTO amountAnnual = new PaymentAmountDTO();
            amountAnnual.setAmount(annualFinancing.getCuotasFinanciamiento().get(0).getMonto());
            amountAnnual.setCurrency(annualFinancing.getCuotasFinanciamiento().get(0).getMoneda());
            installmentPlanAnnual.setPaymentsTotalNumber(annualFinancing.getNumeroCuotas());
            installmentPlanAnnual.setPeriod(periodAnnual);
            installmentPlanAnnual.setPaymentAmount(amountAnnual);
            installments.add(installmentPlanAnnual);

            TotalInstallmentDTO totalInstallmentPlan = new TotalInstallmentDTO();
            PeriodDTO periodAnual = new PeriodDTO();
            totalInstallmentPlan.setAmount(rimacPlan.getPrimaBruta());
            periodAnual.setId(ANNUAL_PERIOD_ID);
            periodAnual.setName(ANNUAL_PERIOD_NAME);
            totalInstallmentPlan.setPeriod(periodAnual);
            totalInstallmentPlan.setCurrency(rimacPlan.getMoneda());

            plan.setInstallmentPlans(installments);
            plan.setTotalInstallment(totalInstallmentPlan);

            List<CoverageDTO> coverages = cotizacion.getPlan().getCoberturas().stream()
                    .map(this::createCoverageDTO).
                    collect(toList());

            plan.setCoverages(coverages);

        }
        return plan;
    }

    private Boolean setValueRecommended(InsuranceProductModalityDAO modalityDao, Boolean seglifePlan1,
                                        Boolean seglifePlan2, Boolean seglifePlan3){
        Boolean checkRecommend = false;
        if(PLANUNO.equals(modalityDao.getInsuranceModalityType())){
            checkRecommend = seglifePlan1;
        }
        if(PLANDOS.equals(modalityDao.getInsuranceModalityType())){
            checkRecommend = seglifePlan2;
        }
        if(PLANTRES.equals(modalityDao.getInsuranceModalityType())){
            checkRecommend = seglifePlan3;
        }
        if(!seglifePlan1 && !seglifePlan2 && !seglifePlan3 && PLANTRES.equals(modalityDao.getInsuranceModalityType())){
            checkRecommend=true;
        }

        return checkRecommend;
    }

    private boolean indicadorBloqueo(Long indicadorBloqueo) {
        boolean result = false;
        result = (0==indicadorBloqueo);
        return result;
    }

    private CoverageDTO createCoverageDTO(CoberturaBO coverage) {
        CoverageDTO coverageDTO = new CoverageDTO();

        coverageDTO.setId(coverage.getCobertura().toString());
        coverageDTO.setName(Objects.nonNull(coverage.getObservacionCobertura()) ? coverage.getObservacionCobertura() : coverage.getDescripcionCobertura());
        coverageDTO.setIsSelected(YES_CONSTANT.equalsIgnoreCase(coverage.getPrincipal()));
        coverageDTO.setDescription(Objects.nonNull(coverage.getDetalleCobertura()) ? coverage.getDetalleCobertura() : coverage.getDescripcionCobertura());
        coverageDTO.setUnit(createUnit(coverage));
        coverageDTO.setCoverageType(coverageType(coverage));

        coverageDTO.setFeePaymentAmount(createPaymentAmount(coverage));
        coverageDTO.setInsuredAmount(createInsuredAmount(coverageDTO));
        coverageDTO.setCoverageLimits(createInsuranceLimits(coverage));

        return coverageDTO;
    }

    private PaymentAmountDTO createPaymentAmount(CoberturaBO coverage){
        PaymentAmountDTO paymentAmountDTO = new PaymentAmountDTO();

        paymentAmountDTO.setAmount(coverage.getPrimaBruta());
        paymentAmountDTO.setCurrency(coverage.getMoneda());

        return paymentAmountDTO;
    }

    private InsuredAmountDTO createInsuredAmount(CoverageDTO coverage){
        InsuredAmountDTO insurancePlanDTO = new InsuredAmountDTO();

        insurancePlanDTO.setAmount(coverage.getAmount());
        insurancePlanDTO.setCurrency(coverage.getCurrency());

        return insurancePlanDTO;
    }

    private InsuranceLimitsDTO createInsuranceLimits (CoberturaBO coverage) {
        InsuranceLimitsDTO coverageLimits = new InsuranceLimitsDTO();

        coverageLimits.getMinimumAmount().setAmount(coverage.getSumaAseguradaMinima());
        coverageLimits.getMaximumAmount().setAmount(coverage.getSumaAseguradaMaxima());

        return null;
    }

    private UnitDTO createUnit(CoberturaBO coverage){
        UnitDTO unit = new UnitDTO();
        unit.setUnitType(AMOUNT_UNIT_TYPE);
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

    public void setApplicationConfigurationService(ApplicationConfigurationService applicationConfigurationService) {
        this.applicationConfigurationService = applicationConfigurationService;
    }
}
