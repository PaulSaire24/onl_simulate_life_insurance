package com.bbva.rbvd.lib.r302.transform.map;

import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDProperties;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ContractMap {
    private ContractMap(){

    }
    public static Map<String, Object> mapInsuranceAmount(BigDecimal idProduct, String idHolder){

        Map<String, Object> mapStringObject = new HashMap<>();
        mapStringObject.put(RBVDProperties.FIELD_OR_FILTER_INSURANCE_PRODUCT_ID.getValue(), idProduct);
        mapStringObject.put(RBVDProperties.FIELD_CUSTOMER_ID.getValue(), idHolder);

        return mapStringObject;
    }
}
