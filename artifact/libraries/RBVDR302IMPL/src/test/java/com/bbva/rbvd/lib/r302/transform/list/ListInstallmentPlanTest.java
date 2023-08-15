package com.bbva.rbvd.lib.r302.transform.list;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.rbvd.dto.lifeinsrc.commons.InsurancePlanDTO;
import com.bbva.rbvd.dto.lifeinsrc.dao.InsuranceProductModalityDAO;
import com.bbva.rbvd.dto.lifeinsrc.mock.MockData;
import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.InsuranceLifeSimulationBO;
import com.bbva.rbvd.lib.r302.transform.list.impl.ListInstallmentPlanDynamicLife;
import com.bbva.rbvd.lib.r302.transform.list.impl.ListInstallmentPlanEasyYes;
import com.google.common.primitives.Booleans;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class ListInstallmentPlanTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ListInstallmentPlanTest.class);

    private IListInstallmentPlan iListInstallmentPlan;
    @Mock
    private ApplicationConfigurationService applicationConfigurationService;
    private List<InsuranceProductModalityDAO> productModalities;
    private InsuranceLifeSimulationBO responseRimac;

    private MockData mockData;

    @Before
    public void setUp() throws Exception{
        MockitoAnnotations.initMocks(this);
        applicationConfigurationService = mock(ApplicationConfigurationService.class);
        mockData = MockData.getInstance();
        responseRimac = mockData.getInsuranceRimacSimulationResponse();
        productModalities = new ArrayList<>();

    }
    @Test
    public void listInstallmentPlanSuccess(){
        //Agregar comportamientos correctos
        LOGGER.info("ListInstallmentPlanTest - Executing listInstallmentPlanSuccess...");


        when(applicationConfigurationService.getProperty(anyString())).thenReturn(null);

        boolean[] arr = { true, true, true };
        List<Boolean> segPlans = Booleans.asList(arr);
        iListInstallmentPlan = new ListInstallmentPlanEasyYes(applicationConfigurationService);
        List<InsurancePlanDTO> plans = iListInstallmentPlan.getPlansNamesAndRecommendedValuesAndInstallmentsPlans(productModalities,responseRimac,segPlans);
        Assert.assertNotNull(plans);
    }

    /*
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

    }*/

    @Test
    public void listInstallmentPlanNullTest() {
        //Agregar comportamientos para el null test
        LOGGER.info("ListInstallmentPlanTest - Executing listInstallmentPlanNullTest...");
        List<InsuranceProductModalityDAO> productModalities = new ArrayList<>();
        InsuranceProductModalityDAO modality = new InsuranceProductModalityDAO();
        modality.setInsuranceCompanyModalityId("533625");
        modality.setInsuranceModalityName("PLAN BASICO");
        modality.setInsuranceModalityType("02");
        productModalities.add(modality);
        boolean[] arr = { false, false, false};
        List<Boolean> segPlans = Booleans.asList(arr);
        iListInstallmentPlan = new ListInstallmentPlanEasyYes(applicationConfigurationService);
        List<InsurancePlanDTO> plan = iListInstallmentPlan.getPlansNamesAndRecommendedValuesAndInstallmentsPlans(productModalities, responseRimac, segPlans);

        Assert.assertNotNull(plan);
    }

    @Test
    public void getPlansNamesAndRecommendedValuesAndInstallmentsPlansFullTest() throws IOException {

        when(applicationConfigurationService.getProperty(anyString())).thenReturn("L");

        InsuranceLifeSimulationBO responseRimac = mockData.getInsuranceRimacSimulationResponse();

        responseRimac.getPayload().getCotizaciones().get(0).setIndicadorBloqueo(1L);

        responseRimac.getPayload().getCotizaciones().get(0).getPlan().getCoberturas().get(0).setSumaAsegurada(BigDecimal.valueOf(2500));
        responseRimac.getPayload().getCotizaciones().get(0).getPlan().getCoberturas().get(0).setMoneda("PEN");

        responseRimac.getPayload().getCotizaciones().get(0).getPlan().getCoberturas().get(0).setSumaAseguradaMinima(BigDecimal.valueOf(1000));
        responseRimac.getPayload().getCotizaciones().get(0).getPlan().getCoberturas().get(0).setSumaAseguradaMaxima(BigDecimal.valueOf(5000));

        List<InsuranceProductModalityDAO> productModalities = new ArrayList<>();
        InsuranceProductModalityDAO modality = new InsuranceProductModalityDAO();
        modality.setInsuranceCompanyModalityId("533629");
        modality.setInsuranceModalityName("PLAN 01 EASY YES");
        modality.setInsuranceModalityType("01");
        productModalities.add(modality);

        boolean[] arr = { true, false, false};
        List<Boolean> segPlans = Booleans.asList(arr);
        iListInstallmentPlan = new ListInstallmentPlanEasyYes(applicationConfigurationService);
        List<InsurancePlanDTO> validation = iListInstallmentPlan.getPlansNamesAndRecommendedValuesAndInstallmentsPlans(productModalities, responseRimac, segPlans);

        Assert.assertNotNull(validation.get(0).getName());

        responseRimac.getPayload().getCotizaciones().get(0).setIndicadorBloqueo(0L);
        responseRimac.getPayload().getCotizaciones().get(0).getPlan().getCoberturas().get(0).setCondicion("INC");
        productModalities.get(0).setInsuranceModalityType("02");
        segPlans.set(0,false);
        segPlans.set(1,true);
        validation = iListInstallmentPlan.getPlansNamesAndRecommendedValuesAndInstallmentsPlans(productModalities, responseRimac, segPlans);

        Assert.assertEquals(1, validation.size());

        responseRimac.getPayload().getCotizaciones().get(0).getPlan().getCoberturas().get(0).setCondicion("OPC");
        productModalities.get(0).setInsuranceModalityType("03");
        segPlans.set(1,false);
        segPlans.set(2,true);
        validation = iListInstallmentPlan.getPlansNamesAndRecommendedValuesAndInstallmentsPlans(productModalities, responseRimac, segPlans);

        Assert.assertEquals(1, validation.size());

        responseRimac.getPayload().getCotizaciones().get(0).getPlan().getCoberturas().get(0).setCondicion("");
        productModalities.get(0).setInsuranceModalityType("03");
        segPlans.set(2,false);
        validation = iListInstallmentPlan.getPlansNamesAndRecommendedValuesAndInstallmentsPlans(productModalities, responseRimac, segPlans);

        Assert.assertEquals(1, validation.size());

        responseRimac.getPayload().getCotizaciones().get(0).getPlan().setCoberturas(new ArrayList<>());
        productModalities.get(0).setInsuranceModalityType("02");
        validation = iListInstallmentPlan.getPlansNamesAndRecommendedValuesAndInstallmentsPlans(productModalities, responseRimac, segPlans);

        Assert.assertEquals(1, validation.size());

    }

    @Test
    public void getPlansNamesAndRecommendedValuesAndInstallmentsPlansNullTest() {

        List<InsuranceProductModalityDAO> productModalities = new ArrayList<>();
        InsuranceProductModalityDAO modality = new InsuranceProductModalityDAO();
        modality.setInsuranceCompanyModalityId("533625");
        modality.setInsuranceModalityName("PLAN BASICO");
        modality.setInsuranceModalityType("02");
        productModalities.add(modality);

        boolean[] arr = { false, false, false};
        List<Boolean> segPlans = Booleans.asList(arr);
        iListInstallmentPlan = new ListInstallmentPlanEasyYes(applicationConfigurationService);
        List<InsurancePlanDTO> validation = iListInstallmentPlan.getPlansNamesAndRecommendedValuesAndInstallmentsPlans(productModalities, responseRimac, segPlans);

        Assert.assertNotNull(validation);
    }

    @Test
    public void testgetPlansNamesAndRecommendedValuesAndInstallmentsPlansInDynamicLife() throws IOException{
        LOGGER.info("ListInstallmentPlanTest - Executing testgetPlansNamesAndRecommendedValuesAndInstallmentsPlansInDynamicLife...");

        responseRimac.getPayload().setProducto("VIDADINAMICO");
        responseRimac.getPayload().getCotizaciones().get(0).setIndicadorBloqueo(Long.parseLong("0"));
        responseRimac.getPayload().getCotizaciones().get(0).getPlan().setPlan(Long.parseLong("533726"));
        productModalities = mockData.getInsuranceProductModalitiesDAO();

        iListInstallmentPlan = new ListInstallmentPlanDynamicLife(applicationConfigurationService);
        List<InsurancePlanDTO> plans = iListInstallmentPlan.getPlansNamesAndRecommendedValuesAndInstallmentsPlans(productModalities,responseRimac,new ArrayList<>());

        Assert.assertNotNull(plans);

        //Coberturas adicionales

        responseRimac.getPayload().getCotizaciones().get(0).getPlan().getCoberturas().get(0).setSumaAsegurada(BigDecimal.valueOf(2500));
        responseRimac.getPayload().getCotizaciones().get(0).getPlan().getCoberturas().get(0).setMoneda("PEN");

        responseRimac.getPayload().getCotizaciones().get(0).getPlan().getCoberturas().get(0).setSumaAseguradaMinima(BigDecimal.valueOf(1000));
        responseRimac.getPayload().getCotizaciones().get(0).getPlan().getCoberturas().get(0).setSumaAseguradaMaxima(BigDecimal.valueOf(5000));

        plans = iListInstallmentPlan.getPlansNamesAndRecommendedValuesAndInstallmentsPlans(productModalities,responseRimac,new ArrayList<>());
        Assert.assertNotNull(plans);
        Assert.assertNotNull(plans.get(0).getCoverages());
        Assert.assertNotNull(plans.get(0).getCoverages().get(0).getCoverageLimits());
        Assert.assertNotNull(plans.get(0).getCoverages().get(0).getInsuredAmount());
        Assert.assertNotNull(plans.get(0).getCoverages().get(0).getFeePaymentAmount());


    }

}