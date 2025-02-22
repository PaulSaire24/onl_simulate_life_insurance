package com.bbva.rbvd.lib.r302.service.dao.impl;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.pisd.lib.r350.PISDR350;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDProperties;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.Mockito.mock;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/META-INF/spring/RBVDR302-app.xml",
        "classpath:/META-INF/spring/RBVDR302-app-test.xml",
        "classpath:/META-INF/spring/RBVDR302-arc.xml",
        "classpath:/META-INF/spring/RBVDR302-arc-test.xml" })

public class InsuranceSimulationDAOImplTest {

    private PISDR350 pisdR350;
    private InsuranceSimulationDAOImpl insuranceSimulation;


    @Before
    public void setUp() {
        pisdR350 = mock(PISDR350.class);
        insuranceSimulation = new InsuranceSimulationDAOImpl(pisdR350);
    }


    @Test(expected = BusinessException.class)
    public void getInsertInsuranceSimulationFailCase() {
        insuranceSimulation.getInsertInsuranceSimulation(null);
        Mockito.verify(pisdR350, Mockito.atLeastOnce()).executeInsertSingleRow(RBVDProperties.QUERY_INSERT_INSURANCE_SIMULATION.getValue(),null);
    }

}