package com.bbva.rbvd.lib.r302.service.api;

import com.bbva.pisd.dto.insurance.aso.crypto.CryptoASO;
import com.bbva.pisd.dto.insurance.aso.tier.TierASO;
import com.bbva.rbvd.lib.r301.RBVDR301;

public class ConsumerInternalService {
    public ConsumerInternalService(RBVDR301 rbvdR301) {
        this.rbvdR301 = rbvdR301;
    }

    private RBVDR301 rbvdR301;

    public CryptoASO callCryptoService(String customerID) {
        return rbvdR301.executeCryptoService(new CryptoASO(customerID));

    }

    public TierASO callGetTierService(String document) {
        return rbvdR301.executeGetTierService(document);
    }


}
