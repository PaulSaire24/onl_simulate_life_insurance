package com.bbva.rbvd.lib.r302.service.dao.impl;

import com.bbva.pisd.lib.r350.PISDR350;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDErrors;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDProperties;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDValidation;
import com.bbva.rbvd.lib.r302.service.dao.IContractDAO;
import com.bbva.rbvd.lib.r302.transform.map.ProductMap;
import com.bbva.rbvd.lib.r302.util.ValidationUtil;

import java.math.BigDecimal;
import java.util.Map;

import static org.springframework.util.CollectionUtils.isEmpty;

public class ContractDAOImpl implements IContractDAO {

    private PISDR350 pisdR350;
    private ProductDAOImpl productDAO;

    public ContractDAOImpl(PISDR350 pisdR350) {
        this.pisdR350 = pisdR350;
    }

    public BigDecimal getInsuranceAmountDAO(String productId,String customerId){

        Map<String, Object> responseQueryGetProductInformation =
                this.pisdR350.executeGetASingleRow(RBVDProperties.QUERY_GET_PRODUCT_INFORMATION.getValue(), ProductMap.mapProductId(productId));

        Map<String, Object> responseQueryGetCumulus =
                this.pisdR350.executeGetListASingleRow(RBVDProperties.QUERY_GET_INSURANCE_AMOUNT.getValue(), ProductMap.mapInsuranceAmount(
                        (BigDecimal) responseQueryGetProductInformation.get(RBVDProperties.FIELD_OR_FILTER_INSURANCE_PRODUCT_ID.getValue()), customerId));

        if(isEmpty(responseQueryGetCumulus)) {
            throw RBVDValidation.build(RBVDErrors.WRONG_PRODUCT_CODE);
        }

        BigDecimal sumCumulus = ValidationUtil.validateQueryGetInsuranceAmount(responseQueryGetCumulus);
        return sumCumulus;
    }


}
