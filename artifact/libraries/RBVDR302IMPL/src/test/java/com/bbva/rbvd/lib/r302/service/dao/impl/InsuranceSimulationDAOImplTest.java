package com.bbva.rbvd.lib.r302.service.dao.impl;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.pisd.lib.r350.PISDR350;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDErrors;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDValidation;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import java.io.IOException;
import java.util.Map;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/META-INF/spring/RBVDR302-app.xml",
        "classpath:/META-INF/spring/RBVDR302-app-test.xml",
        "classpath:/META-INF/spring/RBVDR302-arc.xml",
        "classpath:/META-INF/spring/RBVDR302-arc-test.xml" })

public class InsuranceSimulationDAOImplTest extends TestCase {

    @InjectMocks
    private InsuranceSimulationDAOImpl insuranceSimulationDAO;
    @Mock
    private Map<String, Object> argumentsForSaveSimulation;

    @Mock
    private PISDR350 pisdR350;
    @Mock
    private RBVDValidation rbvdValidation;

    @Mock
    private BusinessException build;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(argumentsForSaveSimulation.get(Mockito.anyString())).thenReturn(new Object());
    }
    @Test
    public void InsuranceSimulationDAOImplNotOne() throws IOException {
        //Agregando tres partes para el test
        //given
        when(this.pisdR350.executeInsertSingleRow(Mockito.anyString(), Mockito.anyMap())).thenReturn(2);
        InsuranceSimulationDAOImpl insuranceSimulationDAO = new InsuranceSimulationDAOImpl(pisdR350);
        //when(rbvdValidation.build(any)(RBVDErrors.INSERTION_ERROR_IN_SIMULATION_TABLE);
        //when
        insuranceSimulationDAO.getInsertInsuranceSimulation(argumentsForSaveSimulation);
        //then
        assertEquals(2, argumentsForSaveSimulation.get(new Object()));

    }

    }