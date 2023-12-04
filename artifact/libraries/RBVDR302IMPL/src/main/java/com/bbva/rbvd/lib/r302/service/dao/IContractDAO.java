package com.bbva.rbvd.lib.r302.service.dao;

import java.math.BigDecimal;

public interface IContractDAO {

    BigDecimal getInsuranceAmountDAO(BigDecimal insuranceProductId , String customerId , String documentNumber);

}
