package com.bbva.rbvd.lib.r302.impl.util;

import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.InsuranceLifeSimulationBO;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

public class MockResponseTest {

    MockResponse mockResponse = new MockResponse();

    @Test
    public void test_getMockResponseRimacService(){
        InsuranceLifeSimulationBO mock = mockResponse.getMockResponseRimacService();

        Assert.assertNotNull(mock);
        Assert.assertNotNull(mock.getPayload());
        Assert.assertNotNull(mock.getPayload().getCotizaciones());
        Assert.assertNotNull(mock.getPayload().getProducto());
        Assert.assertNotNull(mock.getPayload().getMoneda());
        Assert.assertNotNull(mock.getPayload().getPlanes());
        Assert.assertNotNull(mock.getPayload().getDatosParticulares());
        Assert.assertNotNull(mock.getPayload().getFinanciamiento());
    }

}