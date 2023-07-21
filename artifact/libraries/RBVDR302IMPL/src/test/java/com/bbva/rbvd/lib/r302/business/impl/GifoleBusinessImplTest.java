package com.bbva.rbvd.lib.r302.business.impl;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.pisd.dto.insurance.aso.gifole.GifoleInsuranceRequestASO;
import com.bbva.pisd.dto.insurance.mock.MockDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.PeriodDTO;
import com.bbva.rbvd.dto.lifeinsrc.mock.MockData;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;
import com.bbva.rbvd.lib.r301.RBVDR301;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GifoleBusinessImplTest  {

    private CustomerListASO requestCustomerListASO;
    private MockDTO mockDTO;
    private MockData mockData;
    private ApplicationConfigurationService applicationConfigurationService;

    private LifeSimulationDTO responseOut;
    private GifoleBusinessImpl gifoleBusiness ;
    private RBVDR301 rbvdR301;


    @Before
    public void setUp() throws Exception {

        applicationConfigurationService = mock(ApplicationConfigurationService.class);

        mockData = MockData.getInstance();

        mockDTO = MockDTO.getInstance();
        rbvdR301 = mock(RBVDR301.class);
        responseOut = mockData.getInsuranceSimulationResponse();

        gifoleBusiness = new GifoleBusinessImpl(rbvdR301,applicationConfigurationService);
    }

    @Test
    public void createGifoleASO_OK() throws IOException {

        requestCustomerListASO = mockDTO.getCustomerDataResponse();

        GifoleInsuranceRequestASO validation = gifoleBusiness.createGifoleASO(responseOut, requestCustomerListASO);

        Assert.assertNotNull(validation);

        requestCustomerListASO.getData().get(0).setFirstName("null");
        validation = gifoleBusiness.createGifoleASO(responseOut, requestCustomerListASO);

        Assert.assertNotNull(validation);

        requestCustomerListASO.getData().get(0).setFirstName(null);
        validation = gifoleBusiness.createGifoleASO(responseOut, requestCustomerListASO);

        Assert.assertNotNull(validation);

        requestCustomerListASO.getData().get(0).setFirstName(" ");
        responseOut.getHolder().getIdentityDocument().setDocumentNumber(null);
        validation = gifoleBusiness.createGifoleASO(responseOut, requestCustomerListASO);

        Assert.assertNotNull(validation);
    }

    @Test
    public void createGifoleASO_NULL() {
        responseOut.getProduct().getPlans().get(2).getInstallmentPlans().get(1).setPeriod(new PeriodDTO());

        GifoleInsuranceRequestASO validation = gifoleBusiness.createGifoleASO(responseOut, null);

        Assert.assertNotNull(validation);
    }
    @Test
    public void serviceAddGifoleCaseElseTest() throws IOException {
        when(applicationConfigurationService.getProperty(anyString())).thenReturn("true");
        requestCustomerListASO = mockDTO.getCustomerDataResponse();

        gifoleBusiness.serviceAddGifole(responseOut, requestCustomerListASO);

        Mockito.verify(rbvdR301, Mockito.atLeastOnce()).executeGifolelifeService(anyObject());
    }

}