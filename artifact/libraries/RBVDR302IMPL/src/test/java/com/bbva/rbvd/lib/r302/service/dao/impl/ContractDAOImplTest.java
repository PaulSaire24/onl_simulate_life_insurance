package com.bbva.rbvd.lib.r302.service.dao.impl;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.pisd.lib.r350.PISDR350;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDProperties;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.HashMap;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ContractDAOImplTest {

    private ContractDAOImpl contractDAOImpl;
    private PISDR350 pisdR350;
    private BigDecimal insuranceProductId;
    private String customerId;

    @Before
    public void setUp() throws Exception {

        pisdR350 = mock(PISDR350.class);

        insuranceProductId  = new BigDecimal(12);

        customerId = "cliente001";

        contractDAOImpl = new ContractDAOImpl(pisdR350);
    }

    @Test(expected = BusinessException.class)
    public void getInsuranceAmountDAO_NULL(){

        when(this.pisdR350.executeGetListASingleRow(RBVDProperties.QUERY_GET_INSURANCE_AMOUNT.getValue(), new HashMap<>())).
                thenReturn(null);

        contractDAOImpl.getInsuranceAmountDAO(insuranceProductId, customerId);
    }

}