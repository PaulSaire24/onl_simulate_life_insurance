package com.bbva.rbvd.lib.r302.business.impl;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.pisd.dto.insurance.aso.gifole.GifoleInsuranceRequestASO;
import com.bbva.pisd.dto.insurance.aso.gifole.InstallmentPlanASO;
import com.bbva.pisd.dto.insurance.aso.gifole.PlanASO;
import com.bbva.pisd.dto.insurance.aso.gifole.ProductASO;
import com.bbva.pisd.dto.insurance.aso.gifole.AmountASO;
import com.bbva.pisd.dto.insurance.aso.gifole.PeriodASO;
import com.bbva.pisd.dto.insurance.aso.gifole.HolderASO;
import com.bbva.pisd.dto.insurance.aso.gifole.GoodASO;
import com.bbva.pisd.dto.insurance.aso.gifole.GoodDetailASO;
import com.bbva.pisd.dto.insurance.aso.gifole.ContactDetailASO;
import com.bbva.pisd.dto.insurance.aso.gifole.ContactASO;
import com.bbva.pisd.dto.insurance.aso.gifole.BankASO;
import com.bbva.pisd.dto.insurance.aso.gifole.BranchASO;
import com.bbva.pisd.dto.insurance.bo.ContactDetailsBO;
import com.bbva.pisd.dto.insurance.bo.IdentityDocumentsBO;
import com.bbva.pisd.dto.insurance.bo.customer.CustomerBO;
import com.bbva.pisd.dto.insurance.aso.gifole.BusinessAgentASO;
import com.bbva.rbvd.dto.lifeinsrc.commons.InstallmentsDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.InsurancePlanDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.InsuranceProductDTO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;
import com.bbva.rbvd.lib.r044.RBVDR044;
import com.bbva.rbvd.lib.r301.RBVDR301;
import com.bbva.rbvd.lib.r302.business.IGifoleBusiness;
import com.bbva.rbvd.lib.r302.util.ValidationUtil;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Date;

import static com.bbva.rbvd.lib.r302.util.ConstantsUtil.FLAG_GIFOLE_LIB_LIFE;
import static com.bbva.rbvd.lib.r302.util.ValidationUtil.isBBVAClient;

public class GifoleBusinessImpl implements IGifoleBusiness {

    private static final Logger LOGGER = LoggerFactory.getLogger(GifoleBusinessImpl.class);

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

    private static final String EMPTY_VALUE = "";
    private static final String NOT_FOUND_EMAIL= "No se encontro correo";
    private static final String NOT_FOUND_PHOME= "No celular";


    private ApplicationConfigurationService applicationConfigurationService;
    private RBVDR301 rbvdR301;

    public GifoleBusinessImpl(RBVDR301 rbvdR301, ApplicationConfigurationService applicationConfigurationService) {
        this.applicationConfigurationService = applicationConfigurationService;
        this.rbvdR301 = rbvdR301;
    }

    //Agrega el servicio Gifole
    @Override
    public void serviceAddGifole(LifeSimulationDTO response, CustomerListASO responseListCustomers){

        String flag = this.applicationConfigurationService.getProperty(ENABLE_GIFOLE_LIFE_ASO);


        if(flag.equals("true")){

            LOGGER.info("***** GifoleBusinessImpl - serviceAddGifole ***** --- FLAG after -> {}", flag);

            GifoleInsuranceRequestASO gifoleInsuranceRequest = this.createGifoleASO(response, responseListCustomers);
            LOGGER.info("**** GifoleBusinessImpl - gifoleInsuranceRequest: {}", gifoleInsuranceRequest);

            Integer httpStatusGifole = rbvdR301.executeGifolelifeService(gifoleInsuranceRequest);

            LOGGER.info("***** GifoleBusinessImpl ***** Gifole Response Status: {}", httpStatusGifole);
        }
    }

    public GifoleInsuranceRequestASO createGifoleASO(LifeSimulationDTO response, CustomerListASO responseListCustomers){
        LOGGER.info("***** GifoleBusinessImpl ***** - createGifoleASO START");

        InsuranceProductDTO productDto = response.getProduct();
        InsurancePlanDTO planDTO = productDto.getPlans().stream().filter(InsurancePlanDTO::getIsRecommended).findFirst().orElse(new InsurancePlanDTO());
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
            ValidationUtil validationUtil = new ValidationUtil();

            CustomerBO customer = responseListCustomers.getData().get(0);
            holder.setFirstName(validationUtil.validateSN(customer.getFirstName()));
            holder.setLastName(validationUtil.validateSN(customer.getLastName()).concat(" ").concat(validationUtil.validateSN(customer.getSecondLastName())));

            List<ContactDetailsBO> contactDetails = responseListCustomers.getData().get(0).getContactDetails();

            List<ContactDetailASO> contactDetailASOS = new ArrayList<>();

            Optional<ContactDetailsBO> phoneContact = contactDetails.stream()
                    .filter(phone -> CONTACT_DETAIL_MOBILE_TYPE.equals(phone.getContactType().getId())).findFirst();

            ContactDetailASO phoneContactDetailASO = new ContactDetailASO();

            ContactASO phonecontactASO = new ContactASO();
            phonecontactASO.setPhoneNumber(phoneContact.map(ContactDetailsBO::getContact).orElse(NOT_FOUND_PHOME));
            phonecontactASO.setContactType(CONTACT_DETAIL_MOBILE_TYPE_GIFOLE);
            phoneContactDetailASO.setContact(phonecontactASO);

            contactDetailASOS.add(phoneContactDetailASO);

            Optional<ContactDetailsBO> emailContact = contactDetails.stream()
                    .filter(email -> CONTACT_DETAIL_EMAIL_TYPE.equals(email.getContactType().getId())).findFirst();

            ContactDetailASO emailContactDetailASO = new ContactDetailASO();

            ContactASO emailcontactASO = new ContactASO();

            emailcontactASO.setAddress(emailContact.map(ContactDetailsBO::getContact).orElse(NOT_FOUND_EMAIL));
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

        LOGGER.info("***** GifoleBusinessImpl ***** - createGifoleASO END - gifoleInsuranceRequest {}",gifoleInsuranceRequest);

        return gifoleInsuranceRequest;
    }

    /*-----------------------------------------------------------------*/


    @Override
    public void callGifoleDynamicService(LifeSimulationDTO inputLife, CustomerListASO inputListCustomers, RBVDR044 rbvdr044) {

        LOGGER.info("***** GifoleBusinessImpl - callGifoleDynamicService START *****");

        String flag = this.applicationConfigurationService.getProperty(FLAG_GIFOLE_LIB_LIFE);

        if(flag.equals("true")){

            LOGGER.info("***** GifoleBusinessImpl - callGifoleDynamicService ***** --- FLAG after -> {}", flag);

            GifoleInsuranceRequestASO gifoleInsuranceRequest =
                    this.createGifoleAsoDynamic(inputLife, inputListCustomers);
            LOGGER.info("***** GifoleBusinessImpl - callGifoleDynamicService: {}", gifoleInsuranceRequest);

            Integer httpStatusGifole = rbvdr044.executeGifoleRegistration(gifoleInsuranceRequest);

            LOGGER.info("***** GifoleBusinessImpl ***** Gifole Response Status: {}", httpStatusGifole);
        }
    }



    public GifoleInsuranceRequestASO createGifoleAsoDynamic(LifeSimulationDTO response, CustomerListASO responseListCustomers) {

        GifoleInsuranceRequestASO gifoleInsuranceRequest =
                new GifoleInsuranceRequestASO();


        BankASO bank = new BankASO();
        bank.setId(DEFAULT_BANK_ID);

        BranchASO branch = new BranchASO();
        branch.setId(DEFAULT_BRANCH_ID);
        bank.setBranch(branch);


        BusinessAgentASO businessAgent = new BusinessAgentASO();
        businessAgent.setId(response.getCreationUser());


        InsuranceProductDTO productDto = response.getProduct();
        InsurancePlanDTO planDTO = productDto.getPlans().get(0);
        List<InstallmentsDTO> installmentPlanDto = planDTO.getInstallmentPlans();

        ProductASO product = new ProductASO();
        product.setId(productDto.getId());
        product.setName(productDto.getName());

        PlanASO plan = new PlanASO();
        plan.setId(planDTO.getId());
        plan.setName(planDTO.getName());

        product.setPlan(plan);


        InstallmentPlanASO installmentPlan = new InstallmentPlanASO();

        AmountASO premiumAmount = new AmountASO();
        if (!installmentPlanDto.isEmpty() && installmentPlanDto.get(0).getPaymentAmount() != null) {

            premiumAmount.setAmount(installmentPlanDto.get(0).getPaymentAmount().getAmount());
            premiumAmount.setCurrency(installmentPlanDto.get(0).getPaymentAmount().getCurrency());

        } else {
            premiumAmount.setAmount(new BigDecimal(1000));
            premiumAmount.setCurrency("PEN");
        }
        installmentPlan.setPremiumAmount(premiumAmount);
        installmentPlan.setTotalInstallmentsNumber((long) installmentPlanDto.get(0).getPaymentsTotalNumber().intValue());

        PeriodASO period = new PeriodASO();
        period.setId(installmentPlanDto.get(0).getPeriod().getId());
        period.setName(installmentPlanDto.get(0).getPeriod().getName());
        installmentPlan.setPeriod(period);


        AmountASO totalPremiumAmount = new AmountASO();
        totalPremiumAmount.setAmount(planDTO.getTotalInstallment().getAmount());
        totalPremiumAmount.setCurrency(planDTO.getTotalInstallment().getCurrency());

        ValidationUtil validationUtil = new ValidationUtil();

        HolderASO holder = new HolderASO();

        holder.setFirstName(EMPTY_VALUE);
        holder.setLastName(EMPTY_VALUE);
        holder.setContactDetails(new ArrayList<>());
        holder.getContactDetails().add(getContactDetail(NOT_FOUND_EMAIL, CONTACT_DETAIL_EMAIL_TYPE));
        holder.getContactDetails().add(getContactDetail(NOT_FOUND_PHOME, CONTACT_DETAIL_MOBILE_TYPE_GIFOLE));
        validationUtil.docValidationForGifoleDynamic(EMPTY_VALUE, EMPTY_VALUE, holder, response);
        holder.setHasBankAccount(false);
        holder.setHasCreditCard(false);
        holder.setIsDataTreatment(false);

        GoodASO good = new GoodASO();

        GoodDetailASO goodDetail = new GoodDetailASO();
        goodDetail.setInsuranceType(INSURANCE_TYPE_LIFE_VALUE);
        good.setGoodDetail(goodDetail);

        if (Objects.nonNull(response.getHolder())) {
            holder.setFirstName(response.getHolder().getFirstName());
            holder.setLastName(response.getHolder().getLastName());
            holder.setIsBankCustomer(true);
        }

        if(!CollectionUtils.isEmpty(response.getParticipants())){
            holder.setIsBankCustomer(isBBVAClient(response.getParticipants().get(0).getId()));
        }


        if(Objects.nonNull(response.getIsDataTreatment())){
            holder.setIsDataTreatment(response.getIsDataTreatment());
        }

        if (Objects.nonNull(responseListCustomers)) {

            CustomerBO customer = responseListCustomers.getData().get(0);
            holder.setFirstName(validationUtil.validateSN(customer.getFirstName()));
            holder.setLastName(validationUtil.validateSN(customer.getLastName().concat(" ").concat(validationUtil.validateSN(customer.getSecondLastName()))));

            List<ContactDetailsBO> contactDetails = responseListCustomers.getData().get(0).getContactDetails();
            List<ContactDetailASO> contactDetailASOS = new ArrayList<>();

            Optional<ContactDetailsBO> phoneContact = contactDetails.stream()
                    .filter(phone -> CONTACT_DETAIL_MOBILE_TYPE.equals(phone.getContactType().getId())).findFirst();

            Optional<ContactDetailsBO> emailContact = contactDetails.stream()
                    .filter(email -> CONTACT_DETAIL_EMAIL_TYPE.equals(email.getContactType().getId())).findFirst();

            contactDetailASOS.add(getContactDetail(emailContact.map(ContactDetailsBO::getContact).orElse(NOT_FOUND_EMAIL), CONTACT_DETAIL_EMAIL_TYPE));
            contactDetailASOS.add(getContactDetail(phoneContact.map(ContactDetailsBO::getContact).orElse(NOT_FOUND_PHOME), CONTACT_DETAIL_MOBILE_TYPE_GIFOLE));

            holder.setContactDetails(contactDetailASOS);

            IdentityDocumentsBO documentsBO = customer.getIdentityDocuments().get(0);
            validationUtil.docValidationForGifoleDynamic(documentsBO.getDocumentNumber(), documentsBO.getDocumentType().getId(), holder, response);

        }

        gifoleInsuranceRequest.setBank(bank);
        gifoleInsuranceRequest.setBusinessAgent(businessAgent);
        gifoleInsuranceRequest.setExternalSimulationId(response.getExternalSimulationId());
        gifoleInsuranceRequest.setProduct(product);
        gifoleInsuranceRequest.setInstallmentPlan(installmentPlan);
        gifoleInsuranceRequest.setTotalPremiumAmount(totalPremiumAmount);
        gifoleInsuranceRequest.setHolder(holder);
        gifoleInsuranceRequest.setOperationType(INSURANCE_SIMULATION_VALUE);
        gifoleInsuranceRequest.setChannel(response.getAap());

        DateTime currentDate = new DateTime(new Date(), DATE_TIME_ZONE);
        gifoleInsuranceRequest.setOperationDate(currentDate.toString(DATE_TIME_FORMATTER));
        gifoleInsuranceRequest.setGood(good);

        return gifoleInsuranceRequest;
    }

    public ContactDetailASO getContactDetail(String contactValue, String contactType) {
        ContactDetailASO contactDetailASO = new ContactDetailASO();
        ContactASO contactASO = new ContactASO();
        contactASO.setContactType(contactType);
        switch (contactType){
            case CONTACT_DETAIL_EMAIL_TYPE:
                contactASO.setAddress(contactValue);
                break;
            case CONTACT_DETAIL_MOBILE_TYPE_GIFOLE:
                contactASO.setPhoneNumber(contactValue);
                break;
            default:
                break;
        }
        contactDetailASO.setContact(contactASO);
        return contactDetailASO;
    }

}
