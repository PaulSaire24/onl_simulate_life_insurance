package com.bbva.rbvd.lib.r302.impl.util;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.pisd.dto.insurance.aso.CustomerListASO;

import com.bbva.pisd.dto.insurance.aso.gifole.GifoleInsuranceRequestASO;
import com.bbva.pisd.dto.insurance.aso.gifole.PlanASO;
import com.bbva.pisd.dto.insurance.aso.gifole.ProductASO;
import com.bbva.pisd.dto.insurance.aso.gifole.AmountASO;

import com.bbva.pisd.dto.insurance.aso.gifole.InstallmentPlanASO;
import com.bbva.pisd.dto.insurance.aso.gifole.PeriodASO;
import com.bbva.pisd.dto.insurance.aso.gifole.BankASO;
import com.bbva.pisd.dto.insurance.aso.gifole.BranchASO;
import com.bbva.pisd.dto.insurance.aso.gifole.DocumentTypeASO;
import com.bbva.pisd.dto.insurance.aso.gifole.IdentityDocumentASO;
import com.bbva.pisd.dto.insurance.aso.gifole.HolderASO;
import com.bbva.pisd.dto.insurance.aso.gifole.ContactASO;
import com.bbva.pisd.dto.insurance.aso.gifole.ContactDetailASO;

import com.bbva.pisd.dto.insurance.aso.gifole.GoodASO;
import com.bbva.pisd.dto.insurance.aso.gifole.GoodDetailASO;
import com.bbva.pisd.dto.insurance.aso.tier.TierASO;
import com.bbva.pisd.dto.insurance.bo.ContactDetailsBO;
import com.bbva.pisd.dto.insurance.bo.IdentityDocumentsBO;
import com.bbva.pisd.dto.insurance.bo.customer.CustomerBO;
import com.bbva.rbvd.dto.lifeinsrc.commons.TierDTO;

import com.bbva.rbvd.dto.lifeinsrc.commons.PaymentAmountDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.PeriodDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.TotalInstallmentDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.InsurancePlanDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.CoverageDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.InstallmentsDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.UnitDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.InsuranceProductDTO;
import com.bbva.rbvd.dto.lifeinsrc.dao.InsuranceProductModalityDAO;
import com.bbva.rbvd.dto.lifeinsrc.dao.SimulationDAO;
import com.bbva.rbvd.dto.lifeinsrc.dao.SimulationProductDAO;
import com.bbva.rbvd.dto.lifeinsrc.rimac.commons.CoberturaBO;
import com.bbva.rbvd.dto.lifeinsrc.rimac.commons.DatoParticularBO;
import com.bbva.rbvd.dto.lifeinsrc.rimac.commons.FinanciamientoBO;
import com.bbva.rbvd.dto.lifeinsrc.rimac.commons.PlanBO;
import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.AseguradoBO;
import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.CotizacionBO;
import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.InsuranceLifeSimulationBO;
import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.SimulacionLifePayloadBO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.CoverageTypeDTO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDProperties;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.HashMap;
import java.util.Objects;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.util.CollectionUtils;

import static java.util.stream.Collectors.toList;

public class MapperHelper {



    private static final String AMOUNT_UNIT_TYPE = "AMOUNT";

    private static final String YES_CONSTANT = "S";
    private static final String ANNUAL_PERIOD_ID = "ANNUAL";


    private static final String ANNUAL_PERIOD_NAME = "ANUAL";




    protected ApplicationConfigurationService applicationConfigurationService;

    private static final String PLANUNO = "01";

    private static final String PLANDOS = "02";

    private static final String PLANTRES = "03";






    public void mapOutRequestRimacLife(InsuranceLifeSimulationBO responseRimac, LifeSimulationDTO response){

        response.getProduct().setName(responseRimac.getPayload().getProducto());
        response.setExternalSimulationId(responseRimac.getPayload().getCotizaciones().get(0).getCotizacion());

    }


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

    private boolean indicadorBloqueo(Long indicadorBloqueo) {
        boolean result = false;
        result = (0==indicadorBloqueo);
        return result;
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

    private CoverageDTO createCoverageDTO(final CoberturaBO coverage) {
        CoverageDTO coverageDTO = new CoverageDTO();

        coverageDTO.setId(coverage.getCobertura().toString());
        coverageDTO.setName(coverage.getObservacionCobertura());
        coverageDTO.setIsSelected(YES_CONSTANT.equalsIgnoreCase(coverage.getPrincipal()));
        coverageDTO.setDescription(coverage.getDetalleCobertura());
        coverageDTO.setUnit(createUnit(coverage));
        coverageDTO.setCoverageType(coverageType(coverage));

        return coverageDTO;
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

    private UnitDTO createUnit(CoberturaBO coverage){
        UnitDTO unit = new UnitDTO();
        unit.setUnitType(AMOUNT_UNIT_TYPE);
        unit.setAmount(coverage.getSumaAsegurada());
        unit.setCurrency(coverage.getMoneda());

        return unit;
    }







    public void mappingTierASO(LifeSimulationDTO input, TierASO responseTierASO) {
        if (Objects.nonNull(responseTierASO)) {
            TierDTO tierDTO = new TierDTO();
            tierDTO.setId(responseTierASO.getData().get(0).getId());
            tierDTO.setName(responseTierASO.getData().get(0).getDescription());
            input.setTier(tierDTO);
            input.setBankingFactor(responseTierASO.getData().get(0).getChargeFactor());
            if(Objects.nonNull(responseTierASO.getData().get(0).getSegments())) {
                input.setId(responseTierASO.getData().get(0).getSegments().get(0).getId());
            }else {
                input.setId(null);
            }
        }
    }

    public Boolean selectValuePlansDescription(String segmentoPlan, LifeSimulationDTO input){
        boolean valuePlus= false;
        String[] lifeArray = segmentoPlan.split(",");
        List<String> listSegment = Arrays.stream(lifeArray).collect(toList());
        String valueRetail = null;
        valueRetail = listSegment.stream().filter(retail -> retail.equals(input.getId())).findFirst().orElse(null);
        if(null!=valueRetail){
            valuePlus=true;
        }
        return valuePlus;
    }

    public void setApplicationConfigurationService(ApplicationConfigurationService applicationConfigurationService) {
        this.applicationConfigurationService = applicationConfigurationService;
    }
}
