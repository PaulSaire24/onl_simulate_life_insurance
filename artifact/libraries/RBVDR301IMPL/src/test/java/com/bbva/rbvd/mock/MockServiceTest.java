package com.bbva.rbvd.mock;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.pisd.dto.insurance.aso.tier.TierASO;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockServiceTest {

    private MockService mockService = new MockService();
    private ApplicationConfigurationService applicationConfigurationService;

    @Before
    public void setUp() {
        applicationConfigurationService = mock(ApplicationConfigurationService.class);
        mockService.setApplicationConfigurationService(applicationConfigurationService);
    }

    @Test
    public void isEnabledTierMockTrue() {
        when(applicationConfigurationService.getProperty(anyString())).thenReturn("true");
        boolean validation = mockService.isEnabledTierMock();
        assertTrue(validation);
    }

    @Test
    public void isEnabledTierMockFalse() {
        when(applicationConfigurationService.getProperty(anyString())).thenReturn("false");
        boolean validation = mockService.isEnabledTierMock();
        assertFalse(validation);
    }

    @Test
    public void getTierASOMockOK() {
        String tierResponse = "{\"data\":[{\"id\":\"0001\",\"description\":\"TIER 1\",\"factorType\":\"C\",\"chargeFactor\":15.000000,\"effectiveStartDate\":\"2015-08-24\",\"effectiveEndDate\":\"2015-09-28\",\"segments\":[{\"id\":\"86300\",\"description\":\"VIP\"}]}]}";
        when(applicationConfigurationService.getProperty(anyString())).thenReturn(tierResponse);
        TierASO validation = mockService.getTierASOMock();
        assertNotNull(validation);
    }

}
