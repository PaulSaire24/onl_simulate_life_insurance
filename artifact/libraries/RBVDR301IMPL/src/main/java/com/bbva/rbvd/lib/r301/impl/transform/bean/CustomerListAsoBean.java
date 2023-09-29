package com.bbva.rbvd.lib.r301.impl.transform.bean;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.pbtq.dto.validatedocument.response.host.pewu.PEWUResponse;
import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.bbva.rbvd.dto.lifeinsrc.utils.RBVDConstants.Customer.FEMALE;
import static com.bbva.rbvd.dto.lifeinsrc.utils.RBVDConstants.Customer.MALE;
import static com.bbva.rbvd.dto.lifeinsrc.utils.RBVDConstants.Customer.MOBILE_NUMBER;
import static com.bbva.rbvd.dto.lifeinsrc.utils.RBVDConstants.Customer.EMAIL;
import static com.bbva.rbvd.dto.lifeinsrc.utils.RBVDConstants.Customer.PHONE_NUMBER;

public class CustomerListAsoBean {

    private ApplicationConfigurationService applicationConfigurationService;

    public CustomerListAsoBean(ApplicationConfigurationService applicationConfigurationService) {
        this.applicationConfigurationService = applicationConfigurationService;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerListAsoBean.class);

    public  CustomerListASO mapperCustomerListAso(PEWUResponse result){

            CustomerListASO customerList = new CustomerListASO();
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
            customer.getGender().setId(result.getPemsalwu().getSexo().equals("M") ? MALE : FEMALE);

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
                contactDetailPhone.getContactType().setId(PHONE_NUMBER);
                contactDetailPhone.getContactType().setName(result.getPemsalw5().getDescmco());
                contactDetailsBOList.add(contactDetailPhone);
            }

            /* section contact2 type, validate MOBILE_NUMBER */
            LOGGER.info("***** PISDR008Impl - executeGetCustomerHost  ***** Map getTipoco2: {}", result.getPemsalwu().getTipoco2());
            if (StringUtils.isNotEmpty(result.getPemsalwu().getContac2())) {
                ContactDetailsBO contactDetailMobileNumber = new ContactDetailsBO();
                contactDetailMobileNumber.setContactDetailId(result.getPemsalwu().getIdenco2());
                contactDetailMobileNumber.setContact(result.getPemsalwu().getContac2());
                contactDetailMobileNumber.setContactType(new ContactTypeBO());
                contactDetailMobileNumber.getContactType().setId(MOBILE_NUMBER);
                contactDetailMobileNumber.getContactType().setName(result.getPemsalw5().getDescmc1());
                contactDetailsBOList.add(contactDetailMobileNumber);
            }

            /* section contact2 type, validate EMAIL */
            LOGGER.info("***** PISDR008Impl - executeGetCustomerHost  ***** Map getTipoco3: {}", result.getPemsalwu().getTipoco3());
            if (StringUtils.isNotEmpty(result.getPemsalwu().getContac3())) {
                ContactDetailsBO contactDetailEmail = new ContactDetailsBO();
                contactDetailEmail.setContactDetailId(result.getPemsalwu().getIdenco3());
                contactDetailEmail.setContact(result.getPemsalwu().getContac3());
                contactDetailEmail.setContactType(new ContactTypeBO());
                contactDetailEmail.getContactType().setId(EMAIL);
                contactDetailEmail.getContactType().setName(result.getPemsalw5().getDescmc2());
                contactDetailsBOList.add(contactDetailEmail);
            }

            customer.setContactDetails(contactDetailsBOList);
            /* section contact Details */

            customerList.setData(Collections.singletonList(customer));
            LOGGER.info("***** CustomerListAsoBean - executeGetListCustomer End ***** ListCustomer: {}", customerList);
            return customerList;
    }
}
