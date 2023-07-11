package com.bbva.rbvd.lib.r302.service.dao.impl;

import com.bbva.pisd.lib.r350.PISDR350;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDProperties;
import com.bbva.rbvd.lib.r302.service.dao.IContractDAO;
import com.bbva.rbvd.lib.r302.transform.map.ProductMap;

import java.math.BigDecimal;
import java.util.Map;

public class ContractDAOImpl implements IContractDAO {

    private PISDR350 pisdR350;

    public ContractDAOImpl(PISDR350 pisdR350) {
        this.pisdR350 = pisdR350;
    }

    public Map<String,Object> getInsuranceAmountDAO(BigDecimal idProduct,String customerId){
        Map<String, Object> responseQueryGetCumulus =
                this.pisdR350.executeGetListASingleRow(RBVDProperties.QUERY_GET_INSURANCE_AMOUNT.getValue(), ProductMap.mapInsuranceAmount(
                        idProduct, customerId));

        return responseQueryGetCumulus;
    }


}
