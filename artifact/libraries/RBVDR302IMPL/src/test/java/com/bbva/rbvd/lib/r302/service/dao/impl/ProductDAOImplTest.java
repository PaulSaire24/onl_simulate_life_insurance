package com.bbva.rbvd.lib.r302.service.dao.impl;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.pisd.lib.r350.PISDR350;
import org.junit.Before;
import org.junit.Test;


import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProductDAOImplTest {

    //private IProductDAO iProductDAOImpl;
    private ProductDAOImpl productDAOImpl;
    private PISDR350 pisdR350;
    private String productId;


    @Before
    public void setUp() throws Exception {

        //iProductDAOImpl = mock(IProductDAO.class);

        pisdR350 = mock(PISDR350.class);

        productId = "841";

        productDAOImpl = new ProductDAOImpl(pisdR350);

    }

    @Test(expected = BusinessException.class)
    public void getProductInformationByIdTest_NULL(){

        when(this.pisdR350.executeGetASingleRow(anyString(), anyMap())).
                thenReturn(null);

        productDAOImpl.getProductInformationById(productId);
    }

}