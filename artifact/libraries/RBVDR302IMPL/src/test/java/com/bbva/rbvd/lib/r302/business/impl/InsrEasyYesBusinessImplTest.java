package com.bbva.rbvd.lib.r302.business.impl;
import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.pisd.dto.insurance.aso.gifole.GifoleInsuranceRequestASO;
import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.InsuranceLifeSimulationBO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;
import com.bbva.rbvd.lib.r301.RBVDR301;
import com.bbva.rbvd.lib.r302.transfer.PayloadConfig;
import com.bbva.rbvd.lib.r302.transfer.PayloadStore;
import com.bbva.rbvd.lib.r302.transform.bean.QuotationRimac;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import java.math.BigDecimal;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
@RunWith(MockitoJUnitRunner.class)

public class InsrEasyYesBusinessImplTest extends TestCase {

    private CustomerListASO customerList;
    @Mock
    private LifeSimulationDTO response;
    @Mock
    CustomerListASO responseListCustomers;
    private RBVDR301 rbvdR301;
    @Mock
    private ApplicationConfigurationService applicationConfigurationService;
    @Mock
    private GifoleInsuranceRequestASO gifoleInsuranceRequest;
    @Mock
    private InsuranceLifeSimulationBO responseRimac;
    @Mock
    private LifeSimulationDTO input;
    @Mock
    private BigDecimal cumulo;
    @Mock
    private String productInformation;
    @Mock
    private InsuranceLifeSimulationBO requestRimac;

    @Mock
    private InsuranceLifeSimulationBO executeSimulationRimacService;

    @Mock
    private InsuranceLifeSimulationBO payload;

    @Mock
    private InsrEasyYesBusinessImpl insrEasyYesBusinessImpl;

    @Mock
    private PayloadConfig payloadConfig;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        //instanciar los atributos
        rbvdR301 = mock(RBVDR301.class);
        applicationConfigurationService = mock(ApplicationConfigurationService.class);
        requestRimac = QuotationRimac.mapInRequestRimacLife(input, cumulo);
        //responseRimac = rbvdR301.executeSimulationRimacService(payload, input.getTraceId());
        payloadConfig = mock(PayloadConfig.class);
        insrEasyYesBusinessImpl = new InsrEasyYesBusinessImpl(rbvdR301,applicationConfigurationService);
        input = mock(LifeSimulationDTO.class);

    }

    @Test
    public void callQuotationRimacServiceTest(){
        when(this.rbvdR301.executeSimulationRimacService(new InsuranceLifeSimulationBO(), Mockito.anyString())).thenReturn(null);
        PayloadStore payload =insrEasyYesBusinessImpl.doEasyYes(payloadConfig);
        Assert.assertNull(payload);

    }
}