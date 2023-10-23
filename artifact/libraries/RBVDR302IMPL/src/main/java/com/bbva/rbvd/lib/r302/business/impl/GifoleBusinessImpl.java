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
import com.bbva.rbvd.dto.connectionapi.aso.common.BusinessAgentASO;
import com.bbva.rbvd.dto.connectionapi.aso.common.GenericAmountASO;
import com.bbva.rbvd.dto.connectionapi.aso.common.ProductModalityASO;
import com.bbva.rbvd.dto.lifeinsrc.commons.InstallmentsDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.InsurancePlanDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.InsuranceProductDTO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;
import com.bbva.rbvd.lib.r044.RBVDR044;
import com.bbva.rbvd.lib.r301.RBVDR301;
import com.bbva.rbvd.lib.r302.business.IGifoleBusiness;
import com.bbva.rbvd.lib.r302.util.ValidationUtil;
import com.bbva.rbvd.lib.r302.util.ConstantsUtil;
import org.joda.time.DateTime;
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



public class GifoleBusinessImpl implements IGifoleBusiness {

    private static final Logger LOGGER = LoggerFactory.getLogger(GifoleBusinessImpl.class);
    private static final String INSURANCE_TYPE_LIFE_VALUE = "LIFE";
    private static final String DEFAULT_BANK_ID = "0011";
    private static final String DEFAULT_BRANCH_ID = "0814";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private static final String INSURANCE_SIMULATION_VALUE = "INSURANCE_SIMULATION";
    private static final String EMPTY_VALUE = "";

    private ApplicationConfigurationService applicationConfigurationService;
    private RBVDR301 rbvdR301;

    public GifoleBusinessImpl(RBVDR301 rbvdR301, ApplicationConfigurationService applicationConfigurationService) {
        this.applicationConfigurationService = applicationConfigurationService;
        this.rbvdR301 = rbvdR301;
    }

    //Agrega el servicio Gifole
    @Override
    public void serviceAddGifole(LifeSimulationDTO response, CustomerListASO responseListCustomers){

        String flag = this.applicationConfigurationService.getProperty(ConstantsUtil.Flag.ENABLE_GIFOLE_LIFE_ASO);


        if(flag.equals(ConstantsUtil.Condition.TRUE)){

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

        Optional<InstallmentsDTO> planObj = installmentPlanDto.stream().filter(p -> ConstantsUtil.Period.ANNUAL.getId().equals(p.getPeriod().getId())).findFirst();
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
                    .filter(phone -> ConstantsUtil.ContactDetails.MOBILE_NUMBER.equals(phone.getContactType().getId())).findFirst();

            ContactDetailASO phoneContactDetailASO = new ContactDetailASO();

            ContactASO phonecontactASO = new ContactASO();
            phonecontactASO.setPhoneNumber(phoneContact.map(ContactDetailsBO::getContact).orElse(ConstantsUtil.ContactDetails.NOT_FOUND_PHOME));
            phonecontactASO.setContactType(ConstantsUtil.ContactDetails.PHONE);
            phoneContactDetailASO.setContact(phonecontactASO);

            contactDetailASOS.add(phoneContactDetailASO);

            Optional<ContactDetailsBO> emailContact = contactDetails.stream()
                    .filter(email -> ConstantsUtil.ContactDetails.EMAIL.equals(email.getContactType().getId())).findFirst();

            ContactDetailASO emailContactDetailASO = new ContactDetailASO();

            ContactASO emailcontactASO = new ContactASO();

            emailcontactASO.setAddress(emailContact.map(ContactDetailsBO::getContact).orElse(ConstantsUtil.ContactDetails.NOT_FOUND_EMAIL));
            emailcontactASO.setContactType(ConstantsUtil.ContactDetails.EMAIL);
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

        DateTime currentDate = new DateTime(new Date(), ConstantsUtil.Zone.DATE_TIME_ZONE_LIMA);
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

        String flag = this.applicationConfigurationService.getProperty(ConstantsUtil.Flag.FLAG_GIFOLE_LIB_LIFE);

        if(flag.equals(ConstantsUtil.Condition.TRUE)){

            LOGGER.info("***** GifoleBusinessImpl - callGifoleDynamicService ***** --- FLAG after -> {}", flag);

            com.bbva.rbvd.dto.connectionapi.aso.gifole.GifoleInsuranceRequestASO gifoleInsuranceRequest =
                    this.createGifoleAsoDynamic(inputLife, inputListCustomers);
            LOGGER.info("***** GifoleBusinessImpl - callGifoleDynamicService: {}", gifoleInsuranceRequest);

            Integer httpStatusGifole = rbvdr044.executeGifoleRegistration(gifoleInsuranceRequest);

            LOGGER.info("***** GifoleBusinessImpl ***** Gifole Response Status: {}", httpStatusGifole);
        }
    }



    public com.bbva.rbvd.dto.connectionapi.aso.gifole.GifoleInsuranceRequestASO createGifoleAsoDynamic(LifeSimulationDTO response, CustomerListASO responseListCustomers) {

        com.bbva.rbvd.dto.connectionapi.aso.gifole.GifoleInsuranceRequestASO gifoleInsuranceRequest =
                new com.bbva.rbvd.dto.connectionapi.aso.gifole.GifoleInsuranceRequestASO();


        com.bbva.rbvd.dto.connectionapi.aso.common.BankASO bank =
                new com.bbva.rbvd.dto.connectionapi.aso.common.BankASO();
        bank.setId(DEFAULT_BANK_ID);

        com.bbva.rbvd.dto.connectionapi.aso.common.BranchASO branch =
                new com.bbva.rbvd.dto.connectionapi.aso.common.BranchASO();
        branch.setId(DEFAULT_BRANCH_ID);
        bank.setBranch(branch);


        BusinessAgentASO businessAgent = new BusinessAgentASO();
        businessAgent.setId(response.getCreationUser());


        InsuranceProductDTO productDto = response.getProduct();
        InsurancePlanDTO planDTO = productDto.getPlans().get(0);
        List<InstallmentsDTO> installmentPlanDto = planDTO.getInstallmentPlans();

        com.bbva.rbvd.dto.connectionapi.aso.common.ProductASO product =
                new com.bbva.rbvd.dto.connectionapi.aso.common.ProductASO();
        product.setId(productDto.getId());
        product.setName(productDto.getName());

        ProductModalityASO plan = new ProductModalityASO();
        plan.setId(planDTO.getId());
        plan.setName(planDTO.getName());

        product.setPlan(plan);


        com.bbva.rbvd.dto.connectionapi.aso.common.InstallmentPlanASO installmentPlan =
                new com.bbva.rbvd.dto.connectionapi.aso.common.InstallmentPlanASO();

        GenericAmountASO premiumAmount = new GenericAmountASO();
        if (!installmentPlanDto.isEmpty() && installmentPlanDto.get(0).getPaymentAmount() != null) {

            premiumAmount.setAmount(installmentPlanDto.get(0).getPaymentAmount().getAmount());
            premiumAmount.setCurrency(installmentPlanDto.get(0).getPaymentAmount().getCurrency());

        } else {
            premiumAmount.setAmount(new BigDecimal(1000));
            premiumAmount.setCurrency(ConstantsUtil.Currency.PEN);
        }
        installmentPlan.setPremiumAmount(premiumAmount);
        installmentPlan.setTotalInstallmentsNumber(installmentPlanDto.get(0).getPaymentsTotalNumber().intValue());

        com.bbva.rbvd.dto.connectionapi.aso.common.PeriodASO period =
                new com.bbva.rbvd.dto.connectionapi.aso.common.PeriodASO();
        period.setId(installmentPlanDto.get(0).getPeriod().getId());
        period.setName(installmentPlanDto.get(0).getPeriod().getName());
        installmentPlan.setPeriod(period);


        GenericAmountASO totalPremiumAmount = new GenericAmountASO();
        totalPremiumAmount.setAmount(planDTO.getTotalInstallment().getAmount());
        totalPremiumAmount.setCurrency(planDTO.getTotalInstallment().getCurrency());

        ValidationUtil validationUtil = new ValidationUtil();

        com.bbva.rbvd.dto.connectionapi.aso.common.HolderASO holder =
                new com.bbva.rbvd.dto.connectionapi.aso.common.HolderASO();

        holder.setFirstName(EMPTY_VALUE);
        holder.setLastName(EMPTY_VALUE);
        holder.setContactDetails(new ArrayList<>());
        holder.getContactDetails().add(getContactDetail(ConstantsUtil.ContactDetails.NOT_FOUND_EMAIL, ConstantsUtil.ContactDetails.EMAIL));
        holder.getContactDetails().add(getContactDetail(ConstantsUtil.ContactDetails.NOT_FOUND_PHOME, ConstantsUtil.ContactDetails.PHONE));
        validationUtil.docValidationForGifoleDynamic(EMPTY_VALUE, EMPTY_VALUE, holder, response);
        holder.setHasBankAccount(false);
        holder.setHasCreditCard(false);
        holder.setIsDataTreatment(false);

        com.bbva.rbvd.dto.connectionapi.aso.common.GoodASO good = new com.bbva.rbvd.dto.connectionapi.aso.common.GoodASO();

        com.bbva.rbvd.dto.connectionapi.aso.common.GoodDetailASO goodDetail = new com.bbva.rbvd.dto.connectionapi.aso.common.GoodDetailASO();
        goodDetail.setInsuranceType(INSURANCE_TYPE_LIFE_VALUE);
        good.setGoodDetail(goodDetail);

        if (Objects.nonNull(response.getHolder())) {
            holder.setFirstName(response.getHolder().getFirstName());
            holder.setLastName(response.getHolder().getLastName());
            holder.setIsBankCustomer(true);
        }

        if(!CollectionUtils.isEmpty(response.getParticipants())){
            holder.setIsBankCustomer(ValidationUtil.isBBVAClient(response.getParticipants().get(0).getId()));
        }

        if(Objects.nonNull(response.getIsDataTreatment())){
            holder.setIsDataTreatment(response.getIsDataTreatment());
        }

        if (Objects.nonNull(responseListCustomers)) {

            CustomerBO customer = responseListCustomers.getData().get(0);
            holder.setFirstName(validationUtil.validateSN(customer.getFirstName()));
            holder.setLastName(validationUtil.validateSN(customer.getLastName().concat(" ").concat(validationUtil.validateSN(customer.getSecondLastName()))));

            List<ContactDetailsBO> contactDetails = responseListCustomers.getData().get(0).getContactDetails();
            List<com.bbva.rbvd.dto.connectionapi.aso.common.ContactDetailASO> contactDetailASOS = new ArrayList<>();

            Optional<ContactDetailsBO> phoneContact = contactDetails.stream()
                    .filter(phone -> ConstantsUtil.ContactDetails.MOBILE_NUMBER.equals(phone.getContactType().getId())).findFirst();

            Optional<ContactDetailsBO> emailContact = contactDetails.stream()
                    .filter(email -> ConstantsUtil.ContactDetails.EMAIL.equals(email.getContactType().getId())).findFirst();

            contactDetailASOS.add(getContactDetail(emailContact.map(ContactDetailsBO::getContact).orElse(ConstantsUtil.ContactDetails.NOT_FOUND_EMAIL), ConstantsUtil.ContactDetails.EMAIL));
            contactDetailASOS.add(getContactDetail(phoneContact.map(ContactDetailsBO::getContact).orElse(ConstantsUtil.ContactDetails.NOT_FOUND_PHOME), ConstantsUtil.ContactDetails.PHONE));

            holder.setContactDetails(contactDetailASOS);

            IdentityDocumentsBO documentsBO = customer.getIdentityDocuments().get(0);
            validationUtil.docValidationForGifoleDynamic(documentsBO.getDocumentNumber(), documentsBO.getDocumentType().getId(), holder, response);

        }

        gifoleInsuranceRequest.setBank(bank);
        gifoleInsuranceRequest.setBusinessAgent(businessAgent);
        gifoleInsuranceRequest.setExternalSimulationid(response.getExternalSimulationId());
        gifoleInsuranceRequest.setProduct(product);
        gifoleInsuranceRequest.setInstallmentPlan(installmentPlan);
        gifoleInsuranceRequest.setTotalPremiumAmount(totalPremiumAmount);
        gifoleInsuranceRequest.setHolder(holder);
        gifoleInsuranceRequest.setOperationType(INSURANCE_SIMULATION_VALUE);
        gifoleInsuranceRequest.setChannel(response.getAap());

        DateTime currentDate = new DateTime(new Date(), ConstantsUtil.Zone.DATE_TIME_ZONE_LIMA);
        gifoleInsuranceRequest.setOperationDate(currentDate.toString(DATE_TIME_FORMATTER));
        gifoleInsuranceRequest.setGood(good);

        return gifoleInsuranceRequest;
    }

    public com.bbva.rbvd.dto.connectionapi.aso.common.ContactDetailASO getContactDetail(String contactValue, String contactType) {
        com.bbva.rbvd.dto.connectionapi.aso.common.ContactDetailASO contactDetailASO = new com.bbva.rbvd.dto.connectionapi.aso.common.ContactDetailASO();
        com.bbva.rbvd.dto.connectionapi.aso.common.ContactASO contactASO = new com.bbva.rbvd.dto.connectionapi.aso.common.ContactASO();
        contactASO.setContactType(contactType);
        switch (contactType){
            case ConstantsUtil.ContactDetails.EMAIL:
                contactASO.setAddress(contactValue);
                break;
            case ConstantsUtil.ContactDetails.PHONE:
                contactASO.setPhoneNumber(contactValue);
                break;
            default:
                break;
        }
        contactDetailASO.setContact(contactASO);
        return contactDetailASO;
    }

}
