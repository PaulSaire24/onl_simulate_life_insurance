package com.bbva.rbvd.lib.r302.transform.list;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.rbvd.dto.lifeinsrc.commons.*;
import com.bbva.rbvd.dto.lifeinsrc.dao.InsuranceProductModalityDAO;
import com.bbva.rbvd.dto.lifeinsrc.mock.MockData;
import com.bbva.rbvd.dto.lifeinsrc.rimac.commons.PlanBO;
import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.CotizacionBO;
import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.InsuranceLifeSimulationBO;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/META-INF/spring/RBVDR302-app.xml",
        "classpath:/META-INF/spring/RBVDR302-app-test.xml",
        "classpath:/META-INF/spring/RBVDR302-arc.xml",
        "classpath:/META-INF/spring/RBVDR302-arc-test.xml" })

public class ListInstallmentPlanTest extends TestCase {
    private static final Logger LOGGER = LoggerFactory.getLogger(ListInstallmentPlanTest.class);
    private ListInstallmentPlan listInstallmentPlan = new ListInstallmentPlan();
    private ApplicationConfigurationService applicationConfigurationService;
    private List<InsuranceProductModalityDAO> productModalities;
    private InsuranceLifeSimulationBO responseRimac;
    private Boolean seglifePlan1;
    private Boolean seglifePlan2;
    private Boolean seglifePlan3;

    private MockData mockData;

    private String periodicity;

    private PeriodDTO period;

    private CotizacionBO cotizacion;

    @Before
    public void setUp() throws Exception{
        MockitoAnnotations.initMocks(this);
        applicationConfigurationService = mock(ApplicationConfigurationService.class);
        mockData = MockData.getInstance();
        responseRimac = mockData.getInsuranceRimacSimulationResponse();
        productModalities = new ArrayList<>();
        period = new PeriodDTO();
        cotizacion = new CotizacionBO();
    }
    @Test
    public void listInstallmentPlanSuccess(){
        //Agregar comportamientos correctos
        LOGGER.info("ListInstallmentPlanTest - Executing listInstallmentPlanSuccess...");


        when(applicationConfigurationService.getProperty(anyString())).thenReturn(null);

        List<InsurancePlanDTO> plans = listInstallmentPlan.getPlansNamesAndRecommendedValuesAndInstallmentsPlans(productModalities,responseRimac,true,true,true);
        assertNotNull(plans);
    }

    @Test
    public void listInstallmentPlanFullTest(){
        //Agregar comportamientos para el full test
        LOGGER.info("ListInstallmentPlanTest - Executing listInstallmentPlanFullTest...");
        String periodicity = "Mensual";
        PlanBO rimacPlan = cotizacion.getPlan();
        responseRimac.getPayload().getCotizaciones().get(0).setIndicadorBloqueo(1L);
        InsuranceProductModalityDAO modality = new InsuranceProductModalityDAO();
        modality.setInsuranceCompanyModalityId("533629");
        modality.setInsuranceModalityName("PLAN 01 VIDADINAMICO");
        modality.setInsuranceModalityType("01");
        productModalities.add(modality);
        applicationConfigurationService.getProperty(String.valueOf(1));
        //when(period.setId(this.applicationConfigurationService.getProperty(periodicity))).thenReturn(1);
        List<InsurancePlanDTO> plan1 = listInstallmentPlan.getPlansNamesAndRecommendedValuesAndInstallmentsPlans(productModalities, responseRimac, true, false, false);
        assertNotNull(plan1.get(0).getName());
        //modalidad 02
        responseRimac.getPayload().getCotizaciones().get(0).setIndicadorBloqueo(0L);
        responseRimac.getPayload().getCotizaciones().get(0).getPlan().getCoberturas().get(0).setCondicion("INC");
        productModalities.get(0).setInsuranceModalityType("02");
        applicationConfigurationService.getProperty(String.valueOf(2));
        List<InsurancePlanDTO> plan2 = listInstallmentPlan.getPlansNamesAndRecommendedValuesAndInstallmentsPlans(productModalities, responseRimac, false, true, false);
        assertEquals(1, plan2.size());
        responseRimac.getPayload().getCotizaciones().get(0).getPlan().setCoberturas(new ArrayList<>());
        productModalities.get(0).setInsuranceModalityType("02");
        applicationConfigurationService.getProperty(String.valueOf(3));
        List<InsurancePlanDTO> plan = listInstallmentPlan.getPlansNamesAndRecommendedValuesAndInstallmentsPlans(productModalities, responseRimac, false, false, false);
        assertEquals(1, plan.size());

    }
    @Test
    public void listInstallmentPlanNullTest(){
        //Agregar comportamientos para el null test
        LOGGER.info("ListInstallmentPlanTest - Executing listInstallmentPlanNullTest...");
        List<InsuranceProductModalityDAO> productModalities = new ArrayList<>();
        InsuranceProductModalityDAO modality = new InsuranceProductModalityDAO();
        modality.setInsuranceCompanyModalityId("533625");
        modality.setInsuranceModalityName("PLAN BASICO");
        modality.setInsuranceModalityType("02");
        productModalities.add(modality);
        List<InsurancePlanDTO> plan = listInstallmentPlan.getPlansNamesAndRecommendedValuesAndInstallmentsPlans(productModalities, responseRimac, false, false, false);
    }
}