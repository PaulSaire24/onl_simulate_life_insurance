package com.bbva.rbvd.lib.r302.pattern.impl;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.pisd.dto.insurance.mock.MockDTO;
import com.bbva.pisd.lib.r350.PISDR350;
import com.bbva.rbvd.dto.lifeinsrc.dao.InsuranceProductModalityDAO;
import com.bbva.rbvd.dto.lifeinsrc.dao.ProductInformationDAO;
import com.bbva.rbvd.dto.lifeinsrc.mock.MockData;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;

import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDProperties;
import com.bbva.rbvd.lib.r301.RBVDR301;
import com.bbva.rbvd.lib.r302.Transfer.PayloadProperties;
import com.bbva.rbvd.lib.r302.service.api.ConsumerInternalService;
import com.bbva.rbvd.lib.r302.service.dao.IContractDAO;
import com.bbva.rbvd.lib.r302.service.dao.IModalitiesDAO;
import com.bbva.rbvd.lib.r302.service.dao.IProductDAO;
import com.bbva.rbvd.lib.r302.util.ValidationUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SimulationParameterTest {

    private SimulationParameter simulationParameter;
    private LifeSimulationDTO lifeSimulationInput;
    private MockData mockData;
    private RBVDR301 rbvdR301;
    private PISDR350 pisdR350;
    private ApplicationConfigurationService applicationConfigurationService;
    private ValidationUtil validationUtil;
    private String productId;
    private IProductDAO iProductDAO;
    private IContractDAO contractDAO;
    private ConsumerInternalService consumer;
    private BigDecimal insuranceProductId;
    private String customerId;
    private String plansPT;
    private String saleChannel;
    private IModalitiesDAO iModalitiesDAO;

    private Map<String, Object> responseQueryGetProductInformation;
    private Map<String, Object> responseQueryGetCumulus;

    private Map<String, Object> responseQueryModalitiesInformation;

    @Before
    public void setUp() throws Exception {

        mockData = MockData.getInstance();

        lifeSimulationInput = mockData.getInsuranceSimulationRequest();

        rbvdR301 = mock(RBVDR301.class);

        pisdR350 = mock(PISDR350.class);

        applicationConfigurationService = mock(ApplicationConfigurationService.class);

        validationUtil = mock(ValidationUtil.class);

        iProductDAO = mock(IProductDAO.class);

        contractDAO = mock(IContractDAO.class);

        consumer = mock(ConsumerInternalService.class);

        productId = "841";
        insuranceProductId = new BigDecimal(187.2);
        customerId = "customer30";
        plansPT = "Planes";
        saleChannel = "canal sale";

        iModalitiesDAO = mock(IModalitiesDAO.class);

        simulationParameter = new SimulationParameter(pisdR350, rbvdR301, lifeSimulationInput, applicationConfigurationService);

        responseQueryGetProductInformation = new HashMap<>();
        responseQueryGetCumulus = new HashMap<>();
        responseQueryModalitiesInformation = new HashMap<>();
    }


    @Test
    public void getPropertiesTest(){

        //GIVEN
        when(applicationConfigurationService.getProperty(anyString())).thenReturn("L").thenReturn("2345, 7869, 7978");

        //WHEN - CUADNO
        PayloadProperties properties = simulationParameter.getProperties(lifeSimulationInput);

        //THEN
        Assert.assertNotNull(properties);
        Assert.assertEquals("L", properties.getDocumentTypeId());
        Assert.assertEquals("DNI", properties.getDocumentTypeIdAsText());
        Assert.assertEquals(false, properties.getSegmentLifePlans().get(0));
    }


    //@Test
    public void getProductTest(){

        when(this.pisdR350.executeGetASingleRow(RBVDProperties.QUERY_GET_PRODUCT_INFORMATION.getValue(), new HashMap<>())).
                thenReturn(responseQueryGetProductInformation);

        ProductInformationDAO productInformationDAO = simulationParameter.getProduct(productId);

        Assert.assertNotNull(productInformationDAO);
    }

    //@Test
    public void getCumulosTest(){

        when(this.pisdR350.executeGetListASingleRow(RBVDProperties.QUERY_GET_INSURANCE_AMOUNT.getValue(), new HashMap<>())).
                thenReturn(responseQueryGetCumulus);

        BigDecimal cumulos = simulationParameter.getCumulos(insuranceProductId, customerId);

        Assert.assertNotNull(cumulos);
    }

    //@Test
    public void getCustomerTest(){

        CustomerListASO customerList = simulationParameter.getCustomer(customerId);

        Assert.assertNotNull(customerList);
    }

    //@Test
    public void getModalitiesTest(){

        when(this.pisdR350.executeGetListASingleRow(RBVDProperties.QUERY_GET_PRODUCT_MODALITIES_INFORMATION.getValue(), new HashMap<>())).
                thenReturn(responseQueryModalitiesInformation);

        List<InsuranceProductModalityDAO> listInsurance = simulationParameter.getModalities(plansPT, insuranceProductId, saleChannel);

        Assert.assertNotNull(listInsurance);
    }


}