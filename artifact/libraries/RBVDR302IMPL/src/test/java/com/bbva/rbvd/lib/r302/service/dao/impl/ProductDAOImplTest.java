package com.bbva.rbvd.lib.r302.service.dao.impl;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.pisd.lib.r350.PISDR350;
import com.bbva.rbvd.dto.lifeinsrc.dao.ProductInformationDAO;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDProperties;
import com.bbva.rbvd.lib.r302.service.dao.IModalitiesDAO;
import com.bbva.rbvd.lib.r302.service.dao.IProductDAO;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

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

    @Test
    public void getProductInformationByIdTest_NULL(){

        when(this.pisdR350.executeGetASingleRow(RBVDProperties.QUERY_GET_PRODUCT_INFORMATION.getValue(), new HashMap<>())).
                thenReturn(null);

        ProductInformationDAO productInformationDAO = productDAOImpl.getProductInformationById(Mockito.anyString());

        Assert.assertNull(productInformationDAO);
    }

}