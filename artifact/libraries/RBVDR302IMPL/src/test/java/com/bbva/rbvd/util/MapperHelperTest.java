package com.bbva.rbvd.util;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.rbvd.dto.lifeinsrc.commons.InsurancePlanDTO;
import com.bbva.rbvd.dto.lifeinsrc.dao.InsuranceProductModalityDAO;
import com.bbva.rbvd.dto.lifeinsrc.dao.SimulationDAO;
import com.bbva.rbvd.dto.lifeinsrc.dao.SimulationProductDAO;
import com.bbva.rbvd.dto.lifeinsrc.mock.MockData;
import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.InsuranceLifeSimulationBO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDProperties;
import com.bbva.rbvd.lib.r302.impl.util.MapperHelper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MapperHelperTest {

    private final MapperHelper mapperHelper = new MapperHelper();

    private ApplicationConfigurationService applicationConfigurationService;

    private MockData mockDTO;

    private LifeSimulationDTO requestInput;

    private LifeSimulationDTO responseOut;

    private SimulationDAO simulationDAO;

    private InsuranceLifeSimulationBO responseRimac;

    private SimulationProductDAO simulationProductDAO;

    @Before
    public void setUp() throws Exception {

        applicationConfigurationService = mock(ApplicationConfigurationService.class);

        mockDTO = MockData.getInstance();


        requestInput = mockDTO.getInsuranceSimulationRequest();
        responseOut = mockDTO.getInsuranceSimulationResponse();
        responseRimac = mockDTO.getInsuranceRimacSimulationResponse();

        mapperHelper.setApplicationConfigurationService(applicationConfigurationService);

        simulationDAO = mock(SimulationDAO.class);

        simulationProductDAO = mock(SimulationProductDAO.class);

    }

    @Test
    public void  mapInRequestDocument_OKTest(){
        when(applicationConfigurationService.getProperty(anyString())).thenReturn("L");
        InsuranceLifeSimulationBO request = this.mapperHelper.mapInRequestRimacLife(requestInput);
        assertNotNull(request);
    }

    @Test
    public void mapProductIdOKTest() {

        Map<String, Object> mapStringObject = mapperHelper.mapProductId("834");
        assertEquals("834", mapStringObject.get(RBVDProperties.FILTER_INSURANCE_PRODUCT_TYPE.getValue()));

    }

    @Test
    public void getPlansNamesAndRecommendedValuesAndInstallmentsPlansFullTest() throws IOException {

        InsuranceLifeSimulationBO responseRimac = mockDTO.getInsuranceRimacSimulationResponse();
        List<InsuranceProductModalityDAO> productModalities = new ArrayList<>();
        InsuranceProductModalityDAO modality = new InsuranceProductModalityDAO();
        modality.setInsuranceCompanyModalityId("533629");
        modality.setInsuranceModalityName("PLAN BASICO");
        modality.setInsuranceModalityType("02");
        productModalities.add(modality);

        List<InsurancePlanDTO> validation = mapperHelper.getPlansNamesAndRecommendedValuesAndInstallmentsPlans(productModalities, responseRimac);

        assertNotNull(validation.get(0).getName());

        validation = mapperHelper.getPlansNamesAndRecommendedValuesAndInstallmentsPlans(productModalities, responseRimac);

        assertEquals(1, validation.size());

    }

    @Test
    public void getPlansNamesAndRecommendedValuesAndInstallmentsPlansNullTest() throws IOException {


        List<InsuranceProductModalityDAO> productModalities = new ArrayList<>();
        InsuranceProductModalityDAO modality = new InsuranceProductModalityDAO();
        modality.setInsuranceCompanyModalityId("533625");
        modality.setInsuranceModalityName("PLAN BASICO");
        modality.setInsuranceModalityType("02");
        productModalities.add(modality);

        List<InsurancePlanDTO> validation = mapperHelper.getPlansNamesAndRecommendedValuesAndInstallmentsPlans(productModalities, responseRimac);

    }

    @Test
    public void createArgumentsForSaveSimulationOKTest() throws IOException {
        when(simulationDAO.getInsrncCompanySimulationId()).thenReturn("c9debdc9-d7e1-4464-8b3a-990c17eb9f48");
        when(simulationDAO.getCustomerId()).thenReturn("77712345");
        when(simulationDAO.getCustomerSimulationDate()).thenReturn("01/03/2021");
        when(simulationDAO.getCustSimulationExpiredDate()).thenReturn("29/03/2021");
        when(simulationDAO.getBankFactorType()).thenReturn("C");
        when(simulationDAO.getBankFactorAmount()).thenReturn(new BigDecimal(0));
        when(simulationDAO.getBankFactorPer()).thenReturn(new BigDecimal(0.15));
        when(simulationDAO.getSourceBranchId()).thenReturn("0814");

        Map<String, Object> validation = mapperHelper.createArgumentsForSaveSimulation(simulationDAO, "user01", "user02", "DNI");

        assertNotNull(validation.get(RBVDProperties.FIELD_INSRNC_COMPANY_SIMULATION_ID.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_CUSTOMER_ID.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_CUSTOMER_SIMULATION_DATE.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_CUST_SIMULATION_EXPIRED_DATE.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_BANK_FACTOR_TYPE.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_BANK_FACTOR_AMOUNT.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_BANK_FACTOR_PER.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_SOURCE_BRANCH_ID.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_CREATION_USER_ID.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_USER_AUDIT_ID.getValue()));
    }

    @Test
    public void createSimulationProductDAO_OKTest() {

        SimulationProductDAO validation = mapperHelper.createSimulationProductDAO(new BigDecimal("1"), new BigDecimal("827"), "user01", "user02", responseOut);

        assertNotNull(validation.getInsuranceSimulationId());
        assertNotNull(validation.getInsuranceProductId());
        assertNull(validation.getCampaignFactorType());
        assertNotNull(validation.getCampaignOffer1Amount());
        assertNotNull(validation.getCampaignFactorPer());
        assertNotNull(validation.getCreationUser());
        assertNotNull(validation.getUserAudit());
        assertEquals(new BigDecimal("1"), validation.getInsuranceSimulationId());
        assertEquals(new BigDecimal("827"), validation.getInsuranceProductId());
        assertEquals(new BigDecimal("0"), validation.getCampaignOffer1Amount());
        assertEquals(new BigDecimal("0"), validation.getCampaignFactorPer());
    }

    @Test
    public void putConsiderationsFullTest() throws IOException {

        mapperHelper.putConsiderations(responseOut.getProduct().getPlans(), responseRimac.getPayload().getCotizaciones());

        assertNotNull(responseOut.getProduct().getPlans().get(0).getCoverages());
    }

    @Test
    public void createSimulationDAOWithAllFactorTypeTest() {

        SimulationDAO validation = mapperHelper.createSimulationDAO(new BigDecimal("14"),  responseOut);

        assertNotNull(validation.getInsuranceSimulationId());

    }

    @Test
    public void createArgumentsForSaveSimulationProductOKTest() throws IOException {
        when(simulationProductDAO.getInsuranceSimulationId()).thenReturn(new BigDecimal("14"));
        when(simulationProductDAO.getInsuranceProductId()).thenReturn(new BigDecimal("827"));
        when(simulationProductDAO.getCampaignOffer1Amount()).thenReturn(new BigDecimal("0"));
        when(simulationProductDAO.getCampaignFactorPer()).thenReturn(new BigDecimal("0"));
        when(simulationProductDAO.getSaleChannelId()).thenReturn("channel_code");
        when(simulationProductDAO.getSourceBranchId()).thenReturn("0814");
        when(simulationProductDAO.getCreationUser()).thenReturn("user01");
        when(simulationProductDAO.getUserAudit()).thenReturn("user02");

        Map<String, Object> validation = mapperHelper.createArgumentsForSaveSimulationProduct(simulationProductDAO);
        assertNotNull(validation.get(RBVDProperties.FIELD_INSURANCE_SIMULATION_ID.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_OR_FILTER_INSURANCE_PRODUCT_ID.getValue()));
        assertNull(validation.get(RBVDProperties.FIELD_CAMPAIGN_FACTOR_TYPE.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_CAMPAIGN_OFFER_1_AMOUNT.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_CAMPAIGN_FACTOR_PER.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_SALE_CHANNEL_ID.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_SOURCE_BRANCH_ID.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_CREATION_USER_ID.getValue()));
        assertNotNull(validation.get(RBVDProperties.FIELD_USER_AUDIT_ID.getValue()));
    }

    @Test
    public void insuranceProductModalityFiltersCreationOKTest() {

        BigDecimal productId = new BigDecimal("8");
        String saleChannel = "PC";

        Map<String, Object> validation = mapperHelper.createModalitiesInformationFilters("1234,567", productId, saleChannel);
        List<String> modalityTypes = (List<String>) validation.get(RBVDProperties.FIELD_OR_FILTER_INSURANCE_MODALITY_TYPE.getValue());
        assertNotNull(validation.get(RBVDProperties.FIELD_OR_FILTER_INSURANCE_PRODUCT_ID.getValue()));
        assertNotNull(modalityTypes);
        assertNotNull(validation.get(RBVDProperties.FIELD_SALE_CHANNEL_ID.getValue()));
        assertEquals(productId, validation.get(RBVDProperties.FIELD_OR_FILTER_INSURANCE_PRODUCT_ID.getValue()));

        assertEquals(saleChannel, validation.get(RBVDProperties.FIELD_SALE_CHANNEL_ID.getValue()));
    }


    @Test
    public void insuranceProductModalityFiltersCreationOK() {
        mapperHelper.mapOutRequestRimacLife(responseRimac, responseOut);
        assertNotNull(responseOut.getProduct().getName());

    }


}
