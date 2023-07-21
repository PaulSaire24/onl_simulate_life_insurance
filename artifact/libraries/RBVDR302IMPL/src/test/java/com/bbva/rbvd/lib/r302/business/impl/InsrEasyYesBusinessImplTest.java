package com.bbva.rbvd.lib.r302.business.impl;
import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.rbvd.dto.lifeinsrc.mock.MockData;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

import static org.mockito.Matchers.anyString;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/META-INF/spring/RBVDR302-app.xml",
        "classpath:/META-INF/spring/RBVDR302-app-test.xml",
        "classpath:/META-INF/spring/RBVDR302-arc.xml",
        "classpath:/META-INF/spring/RBVDR302-arc-test.xml" })

public class InsrEasyYesBusinessImplTest extends TestCase {
    @Mock
    private MockData mockData;
    @Mock
    private LifeSimulationDTO response;
    @Mock
    CustomerListASO responseListCustomers;
    @Mock
    private ApplicationConfigurationService applicationConfigurationService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        //instanciar los atributos
        //LifeSimulationDTO response = mockData( );
        //CustomerListASO responseListCustomers =mockData();

    }

    @Test

    public void serviceAddGifoleNull() throws IOException {
        //given
        String flag = this.applicationConfigurationService.getProperty(anyString());
        //when

        //then

    }
}