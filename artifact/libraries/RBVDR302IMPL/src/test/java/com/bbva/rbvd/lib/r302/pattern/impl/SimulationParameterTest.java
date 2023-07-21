package com.bbva.rbvd.lib.r302.pattern.impl;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.pisd.dto.insurance.aso.crypto.CryptoASO;
import com.bbva.pisd.dto.insurance.aso.crypto.CryptoDataASO;
import com.bbva.pisd.dto.insurance.aso.tier.TierASO;
import com.bbva.pisd.dto.insurance.mock.MockDTO;
import com.bbva.pisd.lib.r350.PISDR350;
import com.bbva.rbvd.dto.lifeinsrc.mock.MockData;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;
import com.bbva.rbvd.lib.r301.RBVDR301;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/META-INF/spring/RBVDR302-app.xml",
        "classpath:/META-INF/spring/RBVDR302-app-test.xml",
        "classpath:/META-INF/spring/RBVDR302-arc.xml",
        "classpath:/META-INF/spring/RBVDR302-arc-test.xml" })
public class SimulationParameterTest extends TestCase {
    private MockData mockData;

    @Mock
    private RBVDR301 rbvdr301;
    @Mock
    private PISDR350 pisdR350;

    @Mock
    private ApplicationConfigurationService applicationConfigurationService;

    private CryptoASO crypto;

    private TierASO tier;

    private MockDTO mockDTO;


    @Before

    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);
        //instanciar los atributos
        mockData = MockData.getInstance();
        mockDTO = MockDTO.getInstance();
        crypto = new CryptoASO();
        CryptoDataASO data = new CryptoDataASO();
        crypto.setData(data);
        tier = mockDTO.getTierMockResponse();

    }

    @Test

    public void getTierToUpdateRequestNull() throws IOException {
        //Agregar comportamientos para el full test
        //given
        LifeSimulationDTO input = mockData.getInsuranceSimulationRequest();
        when(this.rbvdr301.executeCryptoService(anyObject())).thenReturn(crypto);
        tier.getData().get(0).setSegments(null);
        when(this.rbvdr301.executeGetTierService(anyObject())).thenReturn(tier);
        SimulationParameter simulationParameter = new SimulationParameter(pisdR350,rbvdr301,input,applicationConfigurationService);
        //when

        simulationParameter.getTierToUpdateRequest(input);
        //then
        assertEquals(null,input.getId());
        }
    }
