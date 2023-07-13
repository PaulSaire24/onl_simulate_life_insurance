package com.bbva.rbvd.lib.r302.service.dao;

import com.bbva.rbvd.dto.lifeinsrc.dao.InsuranceProductModalityDAO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface IModalitiesDAO {

    List<InsuranceProductModalityDAO> getModalitiesInfo(String plansPT, BigDecimal insuranceProductId, String saleChannel);
}
