package com.bbva.rbvd.lib.r302.service.dao;

import java.math.BigDecimal;
import java.util.Map;

public interface IContractDAO {

    BigDecimal getInsuranceAmountDAO(String productId, String customerId);

}
