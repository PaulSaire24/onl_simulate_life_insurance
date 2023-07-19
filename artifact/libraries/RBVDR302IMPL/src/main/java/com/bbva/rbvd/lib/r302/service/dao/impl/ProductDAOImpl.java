package com.bbva.rbvd.lib.r302.service.dao.impl;

import com.bbva.pisd.lib.r350.PISDR350;
import com.bbva.rbvd.dto.lifeinsrc.dao.ProductInformationDAO;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDErrors;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDProperties;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDValidation;
import com.bbva.rbvd.lib.r302.service.dao.IProductDAO;
import com.bbva.rbvd.lib.r302.transform.bean.ProductBean;
import com.bbva.rbvd.lib.r302.transform.map.ProductMap;

import java.util.Map;

import static org.springframework.util.CollectionUtils.isEmpty;

public class ProductDAOImpl implements IProductDAO {

    private PISDR350 pisdR350;

    public ProductDAOImpl (PISDR350 pisdR350){
        this.pisdR350=pisdR350;
    }

    @Override
    public ProductInformationDAO getProductInformationById(String productId)
    {
        Map<String, Object> responseQueryGetProductInformation =
                this.pisdR350.executeGetASingleRow(RBVDProperties.QUERY_GET_PRODUCT_INFORMATION.getValue(), ProductMap.mapProductId(productId));

        if(isEmpty(responseQueryGetProductInformation)) {
            throw RBVDValidation.build(RBVDErrors.WRONG_PRODUCT_CODE);
        }

        ProductInformationDAO productInformationDAO = ProductBean.getProductInformation(responseQueryGetProductInformation);

        return productInformationDAO;

    }


}
