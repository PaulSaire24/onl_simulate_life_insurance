package com.bbva.rbvd.lib.r302.transform.bean;

import com.bbva.rbvd.dto.lifeinsrc.dao.ProductInformationDAO;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDProperties;

import java.math.BigDecimal;
import java.util.Map;

public class ProductBean {

    public static ProductInformationDAO getProductInformation(Map<String, Object> responseQueryGetProductInformation) {
        String RIMAC_PRODUCT_NAME = "PRODUCT_SHORT_DESC";
        ProductInformationDAO productInformationDAO = new ProductInformationDAO();
        productInformationDAO.setInsuranceProductId((BigDecimal) responseQueryGetProductInformation.get(RBVDProperties.FIELD_OR_FILTER_INSURANCE_PRODUCT_ID.getValue()));
        productInformationDAO.setInsuranceProductDescription((String) responseQueryGetProductInformation.get(RBVDProperties.FIELD_INSURANCE_PRODUCT_DESC.getValue()));
        productInformationDAO.setInsuranceBusinessName((String) responseQueryGetProductInformation.get(RIMAC_PRODUCT_NAME));
        return productInformationDAO;
    }

}
