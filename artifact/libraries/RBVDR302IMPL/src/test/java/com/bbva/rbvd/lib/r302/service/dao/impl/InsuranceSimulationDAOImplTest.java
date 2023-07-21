package com.bbva.rbvd.lib.r302.service.dao.impl;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.pisd.lib.r350.PISDR350;
<<<<<<< HEAD
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDErrors;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDProperties;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDValidation;
=======
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDProperties;
>>>>>>> b173725e97713327a22a9240c3c5fc25f80b3dca
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
<<<<<<< HEAD
import java.io.IOException;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
=======


import static org.mockito.Mockito.mock;
>>>>>>> b173725e97713327a22a9240c3c5fc25f80b3dca

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/META-INF/spring/RBVDR302-app.xml",
        "classpath:/META-INF/spring/RBVDR302-app-test.xml",
        "classpath:/META-INF/spring/RBVDR302-arc.xml",
        "classpath:/META-INF/spring/RBVDR302-arc-test.xml" })

public class InsuranceSimulationDAOImplTest extends TestCase {
<<<<<<< HEAD
    private PISDR350 pisdR350;
    private InsuranceSimulationDAOImpl insuranceSimulation;
    @Before
    public void setup(){
        pisdR350 = mock(PISDR350.class);
        insuranceSimulation = new InsuranceSimulationDAOImpl(pisdR350);
    }
    @Test(expected = BusinessException.class)
    public void getInsertInsuranceSimulationFailCase() {

=======

    private PISDR350 pisdR350;
    private InsuranceSimulationDAOImpl insuranceSimulation;

    @Before
    public void setUp() {
        pisdR350 = mock(PISDR350.class);
        insuranceSimulation = new InsuranceSimulationDAOImpl(pisdR350);
    }

    /*@Test
    public void InsuranceSimulationDAOImplNotOne() {
        //Agregando tres partes para el test
        //given
        when(this.pisdR350.executeInsertSingleRow(Mockito.anyString(), Mockito.anyMap())).thenReturn(2);
        InsuranceSimulationDAOImpl insuranceSimulationDAO = new InsuranceSimulationDAOImpl(pisdR350);
        //when(rbvdValidation.build(any)(RBVDErrors.INSERTION_ERROR_IN_SIMULATION_TABLE);
        //when
        insuranceSimulationDAO.getInsertInsuranceSimulation(argumentsForSaveSimulation);
        //then
        assertEquals(2, argumentsForSaveSimulation.get(new Object()));

    }*/

    @Test(expected = BusinessException.class)
    public void getInsertInsuranceSimulationFailCase() {
>>>>>>> b173725e97713327a22a9240c3c5fc25f80b3dca
        insuranceSimulation.getInsertInsuranceSimulation(null);
        Mockito.verify(pisdR350, Mockito.atLeastOnce()).executeInsertSingleRow(RBVDProperties.QUERY_INSERT_INSURANCE_SIMULATION.getValue(),null);
    }

}