package com.bbva.rbvd.lib.r302.transform.map;

import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDProperties;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public class ProductMap {

    private ProductMap(){}

    public static Map<String, Object> mapProductId(String arguments){

        Map<String, Object> mapStringObject = new HashMap<>();
        mapStringObject.put(RBVDProperties.FILTER_INSURANCE_PRODUCT_TYPE.getValue(), arguments);

        return mapStringObject;
    }

    public static Map<String, Object> mapInsuranceAmount(BigDecimal idProduct, String idHolder){

        Map<String, Object> mapStringObject = new HashMap<>();
        mapStringObject.put(RBVDProperties.FIELD_OR_FILTER_INSURANCE_PRODUCT_ID.getValue(), idProduct);
        mapStringObject.put(RBVDProperties.FIELD_CUSTOMER_ID.getValue(), idHolder);

        return mapStringObject;
    }

    public static Map<String, Object> createModalitiesInformationFilters(String plansPT, BigDecimal insuranceProductId, String saleChannel) {
        Map<String, Object> filters = new HashMap<>();

        String[] plansPTArray = plansPT.split(",");
        List<String> planes = Arrays.stream(plansPTArray).collect(toList());

        filters.put(RBVDProperties.FIELD_OR_FILTER_INSURANCE_PRODUCT_ID.getValue(), insuranceProductId);
        filters.put(RBVDProperties.FIELD_OR_FILTER_INSURANCE_MODALITY_TYPE.getValue(), planes);
        filters.put(RBVDProperties.FIELD_SALE_CHANNEL_ID.getValue(), saleChannel);
        return filters;
    }


}
