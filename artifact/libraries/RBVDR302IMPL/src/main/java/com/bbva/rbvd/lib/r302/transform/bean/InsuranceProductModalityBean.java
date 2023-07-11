package com.bbva.rbvd.lib.r302.transform.bean;

import com.bbva.rbvd.dto.lifeinsrc.dao.InsuranceProductModalityDAO;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDProperties;

import java.math.BigDecimal;
import java.util.Map;

public class InsuranceProductModalityBean {

    private InsuranceProductModalityBean(){
    }

    //Crea la modalidad del producto asegurado
    public static InsuranceProductModalityDAO createInsuranceProductModalityDAO(Map<String, Object> mapElement) {
        return new InsuranceProductModalityDAO((String) mapElement.get(RBVDProperties.FIELD_INSURANCE_COMPANY_MODALITY_ID.getValue()),
                (String) mapElement.get(RBVDProperties.FIELD_INSURANCE_MODALITY_NAME.getValue()),
                (String) mapElement.get(RBVDProperties.FIELD_OR_FILTER_INSURANCE_MODALITY_TYPE.getValue()),
                (String) mapElement.get(RBVDProperties.FIELD_SUGGESTED_MODALITY_IND_TYPE.getValue()),
                (BigDecimal) mapElement.get(RBVDProperties.FIELD_PUBLICATION_ORDER_NUMBER.getValue()));
    }

}
