package com.bbva.rbvd.util;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.pisd.dto.insurance.aso.gifole.GifoleInsuranceRequestASO;
import com.bbva.pisd.dto.insurance.aso.tier.TierASO;
import com.bbva.pisd.dto.insurance.aso.tier.TierDataASO;
import com.bbva.pisd.dto.insurance.aso.tier.TierSegmentASO;
import com.bbva.pisd.dto.insurance.mock.MockDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.InsurancePlanDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.PeriodDTO;
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
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MapperHelperTest {

    private final MapperHelper mapperHelper = new MapperHelper();

    private ApplicationConfigurationService applicationConfigurationService;

    private MockData mockData;

    private LifeSimulationDTO requestInput;

    private LifeSimulationDTO responseOut;

    private SimulationDAO simulationDAO;

    private InsuranceLifeSimulationBO responseRimac;

    private SimulationProductDAO simulationProductDAO;

    private MockDTO mockDTO;

    private CustomerListASO requestCustomerListASO;

    private BigDecimal sumCumulus;

    @Before
    public void setUp() throws Exception {

        applicationConfigurationService = mock(ApplicationConfigurationService.class);

        mockData = MockData.getInstance();

        mockDTO = MockDTO.getInstance();

        requestInput = mockData.getInsuranceSimulationRequest();
        responseOut = mockData.getInsuranceSimulationResponse();
        responseRimac = mockData.getInsuranceRimacSimulationResponse();

        mapperHelper.setApplicationConfigurationService(applicationConfigurationService);

        simulationDAO = mock(SimulationDAO.class);
        simulationDAO.setInsuranceModalityType("03");

        simulationProductDAO = mock(SimulationProductDAO.class);

        requestCustomerListASO = new CustomerListASO();

        sumCumulus = BigDecimal.ZERO;

    }

    @Test
    public void  mapInRequestDocument_OKTest(){
        when(applicationConfigurationService.getProperty(anyString())).thenReturn("L");
        InsuranceLifeSimulationBO request = this.mapperHelper.mapInRequestRimacLife(requestInput, sumCumulus);
        assertNotNull(request);

        request = this.mapperHelper.mapInRequestRimacLife(requestInput, null);
        assertNotNull(request);
    }

    @Test
    public void mapProductIdOKTest() {
        Map<String, Object> mapStringObject = this.mapperHelper.mapProductId("840");
        assertEquals("840", mapStringObject.get(RBVDProperties.FILTER_INSURANCE_PRODUCT_TYPE.getValue()));

    }

    @Test
    public void mapInsuranceAmountOKTest() {
        Map<String, Object> mapStringObject = this.mapperHelper.mapInsuranceAmount(new BigDecimal(8), "0000000");
        assertEquals(new BigDecimal(8), mapStringObject.get(RBVDProperties.FIELD_OR_FILTER_INSURANCE_PRODUCT_ID.getValue()));

    }

    @Test
    public void getPlansNamesAndRecommendedValuesAndInstallmentsPlansFullTest() throws IOException {

        InsuranceLifeSimulationBO responseRimac = mockData.getInsuranceRimacSimulationResponse();
        responseRimac.getPayload().getCotizaciones().get(0).setIndicadorBloqueo(1L);
        List<InsuranceProductModalityDAO> productModalities = new ArrayList<>();
        InsuranceProductModalityDAO modality = new InsuranceProductModalityDAO();
        modality.setInsuranceCompanyModalityId("533629");
        modality.setInsuranceModalityName("PLAN 01 EASY YES");
        modality.setInsuranceModalityType("01");
        productModalities.add(modality);

        List<InsurancePlanDTO> validation = mapperHelper.getPlansNamesAndRecommendedValuesAndInstallmentsPlans(productModalities, responseRimac, true, false, false);

        assertNotNull(validation.get(0).getName());

        responseRimac.getPayload().getCotizaciones().get(0).setIndicadorBloqueo(0L);
        responseRimac.getPayload().getCotizaciones().get(0).getPlan().getCoberturas().get(0).setCondicion("INC");
        productModalities.get(0).setInsuranceModalityType("02");
        validation = mapperHelper.getPlansNamesAndRecommendedValuesAndInstallmentsPlans(productModalities, responseRimac, false, true, false);

        assertEquals(1, validation.size());

        responseRimac.getPayload().getCotizaciones().get(0).getPlan().getCoberturas().get(0).setCondicion("OPC");
        productModalities.get(0).setInsuranceModalityType("03");
        validation = mapperHelper.getPlansNamesAndRecommendedValuesAndInstallmentsPlans(productModalities, responseRimac, false, false, true);

        assertEquals(1, validation.size());

        responseRimac.getPayload().getCotizaciones().get(0).getPlan().getCoberturas().get(0).setCondicion("");
        productModalities.get(0).setInsuranceModalityType("03");
        validation = mapperHelper.getPlansNamesAndRecommendedValuesAndInstallmentsPlans(productModalities, responseRimac, false, false, false);

        assertEquals(1, validation.size());

        responseRimac.getPayload().getCotizaciones().get(0).getPlan().setCoberturas(new ArrayList<>());
        productModalities.get(0).setInsuranceModalityType("02");
        validation = mapperHelper.getPlansNamesAndRecommendedValuesAndInstallmentsPlans(productModalities, responseRimac, false, false, false);

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

        List<InsurancePlanDTO> validation = mapperHelper.getPlansNamesAndRecommendedValuesAndInstallmentsPlans(productModalities, responseRimac, false, false, false);

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
    public void createSimulationDAOWithAllFactorTypeTest() {

        SimulationDAO validation = mapperHelper.createSimulationDAO(new BigDecimal("14"),  new Date(),responseOut);

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

    @Test
    public void createGifoleASO_OK() throws IOException {

        requestCustomerListASO = mockDTO.getCustomerDataResponse();
        GifoleInsuranceRequestASO validation = this.mapperHelper.createGifoleASO(responseOut, requestCustomerListASO);

        assertNotNull(validation);

        requestCustomerListASO.getData().get(0).setFirstName("null");
        validation = this.mapperHelper.createGifoleASO(responseOut, requestCustomerListASO);

        assertNotNull(validation);

        requestCustomerListASO.getData().get(0).setFirstName(null);
        validation = this.mapperHelper.createGifoleASO(responseOut, requestCustomerListASO);

        assertNotNull(validation);

        requestCustomerListASO.getData().get(0).setFirstName(" ");
        responseOut.getHolder().getIdentityDocument().setDocumentNumber(null);
        validation = this.mapperHelper.createGifoleASO(responseOut, requestCustomerListASO);

        assertNotNull(validation);
    }

    @Test
    public void createGifoleASO_NULL() {

        responseOut.getProduct().getPlans().get(2).getInstallmentPlans().get(1).setPeriod(new PeriodDTO());
        GifoleInsuranceRequestASO validation = this.mapperHelper.createGifoleASO(responseOut, null);

        assertNotNull(validation);
    }

    @Test
    public void mappingTierASOOK() {
        TierASO tierASO = new TierASO();
        TierDataASO data = new TierDataASO();
        TierSegmentASO segmento = new TierSegmentASO();
        List<TierSegmentASO> listTierSegmentASO = new ArrayList<>();
        data.setId("0001");
        data.setDescription("TIER 1");
        data.setChargeFactor(15.0000);
        segmento.setDescription("testdesc");
        listTierSegmentASO.add(segmento);
        data.setSegments(listTierSegmentASO);
        tierASO.setData(Collections.singletonList(data));

        LifeSimulationDTO insuranceSimulationDTO = new LifeSimulationDTO();
        mapperHelper.mappingTierASO(insuranceSimulationDTO, tierASO);
        assertNotNull(insuranceSimulationDTO.getTier());
        assertNotNull(insuranceSimulationDTO.getTier().getId());
        assertNotNull(insuranceSimulationDTO.getTier().getName());
        assertNotNull(insuranceSimulationDTO.getBankingFactor());
    }

    @Test
    public void mappingTierSegmentNull() {
        TierASO tierASO = new TierASO();
        TierDataASO data = new TierDataASO();
        TierSegmentASO segmento = new TierSegmentASO();
        List<TierSegmentASO> listTierSegmentASO = new ArrayList<>();
        data.setId("0001");
        data.setDescription("TIER 1");
        data.setChargeFactor(15.0000);
        segmento.setId("86300");
        listTierSegmentASO.add(segmento);
        data.setSegments(null);
        tierASO.setData(Collections.singletonList(data));

        LifeSimulationDTO insuranceSimulationDTO = new LifeSimulationDTO();
        mapperHelper.mappingTierASO(insuranceSimulationDTO, tierASO);
        assertNotNull(insuranceSimulationDTO.getTier());
        assertNotNull(insuranceSimulationDTO.getTier().getId());
        assertNotNull(insuranceSimulationDTO.getTier().getName());
        assertNotNull(insuranceSimulationDTO.getBankingFactor());
    }

    @Test
    public void mappingTierASONULL() {

        LifeSimulationDTO insuranceSimulationDTO = new LifeSimulationDTO();
        mapperHelper.mappingTierASO(insuranceSimulationDTO, null);
        assertNull(insuranceSimulationDTO.getTier());
    }

    @Test
    public void selectValuePlansDescriptionTest() throws IOException {
        Boolean valor=false;
        LifeSimulationDTO input = new LifeSimulationDTO();
        input.setId("86300");
        String seglifePlan1 = "86300";

        valor = mapperHelper.selectValuePlansDescription(seglifePlan1, input);
        assertEquals(valor, true);

        input.setId(null);

        valor = mapperHelper.selectValuePlansDescription(seglifePlan1, input);
        assertEquals( valor, false);
        assertNull(input.getDescription());
    }

}
