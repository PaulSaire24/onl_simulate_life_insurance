package com.bbva.rbvd.lib.r302.service.api;

import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.pisd.dto.insurance.aso.crypto.CryptoASO;
import com.bbva.pisd.dto.insurance.aso.tier.TierASO;
import com.bbva.rbvd.lib.r301.RBVDR301;

public class ConsumerInternalService {

    private final RBVDR301 rbvdR301;

    public ConsumerInternalService(RBVDR301 rbvdR301) {
        this.rbvdR301 = rbvdR301;
    }


    public CryptoASO callCryptoService(String customerID) {
        return this.rbvdR301.executeCryptoService(new CryptoASO(customerID));
    }

    public TierASO callGetTierService(String document) {
        return this.rbvdR301.executeGetTierService(document);
    }


    public CustomerListASO callListCustomerResponse(String customerId){ return this.rbvdR301.executeCallListCustomerResponse(customerId);}

}
