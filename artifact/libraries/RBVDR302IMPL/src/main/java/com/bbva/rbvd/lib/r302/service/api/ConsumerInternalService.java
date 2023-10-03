package com.bbva.rbvd.lib.r302.service.api;

import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.pisd.dto.insurance.aso.crypto.CryptoASO;
import com.bbva.pisd.dto.insurance.aso.tier.TierASO;
import com.bbva.pisd.dto.insurance.bo.customer.CustomerBO;
import com.bbva.rbvd.lib.r301.RBVDR301;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

public class ConsumerInternalService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerInternalService.class);
    private final RBVDR301 rbvdR301;

    public ConsumerInternalService(RBVDR301 rbvdR301) {
        this.rbvdR301 = rbvdR301;
    }


    public String callCryptoService(String customerID) {
        return this.rbvdR301.executeGetCustomerIdEncrypted(new CryptoASO(customerID));
    }

    public TierASO callGetTierService(String document) {
        return this.rbvdR301.executeGetTierService(document);
    }


    public CustomerListASO callListCustomerResponse(String customerId){
        CustomerBO customer = this.rbvdR301.executeGetCustomer(customerId);
        CustomerListASO customerList = new CustomerListASO();
        customerList.setData(Collections.singletonList(customer));
        return customerList;
    }

}
