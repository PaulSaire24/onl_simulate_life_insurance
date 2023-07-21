package com.bbva.rbvd.lib.r302.business.impl;
import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.pisd.dto.insurance.aso.gifole.GifoleInsuranceRequestASO;
import com.bbva.rbvd.dto.lifeinsrc.mock.MockData;
import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.InsuranceLifeSimulationBO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;
import com.bbva.rbvd.lib.r301.RBVDR301;
import com.bbva.rbvd.lib.r302.transform.objects.QuotationRimac;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import java.io.IOException;
import java.math.BigDecimal;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;


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
    @Mock
    private RBVDR301 rbvdR301;
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

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        //instanciar los atributos
        applicationConfigurationService = mock(ApplicationConfigurationService.class);
        input = new LifeSimulationDTO();
        rbvdR301 = mock(RBVDR301.class);
        requestRimac = QuotationRimac.mapInRequestRimacLife(input, cumulo);
        //responseRimac = rbvdR301.executeSimulationRimacService();


    }
    @Test

    public void serviceAddGifoleTest() throws IOException {
        //given
        String flag = this.applicationConfigurationService.getProperty(anyString());
        //when
        //when(flag.equals("true")).thenReturn(gifoleInsuranceRequest.createGifoleASO(response, responseListCustomers));
        //then
        //assertEquals(rbvdR301.executeGifolelifeService(gifoleInsuranceRequest));

    }
    @Test

    public void callQuotationRimacServiceTest() throws IOException{
        //given

        //when
        //when(Objects.isNull(responseRimac)).thenReturn(requestRimac,input.getTraceId());
        //then
        //InsrEasyYesBusinessImpl insrEasyYesBusiness = InsrEasyYesBusinessImpl(rbvdR301,applicationConfigurationService);
        //assertEquals(1, RBVDValidation.build(RBVDErrors.(anyString())));
    }
}