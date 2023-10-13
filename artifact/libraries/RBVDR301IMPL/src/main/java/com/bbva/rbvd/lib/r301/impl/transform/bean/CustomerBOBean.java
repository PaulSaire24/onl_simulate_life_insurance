package com.bbva.rbvd.lib.r301.impl.transform.bean;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.pbtq.dto.validatedocument.response.host.pewu.PEWUResponse;
import com.bbva.pisd.dto.insurance.bo.BirthDataBO;
import com.bbva.pisd.dto.insurance.bo.CountryBO;
import com.bbva.pisd.dto.insurance.bo.ContactDetailsBO;
import com.bbva.pisd.dto.insurance.bo.IdentityDocumentsBO;
import com.bbva.pisd.dto.insurance.bo.ContactTypeBO;
import com.bbva.pisd.dto.insurance.bo.GenderBO;
import com.bbva.pisd.dto.insurance.bo.DocumentTypeBO;
import com.bbva.pisd.dto.insurance.bo.customer.CustomerBO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.bbva.rbvd.lib.r301.impl.util.Constans.CustomerContact;
import com.bbva.rbvd.lib.r301.impl.util.Constans.Gender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CustomerBOBean {

    private ApplicationConfigurationService applicationConfigurationService;

    public CustomerBOBean(ApplicationConfigurationService applicationConfigurationService) {
        this.applicationConfigurationService = applicationConfigurationService;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerBOBean.class);

    public  CustomerBO mapperCustomer(PEWUResponse result){

            /* section customer data */
            CustomerBO customer = new CustomerBO();
            customer.setCustomerId(result.getPemsalwu().getNroclie());
            customer.setFirstName(result.getPemsalwu().getNombres());
            customer.setLastName(result.getPemsalwu().getApellip());
            customer.setSecondLastName(result.getPemsalwu().getApellim());
            customer.setBirthData(new BirthDataBO());
            customer.getBirthData().setBirthDate(result.getPemsalwu().getFechan());
            customer.getBirthData().setCountry(new CountryBO());
            customer.getBirthData().getCountry().setId(result.getPemsalwu().getPaisn());
            customer.setGender(new GenderBO());
            customer.getGender().setId(result.getPemsalwu().getSexo().equals("M") ? Gender.MALE : Gender.FEMALE);

            /* section identity document*/
            IdentityDocumentsBO identityDocumentsBO = new IdentityDocumentsBO();
            identityDocumentsBO.setDocumentNumber(result.getPemsalwu().getNdoi());
            identityDocumentsBO.setDocumentType(new DocumentTypeBO());

            /* map document type host ? yes*/
            identityDocumentsBO.getDocumentType().setId(this.applicationConfigurationService.getProperty(result.getPemsalwu().getTdoi()));

            identityDocumentsBO.setExpirationDate(result.getPemsalwu().getFechav());
            customer.setIdentityDocuments(Collections.singletonList(identityDocumentsBO));

            /* section contact Details */
            List<ContactDetailsBO> contactDetailsBOList = new ArrayList<>();

            /* section contact PHONE_NUMBER */
            LOGGER.info("***** PISDR008Impl - executeGetCustomerHost  ***** Map getTipocon: {}", result.getPemsalwu().getTipocon());
            if (StringUtils.isNotEmpty(result.getPemsalwu().getContact())) {
                ContactDetailsBO contactDetailPhone = new ContactDetailsBO();
                contactDetailPhone.setContactDetailId(result.getPemsalwu().getIdencon());
                contactDetailPhone.setContact(result.getPemsalwu().getContact());
                contactDetailPhone.setContactType(new ContactTypeBO());
                contactDetailPhone.getContactType().setId(CustomerContact.PHONE_NUMBER);
                contactDetailPhone.getContactType().setName(result.getPemsalw5().getDescmco());
                contactDetailsBOList.add(contactDetailPhone);
            }

            /* section contact2 type, validate MOBILE_NUMBER */
            LOGGER.info("***** PISDR008Impl - executeGetCustomerHost  ***** Map getTipoco2: {}", result.getPemsalwu().getTipoco2());
            LOGGER.info("***** PISDR008Impl - executeGetCustomerHost  ***** Map getContac2: {}", result.getPemsalwu().getContac2());
            if (StringUtils.isNotEmpty(result.getPemsalwu().getContac2())) {
                ContactDetailsBO contactDetailMobileNumber = new ContactDetailsBO();
                contactDetailMobileNumber.setContactDetailId(result.getPemsalwu().getIdenco2());
                contactDetailMobileNumber.setContact(result.getPemsalwu().getContac2());
                contactDetailMobileNumber.setContactType(new ContactTypeBO());
                contactDetailMobileNumber.getContactType().setId(CustomerContact.MOBILE_NUMBER);
                contactDetailMobileNumber.getContactType().setName(result.getPemsalw5().getDescmc1());
                contactDetailsBOList.add(contactDetailMobileNumber);
            }

            /* section contact2 type, validate EMAIL */
            LOGGER.info("***** PISDR008Impl - executeGetCustomerHost  ***** Map getTipoco3: {}", result.getPemsalwu().getTipoco3());
            LOGGER.info("***** PISDR008Impl - executeGetCustomerHost  ***** Map getContac2: {}", result.getPemsalwu().getContac3());
            if (StringUtils.isNotEmpty(result.getPemsalwu().getContac3())) {
                ContactDetailsBO contactDetailEmail = new ContactDetailsBO();
                contactDetailEmail.setContactDetailId(result.getPemsalwu().getIdenco3());
                contactDetailEmail.setContact(result.getPemsalwu().getContac3());
                contactDetailEmail.setContactType(new ContactTypeBO());
                contactDetailEmail.getContactType().setId(CustomerContact.EMAIL);
                contactDetailEmail.getContactType().setName(result.getPemsalw5().getDescmc2());
                contactDetailsBOList.add(contactDetailEmail);
            }

            customer.setContactDetails(contactDetailsBOList);
            /* section contact Details */

            LOGGER.info("***** CustomerListAsoBean - executeGetListCustomer End ***** customerBO: {}", customer);
            return customer;
    }
}
