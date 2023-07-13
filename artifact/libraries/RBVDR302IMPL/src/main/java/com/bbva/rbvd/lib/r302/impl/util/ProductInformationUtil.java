package com.bbva.rbvd.lib.r302.impl.util;

import com.bbva.pisd.lib.r350.PISDR350;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDProperties;
import com.bbva.rbvd.lib.r302.transform.map.ProductMap;

import java.util.Map;

public class ProductInformationUtil {

    private PISDR350 pisdR350;

    public ProductInformationUtil(PISDR350 pisdR350) {
        this.pisdR350 = pisdR350;
    }

    public Map<String, Object> getProductInformationById(String productId) {

        return this.pisdR350.executeGetASingleRow(RBVDProperties.QUERY_GET_PRODUCT_INFORMATION.getValue(), ProductMap.mapProductId(productId));
    }
}
