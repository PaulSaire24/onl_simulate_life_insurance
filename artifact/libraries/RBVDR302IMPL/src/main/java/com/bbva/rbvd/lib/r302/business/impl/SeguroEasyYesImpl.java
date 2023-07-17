package com.bbva.rbvd.lib.r302.business.impl;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.pisd.dto.insurance.aso.gifole.*;
import com.bbva.pisd.dto.insurance.bo.ContactDetailsBO;
import com.bbva.pisd.dto.insurance.bo.customer.CustomerBO;
import com.bbva.rbvd.dto.lifeinsrc.commons.InstallmentsDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.InsurancePlanDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.InsuranceProductDTO;
import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.InsuranceLifeSimulationBO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;
import com.bbva.rbvd.lib.r301.RBVDR301;
import com.bbva.rbvd.lib.r302.business.ISeguroEasyYes;
import com.bbva.rbvd.lib.r302.transform.objects.QuotationRimac;
import com.bbva.rbvd.lib.r302.util.ValidationUtil;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.math.BigDecimal;
import java.util.*;

public class SeguroEasyYesImpl implements ISeguroEasyYes {

    private static final String ENABLE_GIFOLE_LIFE_ASO = "ENABLE_GIFOLE_LIFE_ASO";
    private static final String ANNUAL_PERIOD_ID = "ANNUAL";
    private static final String INSURANCE_TYPE_LIFE_VALUE = "LIFE";
    private static final String CONTACT_DETAIL_MOBILE_TYPE = "MOBILE_NUMBER";
    private static final String CONTACT_DETAIL_MOBILE_TYPE_GIFOLE = "PHONE";
    private static final String CONTACT_DETAIL_EMAIL_TYPE = "EMAIL";
    private static final String DEFAULT_BANK_ID = "0011";
    private static final String DEFAULT_BRANCH_ID = "0814";
    private static final DateTimeZone DATE_TIME_ZONE = DateTimeZone.forID("America/Lima");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    private static final String INSURANCE_SIMULATION_VALUE = "INSURANCE_SIMULATION";


    private RBVDR301 rbvdR301;
    private ApplicationConfigurationService applicationConfigurationService;
    private ValidationUtil validationUtil = new ValidationUtil(rbvdR301);

    public InsuranceLifeSimulationBO executeQuotationRimacService(LifeSimulationDTO input, InsuranceLifeSimulationBO requestRimac){
        InsuranceLifeSimulationBO responseRimac = rbvdR301.executeSimulationRimacService(requestRimac,input.getTraceId());
        return responseRimac;
    }


    //Agrega el servicio Gifole
    public void serviceAddGifole(LifeSimulationDTO response, CustomerListASO responseListCustomers){

        //LOGGER.info("Param 1 - LifeSimulationDTO : {}", response);
        //LOGGER.info("Param 2 - CustomerListASO : {}", responseListCustomers);

        String flag = this.applicationConfigurationService.getProperty(ENABLE_GIFOLE_LIFE_ASO);

        //LOGGER.info("FLAG : {}", flag);

        if(flag.equals("true")){

            //LOGGER.info("***** RBVDR302Impl - executeGetSimulation | createGifoleInsuranceRequest invokation *****");

            //LOGGER.info("FLAG after -> {}", flag);

            GifoleInsuranceRequestASO gifoleInsuranceRequest = this.createGifoleASO(response, responseListCustomers);
            //LOGGER.info("**** RBVDR302Impl - GifoleInsuranceRequestASO gifoleInsuranceRequest: {}", gifoleInsuranceRequest);

            Integer httpStatusGifole = rbvdR301.executeGifolelifeService(gifoleInsuranceRequest);

            //LOGGER.info("***** RBVDR302Impl - executeGetSimulation ***** Gifole Response Status: {}", httpStatusGifole);
        }
    }

    private GifoleInsuranceRequestASO createGifoleASO(LifeSimulationDTO response, CustomerListASO responseListCustomers){

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
            holder.setFirstName(validationUtil.validateSN(customer.getFirstName()));
            holder.setLastName(validationUtil.validateSN(customer.getLastName()).concat(" ").concat(validationUtil.validateSN(customer.getSecondLastName())));

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

            validationUtil.docValidationForGifole(customer.getIdentityDocuments().get(0),holder,response);

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



    public void setApplicationConfigurationService(ApplicationConfigurationService applicationConfigurationService) {
        this.applicationConfigurationService = applicationConfigurationService;
    }

    public void setRbvdR301(RBVDR301 rbvdR301) {
        this.rbvdR301 = rbvdR301;
    }
}
