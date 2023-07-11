package com.bbva.rbvd.lib.r302.service.dao.impl;

import com.bbva.pisd.lib.r350.PISDR350;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDProperties;
import com.bbva.rbvd.lib.r302.transform.map.ProductMap;

import java.util.Map;

public class ProductDAO {

    private PISDR350 pisdR350;

    public ProductDAO(PISDR350 pisdR350) {
        this.pisdR350 = pisdR350;
    }

    public Map<String,Object> getProductInformationDAO(String productId){
        Map<String, Object> responseQueryGetProductInformation =
                this.pisdR350.executeGetASingleRow(RBVDProperties.QUERY_GET_PRODUCT_INFORMATION.getValue(), ProductMap.mapProductId(productId));

        return responseQueryGetProductInformation;
    }


}
