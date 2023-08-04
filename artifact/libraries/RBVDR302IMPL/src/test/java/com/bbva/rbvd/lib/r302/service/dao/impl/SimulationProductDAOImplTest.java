package com.bbva.rbvd.lib.r302.service.dao.impl;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.pisd.lib.r350.PISDR350;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDProperties;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SimulationProductDAOImplTest {

    private SimulationProductDAOImpl simulationProductDAOImpl;
    private PISDR350 pisdR350;

    private Map<String,Object> argument;

    @Before
    public void setUp() throws Exception {


        pisdR350 = mock(PISDR350.class);

        simulationProductDAOImpl = new SimulationProductDAOImpl(pisdR350);

        argument = new HashMap<>();
    }


    @Test(expected = BusinessException.class)
    public void insertSimulationProduct_ERROR() {

        when(this.pisdR350.executeInsertSingleRow(RBVDProperties.QUERY_INSERT_INSRNC_SIMLT_PRD.getValue(), new HashMap<>())).
                thenReturn(0);

        simulationProductDAOImpl.insertSimulationProduct(argument);
    }

}