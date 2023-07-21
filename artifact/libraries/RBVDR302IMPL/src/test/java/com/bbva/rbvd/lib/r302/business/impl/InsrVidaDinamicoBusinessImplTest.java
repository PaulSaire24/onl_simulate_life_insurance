package com.bbva.rbvd.lib.r302.business.impl;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.pisd.dto.insurance.aso.tier.TierASO;
import com.bbva.pisd.dto.insurance.mock.MockDTO;
import com.bbva.pisd.lib.r350.PISDR350;
import com.bbva.rbvd.dto.lifeinsrc.mock.MockData;
import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.InsuranceLifeSimulationBO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;
import com.bbva.rbvd.lib.r301.RBVDR301;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.math.BigDecimal;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/META-INF/spring/RBVDR302-app.xml",
        "classpath:/META-INF/spring/RBVDR302-app-test.xml",
        "classpath:/META-INF/spring/RBVDR302-arc.xml",
        "classpath:/META-INF/spring/RBVDR302-arc-test.xml" })

public class InsrVidaDinamicoBusinessImplTest{
    private RBVDR301 rbvdR301;
    private InsrVidaDinamicoBusinessImpl insrVidaDinamicoBusiness;
    private TierASO responseTier;
    private CustomerListASO customerList;
    private ApplicationConfigurationService applicationConfigurationService;
    private LifeSimulationDTO requestInput;
    private MockDTO mockDTO;
    private MockData mockData;
    @Before
    public void setup() throws IOException {
        applicationConfigurationService = mock(ApplicationConfigurationService.class);
        rbvdR301 = mock(RBVDR301.class);
        //responseTier = mockDTO.getTierMockResponse();
        mockData = MockData.getInstance();
        mockDTO = MockDTO.getInstance();
        customerList = mockDTO.getCustomerDataResponse();
        requestInput = mockData.getInsuranceSimulationRequest();
        insrVidaDinamicoBusiness = new InsrVidaDinamicoBusinessImpl(rbvdR301);
    }

    @Test(expected = BusinessException.class)
    public void executeQuotationRimacServiceResultNull() {
    //given

        when(applicationConfigurationService.getProperty("IS_MOCK_MODIFY_QUOTATION_DYNAMIC")).thenReturn("N");
        when(rbvdR301.executeSimulationModificationRimacService(Mockito.anyObject(),Mockito.anyString(),Mockito.anyString())).
                thenReturn(null);

    //when
        InsuranceLifeSimulationBO result = insrVidaDinamicoBusiness.executeQuotationRimacService(
                requestInput,
                "",
                customerList,
                BigDecimal.valueOf(1),
                applicationConfigurationService);
    //then
        Assert.assertNull(result);
    }

    @Test(expected = BusinessException.class)
    public void executeModifyQuotationRimacServiceResultNull() {
        //given

        when(applicationConfigurationService.getProperty("IS_MOCK_MODIFY_QUOTATION_DYNAMIC")).thenReturn("N");
        //when(rbvdR301.executeSimulationModificationRimacService(Mockito.anyObject(),Mockito.anyString(),Mockito.anyString())).
        //        thenReturn(null);

        //when
        InsuranceLifeSimulationBO result = insrVidaDinamicoBusiness.executeModifyQuotationRimacService(
                requestInput,
                customerList,
                BigDecimal.valueOf(1),
                applicationConfigurationService);
        //then
        Assert.assertNull(result);
    }
    @Test
    public void executeModifyQuotationRimacServiceResultNotNull() {
        //given

        when(applicationConfigurationService.getProperty("IS_MOCK_MODIFY_QUOTATION_DYNAMIC")).thenReturn("N");
        when(rbvdR301.executeSimulationModificationRimacService(Mockito.anyObject(),Mockito.anyString(),Mockito.anyString())).
                thenReturn(Mockito.anyObject());

        //when
        InsuranceLifeSimulationBO result = insrVidaDinamicoBusiness.executeModifyQuotationRimacService(
                requestInput,
                customerList,
                BigDecimal.valueOf(1),
                applicationConfigurationService);
        //then
        Assert.assertNotNull(result);
    }
}