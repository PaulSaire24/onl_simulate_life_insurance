package com.bbva.rbvd.lib.r302.impl.util;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.rbvd.dto.lifeinsrc.commons.PaymentAmountDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.PeriodDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.TotalInstallmentDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.InsurancePlanDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.CoverageDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.InstallmentsDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.UnitDTO;
import com.bbva.rbvd.dto.lifeinsrc.dao.InsuranceProductModalityDAO;
import com.bbva.rbvd.dto.lifeinsrc.dao.SimulationDAO;
import com.bbva.rbvd.dto.lifeinsrc.dao.SimulationProductDAO;
import com.bbva.rbvd.dto.lifeinsrc.rimac.commons.CoberturaBO;
import com.bbva.rbvd.dto.lifeinsrc.rimac.commons.DatoParticularBO;
import com.bbva.rbvd.dto.lifeinsrc.rimac.commons.FinanciamientoBO;
import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.AseguradoBO;
import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.CotizacionBO;
import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.InsuranceLifeSimulationBO;
import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.SimulacionLifePayloadBO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.CoverageTypeDTO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDProperties;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Objects;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

import static java.util.stream.Collectors.toList;

public class MapperHelper {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    private static final String AMOUNT_UNIT_TYPE = "AMOUNT";

    private static final String YES_CONSTANT = "S";

    private static final String ANNUAL = "ANNUAL";

    protected ApplicationConfigurationService applicationConfigurationService;

    public InsuranceLifeSimulationBO mapInRequestRimacLife(LifeSimulationDTO input){

        InsuranceLifeSimulationBO simulationBo = new InsuranceLifeSimulationBO();
        SimulacionLifePayloadBO payload = new SimulacionLifePayloadBO();

        payload.setMoneda("PEN");

        DatoParticularBO datos = new DatoParticularBO();
        List<DatoParticularBO> datosParticulares = new ArrayList<>();
        datos.setEtiqueta("CUMULO_CLIENTE");
        datos.setCodigo("");
        datos.setValor("20000");
        datosParticulares.add(datos);
        payload.setDatosParticulares(datosParticulares);

        AseguradoBO asegurado = new AseguradoBO();
        asegurado.setTipoDocumento(input.getHolder().getIdentityDocument().getDocumentType().getId());
        asegurado.setNumeroDocumento(input.getHolder().getIdentityDocument().getDocumentNumber());
        payload.setAsegurado(asegurado);

        List<Integer> list = Arrays.asList(1);
        payload.setPeriodosConDescuentoPrima(list);

        simulationBo.setPayload(payload);
        return simulationBo;
    }

    public Map<String, Object> mapProductId(String arguments){

        Map<String, Object> mapStringObject = new HashMap<>();
        mapStringObject.put(RBVDProperties.FILTER_INSURANCE_PRODUCT_TYPE.getValue(), arguments);

        return mapStringObject;
    }

    public void mapOutRequestRimacLife(InsuranceLifeSimulationBO responseRimac, LifeSimulationDTO response){

        response.getProduct().setName(responseRimac.getPayload().getProducto());
        response.setExternalSimulationId(responseRimac.getPayload().getCotizaciones().get(0).getCotizacion());

    }

    public Map<String, Object> createModalitiesInformationFilters(String plansPT, BigDecimal insuranceProductId, String saleChannel) {
        Map<String, Object> filters = new HashMap<>();

        String[] plansPTArray = plansPT.split(",");
        List<String> planes = Arrays.stream(plansPTArray).collect(toList());

        filters.put(RBVDProperties.FIELD_OR_FILTER_INSURANCE_PRODUCT_ID.getValue(), insuranceProductId);
        filters.put(RBVDProperties.FIELD_OR_FILTER_INSURANCE_MODALITY_TYPE.getValue(), planes);
        filters.put(RBVDProperties.FIELD_SALE_CHANNEL_ID.getValue(), saleChannel);
        return filters;
    }

    public List<InsurancePlanDTO> getPlansNamesAndRecommendedValuesAndInstallmentsPlans(List<InsuranceProductModalityDAO> productModalities, InsuranceLifeSimulationBO responseRimac) {

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
            plan.setName(modalityDao.getInsuranceModalityName());
            plan.setIsRecommended("03".equalsIgnoreCase(modalityDao.getInsuranceModalityType()));

            InstallmentsDTO installmentPlan = new InstallmentsDTO();

            PeriodDTO period = new PeriodDTO();

            FinanciamientoBO monthlyFinancing = cotizacion.getPlan().getFinanciamientos().stream().
                    filter(financing -> "Mensual".equals(financing.getPeriodicidad())).findFirst().orElse(new FinanciamientoBO());

            PaymentAmountDTO amount = new PaymentAmountDTO();

            FinanciamientoBO annualFinancing = cotizacion.getPlan().getFinanciamientos().stream().
                    filter(financing -> "Anual".equals(financing.getPeriodicidad())).findFirst().orElse(null);

            String periodicity = monthlyFinancing.getPeriodicidad();

            period.setId(this.applicationConfigurationService.getProperty(periodicity));
            period.setName(periodicity);

            amount.setAmount(monthlyFinancing.getCuotasFinanciamiento().get(0).getMonto());

            amount.setCurrency(cotizacion.getPlan().getMoneda());
            installmentPlan.setPaymentsTotalNumber(monthlyFinancing.getNumeroCuotas());

            TotalInstallmentDTO totalInstallmentPlan = new TotalInstallmentDTO();
            PeriodDTO periodAnual = new PeriodDTO();
            String periodicityAnual = "";

            if (Objects.nonNull(annualFinancing)) {
                totalInstallmentPlan.setAmount(annualFinancing.getCuotasFinanciamiento().get(0).getMonto());
                periodicityAnual = annualFinancing.getPeriodicidad();
                periodAnual.setId(this.applicationConfigurationService.getProperty(periodicityAnual));
                periodAnual.setName(periodicityAnual);
            } else {
                periodAnual.setId(ANNUAL);
                totalInstallmentPlan.setAmount(cotizacion.getPlan().getPrimaBruta());
            }
            totalInstallmentPlan.setPeriod(periodAnual);

            totalInstallmentPlan.setCurrency(cotizacion.getPlan().getMoneda());
            List<InstallmentsDTO> installments = new ArrayList<>();

            installmentPlan.setPeriod(period);
            installmentPlan.setPaymentAmount(amount);
            installments.add(installmentPlan);
            plan.setInstallmentPlans(installments);
            plan.setTotalInstallment(totalInstallmentPlan);

        }
        return plan;
    }

    public void putConsiderations(List<InsurancePlanDTO> plans, List<CotizacionBO> cotizaciones) {
        plans.forEach(plan -> getConsiderationsForThisPlan(plan, cotizaciones));
    }

    private void getConsiderationsForThisPlan(InsurancePlanDTO plan, List<CotizacionBO> cotizaciones) {

        List<CoverageDTO> coverages = cotizaciones.get(0).getPlan()
                .getCoberturas().stream().
                map(this::createCoverageDTO).
                collect(toList());

        plan.setCoverages(coverages);

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

    public SimulationDAO createSimulationDAO(BigDecimal insuranceSimulationId, final Date maturityDate, LifeSimulationDTO insuranceSimulationDTO) {
        SimulationDAO simulationDAO = new SimulationDAO();
        simulationDAO.setInsuranceSimulationId(insuranceSimulationId);
        simulationDAO.setInsrncCompanySimulationId(insuranceSimulationDTO.getExternalSimulationId());
        simulationDAO.setCustomerId(insuranceSimulationDTO.getHolder().getId());
        simulationDAO.setCustomerSimulationDate(dateFormat.format(new Date()));
        simulationDAO.setCustSimulationExpiredDate(dateFormat.format(maturityDate));
        simulationDAO.setParticipantPersonalId(insuranceSimulationDTO.getHolder().getIdentityDocument().getDocumentNumber());

        return simulationDAO;
    }

    public Map<String, Object> createArgumentsForSaveSimulation(SimulationDAO simulationDAO, String creationUser, String userAudit, String documentTypeId) {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put(RBVDProperties.FIELD_INSURANCE_SIMULATION_ID.getValue(), simulationDAO.getInsuranceSimulationId());
        arguments.put(RBVDProperties.FIELD_INSRNC_COMPANY_SIMULATION_ID.getValue(), simulationDAO.getInsrncCompanySimulationId());
        arguments.put(RBVDProperties.FIELD_CUSTOMER_ID.getValue(), simulationDAO.getCustomerId());
        arguments.put(RBVDProperties.FIELD_CUSTOMER_SIMULATION_DATE.getValue(), simulationDAO.getCustomerSimulationDate());
        arguments.put(RBVDProperties.FIELD_CUST_SIMULATION_EXPIRED_DATE.getValue(), simulationDAO.getCustSimulationExpiredDate());
        arguments.put(RBVDProperties.FIELD_BANK_FACTOR_TYPE.getValue(), simulationDAO.getBankFactorType());
        arguments.put(RBVDProperties.FIELD_BANK_FACTOR_AMOUNT.getValue(), simulationDAO.getBankFactorAmount());
        arguments.put(RBVDProperties.FIELD_BANK_FACTOR_PER.getValue(), simulationDAO.getBankFactorPer());
        arguments.put(RBVDProperties.FIELD_SOURCE_BRANCH_ID.getValue(), simulationDAO.getSourceBranchId());
        arguments.put(RBVDProperties.FIELD_CREATION_USER_ID.getValue(), creationUser);
        arguments.put(RBVDProperties.FIELD_USER_AUDIT_ID.getValue(), userAudit);
        arguments.put(RBVDProperties.FIELD_PERSONAL_DOC_TYPE.getValue(), documentTypeId);
        arguments.put(RBVDProperties.FIELD_PARTICIPANT_PERSONAL_ID.getValue(), simulationDAO.getParticipantPersonalId());
        arguments.put(RBVDProperties.FIELD_INSURED_CUSTOMER_NAME.getValue(), null);
        arguments.put(RBVDProperties.FIELD_CLIENT_LAST_NAME.getValue(), null);
        arguments.put(RBVDProperties.FIELD_CUSTOMER_SEGMENT_NAME.getValue(), null);
        return arguments;
    }

    public SimulationProductDAO createSimulationProductDAO(BigDecimal insuranceSimulationId, BigDecimal productId, String creationUser, String userAudit, LifeSimulationDTO insuranceSimulationDto) {
        SimulationProductDAO simulationProductDAO = new SimulationProductDAO();
        simulationProductDAO.setInsuranceSimulationId(insuranceSimulationId);
        simulationProductDAO.setInsuranceProductId(productId);
        simulationProductDAO.setSaleChannelId(insuranceSimulationDto.getSaleChannelId());
        simulationProductDAO.setCreationUser(creationUser);
        simulationProductDAO.setUserAudit(userAudit);
        return simulationProductDAO;
    }

    public Map<String, Object> createArgumentsForSaveSimulationProduct(SimulationProductDAO simulationProductDAO) {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put(RBVDProperties.FIELD_INSURANCE_SIMULATION_ID.getValue(), simulationProductDAO.getInsuranceSimulationId());
        arguments.put(RBVDProperties.FIELD_OR_FILTER_INSURANCE_PRODUCT_ID.getValue(), simulationProductDAO.getInsuranceProductId());
        arguments.put(RBVDProperties.FIELD_CAMPAIGN_FACTOR_TYPE.getValue(), simulationProductDAO.getCampaignFactorType());
        arguments.put(RBVDProperties.FIELD_CAMPAIGN_OFFER_1_AMOUNT.getValue(), simulationProductDAO.getCampaignOffer1Amount());
        arguments.put(RBVDProperties.FIELD_CAMPAIGN_FACTOR_PER.getValue(), simulationProductDAO.getCampaignFactorPer());
        arguments.put(RBVDProperties.FIELD_SALE_CHANNEL_ID.getValue(), simulationProductDAO.getSaleChannelId());
        arguments.put(RBVDProperties.FIELD_SOURCE_BRANCH_ID.getValue(), simulationProductDAO.getSourceBranchId());
        arguments.put(RBVDProperties.FIELD_CREATION_USER_ID.getValue(), simulationProductDAO.getCreationUser());
        arguments.put(RBVDProperties.FIELD_USER_AUDIT_ID.getValue(), simulationProductDAO.getUserAudit());

        return arguments;
    }

    public void setApplicationConfigurationService(ApplicationConfigurationService applicationConfigurationService) {
        this.applicationConfigurationService = applicationConfigurationService;
    }
}
