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

    private static final String CONTACT_DETAIL_MOBILE_TYPE = "MOBILE_NUMBER";

    private static final String CONTACT_DETAIL_EMAIL_TYPE = "EMAIL";

    private static final String CONTACT_DETAIL_MOBILE_TYPE_GIFOLE = "PHONE";

    private static final String DEFAULT_BRANCH_ID = "0814";

    private static final String DEFAULT_BANK_ID = "0011";

    private static final DateTimeZone DATE_TIME_ZONE = DateTimeZone.forID("America/Lima");

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    private static final String INSURANCE_SIMULATION_VALUE = "INSURANCE_SIMULATION";

    protected ApplicationConfigurationService applicationConfigurationService;

    private static final String PLANUNO = "01";

    private static final String PLANDOS = "02";

    private static final String PLANTRES = "03";

    private static final String INSURANCE_TYPE_LIFE_VALUE = "LIFE";

    public InsuranceLifeSimulationBO mapInRequestRimacLife(LifeSimulationDTO input, BigDecimal sumCumulus){

        InsuranceLifeSimulationBO simulationBo = new InsuranceLifeSimulationBO();
        SimulacionLifePayloadBO payload = new SimulacionLifePayloadBO();

        payload.setMoneda("PEN");

        DatoParticularBO datos = new DatoParticularBO();
        List<DatoParticularBO> datosParticulares = new ArrayList<>();
        datos.setEtiqueta("CUMULO_CLIENTE");
        datos.setCodigo("");
        datos.setValor(sumCumulus == null ? "0" : String.valueOf(sumCumulus));
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


    public void addFieldsDatoParticulares(InsuranceLifeSimulationBO rimacRequest, LifeSimulationDTO input, CustomerListASO responseListCustomers){

        DatoParticularBO datos1 = new DatoParticularBO();
        DatoParticularBO datos2 = new DatoParticularBO();
        DatoParticularBO datos3 = new DatoParticularBO();
        DatoParticularBO datos4 = new DatoParticularBO();
        DatoParticularBO datos5 = new DatoParticularBO();

        datos1.setEtiqueta(RBVDProperties.DATO_PARTICULAR_EDAD_ASEGURADO.getValue());
        datos1.setCodigo("");
        datos1.setValor(calculateYeardOldCustomer(responseListCustomers.getData().get(0).getBirthData().getBirthDate()));
        rimacRequest.getPayload().getDatosParticulares().add(datos1);

        datos2.setEtiqueta(RBVDProperties.DATO_PARTICULAR_SUMA_ASEGURADA_COBERTURA_FALLECIMIENTO.getValue());
        datos2.setCodigo("");
        datos2.setValor(input.getInsuredAmount() != null ? String.valueOf(input.getInsuredAmount().getAmount()) : "0");
        rimacRequest.getPayload().getDatosParticulares().add(datos2);

        datos3.setEtiqueta(RBVDProperties.DATO_PARTICULAR_PERIODO_ANOS.getValue());
        datos3.setCodigo("");
        datos3.setValor(input.getTerm() != null ? String.valueOf(input.getTerm().getNumber()) : "5");
        rimacRequest.getPayload().getDatosParticulares().add(datos3);

        datos4.setEtiqueta(RBVDProperties.DATO_PARTICULAR_PORCENTAJE_DEVOLUCION.getValue());
        datos4.setCodigo("");
        datos4.setValor(CollectionUtils.isEmpty(input.getListRefunds()) ? "0" : String.valueOf(input.getListRefunds().get(0).getUnit().getPercentage()));
        rimacRequest.getPayload().getDatosParticulares().add(datos4);

        datos5.setEtiqueta(RBVDProperties.DATO_PARTICULAR_INDICADOR_ENDOSADO.getValue());
        datos5.setCodigo("");
        datos5.setValor("N");
        rimacRequest.getPayload().getDatosParticulares().add(datos5);
    }

    private String calculateYeardOldCustomer(String birthDate){

        LocalDate hoy = LocalDate.now();
        LocalDate nacimiento = LocalDate.parse(birthDate);
        Long years = ChronoUnit.YEARS.between(nacimiento, hoy);

        return years.toString();
    }


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


    public GifoleInsuranceRequestASO createGifoleASO(LifeSimulationDTO response, CustomerListASO responseListCustomers){

        InsuranceProductDTO productDto = response.getProduct();

        InsurancePlanDTO planDTO = productDto.getPlans().stream().filter(r -> r.getIsRecommended()).findFirst().orElse(new InsurancePlanDTO());
        List<InstallmentsDTO> installmentPlanDto = planDTO.getInstallmentPlans();

        GifoleInsuranceRequestASO gifoleInsuranceRequest = new GifoleInsuranceRequestASO();

        ProductASO product = new ProductASO();
        product.setId(productDto.getId());
        product.setName(productDto.getName());

        PlanASO plan = new PlanASO();

        plan.setId(planDTO.getId());
        plan.setName(planDTO.getName());

        product.setPlan(plan);

        InstallmentPlanASO installmentPlan = new InstallmentPlanASO();

        AmountASO premiumAmount = new AmountASO();
        premiumAmount.setAmount(installmentPlanDto.get(0).getPaymentAmount().getAmount());
        premiumAmount.setCurrency(installmentPlanDto.get(0).getPaymentAmount().getCurrency());

        installmentPlan.setPremiumAmount(premiumAmount);

        PeriodASO period = new PeriodASO();
        period.setId(installmentPlanDto.get(0).getPeriod().getId());
        period.setName(installmentPlanDto.get(0).getPeriod().getName());

        installmentPlan.setPeriod(period);

        installmentPlan.setTotalInstallmentsNumber(installmentPlanDto.get(0).getPaymentsTotalNumber());

        Optional<InstallmentsDTO> planObj = installmentPlanDto.stream().filter(p -> ANNUAL_PERIOD_ID.equals(p.getPeriod().getId())).findFirst();
        AmountASO totalPremiumAmount = new AmountASO();
        totalPremiumAmount.setAmount(planObj.isPresent() ? planObj.get().getPaymentAmount().getAmount() : planDTO.getTotalInstallment().getAmount());
        totalPremiumAmount.setCurrency(planObj.isPresent() ? planObj.get().getPaymentAmount().getCurrency() : planDTO.getTotalInstallment().getCurrency());

        HolderASO holder = new HolderASO();
        holder.setIsBankCustomer(true);
        holder.setIsDataTreatment(true);

        GoodASO good = new GoodASO();

        GoodDetailASO goodDetail = new GoodDetailASO();
        goodDetail.setInsuranceType(INSURANCE_TYPE_LIFE_VALUE);
        good.setGoodDetail(goodDetail);

        if(Objects.nonNull(responseListCustomers)) {
            CustomerBO customer = responseListCustomers.getData().get(0);
            holder.setFirstName(validateSN(customer.getFirstName()));
            holder.setLastName(validateSN(customer.getLastName()).concat(" ").concat(validateSN(customer.getSecondLastName())));

            List<ContactDetailsBO> contactDetails = responseListCustomers.getData().get(0).getContactDetails();

            List<ContactDetailASO> contactDetailASOS = new ArrayList<>();

            Optional<ContactDetailsBO> phoneContact = contactDetails.stream()
                    .filter(phone -> CONTACT_DETAIL_MOBILE_TYPE.equals(phone.getContactType().getId())).findFirst();

            ContactDetailASO phoneContactDetailASO = new ContactDetailASO();

            ContactASO phonecontactASO = new ContactASO();
            phonecontactASO.setPhoneNumber(phoneContact.map(ContactDetailsBO::getContact).orElse("No se encontro celular"));
            phonecontactASO.setContactType(CONTACT_DETAIL_MOBILE_TYPE_GIFOLE);
            phoneContactDetailASO.setContact(phonecontactASO);

            contactDetailASOS.add(phoneContactDetailASO);

            Optional<ContactDetailsBO> emailContact = contactDetails.stream()
                    .filter(email -> CONTACT_DETAIL_EMAIL_TYPE.equals(email.getContactType().getId())).findFirst();

            ContactDetailASO emailContactDetailASO = new ContactDetailASO();

            ContactASO emailcontactASO = new ContactASO();

            emailcontactASO.setAddress(emailContact.map(ContactDetailsBO::getContact).orElse("No se encontro correo"));
            emailcontactASO.setContactType(CONTACT_DETAIL_EMAIL_TYPE);
            emailContactDetailASO.setContact(emailcontactASO);

            contactDetailASOS.add(emailContactDetailASO);

            holder.setContactDetails(contactDetailASOS);

            docValidationForGifole(customer.getIdentityDocuments().get(0),holder,response);

            response.getHolder().setFirstName(holder.getFirstName());
            response.getHolder().setLastName(holder.getLastName());
            response.getHolder().setFullName(holder.getFirstName().concat(" ").concat(holder.getLastName()) );
        }

        BankASO bank = new BankASO();
        bank.setId(DEFAULT_BANK_ID);
        BranchASO branch = new BranchASO();
        branch.setId(DEFAULT_BRANCH_ID);
        bank.setBranch(branch);

        holder.setHasBankAccount(false);
        holder.setHasCreditCard(false);

        gifoleInsuranceRequest.setProduct(product);

        gifoleInsuranceRequest.setInstallmentPlan(installmentPlan);
        gifoleInsuranceRequest.setTotalPremiumAmount(totalPremiumAmount);
        gifoleInsuranceRequest.setHolder(holder);
        gifoleInsuranceRequest.setChannel(response.getAap());
        gifoleInsuranceRequest.setBank(bank);
        gifoleInsuranceRequest.setExternalSimulationId(response.getExternalSimulationId());

        DateTime currentDate = new DateTime(new Date(), DATE_TIME_ZONE);
        gifoleInsuranceRequest.setOperationDate(currentDate.toString(DATE_TIME_FORMATTER));

        gifoleInsuranceRequest.setOperationType(INSURANCE_SIMULATION_VALUE);
        gifoleInsuranceRequest.setGood(good);

        return gifoleInsuranceRequest;
    }

    private String validateSN(String name) {
        if(Objects.isNull(name) || "null".equals(name) || " ".equals(name)){
            return "N/A";
        }else{
            name = name.replace("#","Ñ");
            return name;
        }
    }

    private void docValidationForGifole(IdentityDocumentsBO customerInfo, HolderASO holder, LifeSimulationDTO response){
        IdentityDocumentASO identityDocument = new IdentityDocumentASO();
        DocumentTypeASO documentType = new DocumentTypeASO();
        String docNumber = customerInfo.getDocumentNumber();
        documentType.setId(customerInfo.getDocumentType().getId());
        identityDocument.setDocumentType(documentType);

        identityDocument.setDocumentNumber(response.getHolder().getIdentityDocument().getDocumentNumber());
        if (Objects.isNull(response.getHolder().getIdentityDocument().getDocumentNumber())) {
            identityDocument.setDocumentNumber(docNumber);
        } else {
            identityDocument.setDocumentNumber(
                    response.getHolder().getIdentityDocument().getDocumentNumber());
        }
        holder.setIdentityDocument(identityDocument);
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
