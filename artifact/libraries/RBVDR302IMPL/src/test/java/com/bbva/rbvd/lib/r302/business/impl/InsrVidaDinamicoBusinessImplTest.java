package com.bbva.rbvd.lib.r302.business.impl;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.pisd.dto.insurance.bo.BirthDataBO;
import com.bbva.pisd.dto.insurance.bo.customer.CustomerBO;
import com.bbva.pisd.dto.insurance.mock.MockDTO;
import com.bbva.rbvd.dto.lifeinsrc.dao.InsuranceProductModalityDAO;
import com.bbva.rbvd.dto.lifeinsrc.dao.ProductInformationDAO;
import com.bbva.rbvd.dto.lifeinsrc.mock.MockData;
import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.InsuranceLifeSimulationBO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;
import com.bbva.rbvd.lib.r301.RBVDR301;
import com.bbva.rbvd.lib.r302.transfer.PayloadConfig;
import com.bbva.rbvd.lib.r302.transfer.PayloadProperties;
import com.bbva.rbvd.lib.r302.transfer.PayloadStore;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
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
    private CustomerListASO customerList;
    private ApplicationConfigurationService applicationConfigurationService;
    private LifeSimulationDTO requestInput;
    private MockDTO mockDTO;
    private MockData mockData;
    private PayloadProperties properties;
    private PayloadConfig payloadConfig;
    private InsuranceLifeSimulationBO responseRimac;

    @Before
    public void setup() throws IOException {
        applicationConfigurationService = mock(ApplicationConfigurationService.class);
        rbvdR301 = mock(RBVDR301.class);
        //responseTier = mockDTO.getTierMockResponse();
        mockData = MockData.getInstance();
        mockDTO = MockDTO.getInstance();
        customerList = mockDTO.getCustomerDataResponse();
        requestInput = mockData.getInsuranceSimulationRequest();
        insrVidaDinamicoBusiness = new InsrVidaDinamicoBusinessImpl(rbvdR301, applicationConfigurationService);
        responseRimac = mockData.getInsuranceRimacSimulationResponse();

        properties = new PayloadProperties();
        properties.setSegmentLifePlans(Arrays.asList(false,true,false));
        properties.setDocumentTypeId("L");
        properties.setDocumentTypeIdAsText("DNI");
        payloadConfig = new PayloadConfig();
        payloadConfig.setInput(requestInput);
        payloadConfig.setSumCumulus(new BigDecimal(4732));
        payloadConfig.setProperties(properties);
        payloadConfig.setCustomerListASO(getCustomerListASO());
        payloadConfig.setListInsuranceProductModalityDAO(getInsuranceProductModalitiDao());
        payloadConfig.setProductInformation(getProductInformationDAO());
    }

    @NotNull
    private static ProductInformationDAO getProductInformationDAO() {
        ProductInformationDAO productInformationDAO = new ProductInformationDAO();
        productInformationDAO.setInsuranceProductId(new BigDecimal(10));
        productInformationDAO.setInsuranceBusinessName("VIDADINAMICO");
        productInformationDAO.setInsuranceProductDescription("SEGURO VIDA DINAMICO");
        return productInformationDAO;
    }

    @NotNull
    private static List<InsuranceProductModalityDAO> getInsuranceProductModalitiDao(){
        List<InsuranceProductModalityDAO> lista = new ArrayList<>();
        InsuranceProductModalityDAO modalityDAO1 = new InsuranceProductModalityDAO("533726", "Plan 1", "01","N", new BigDecimal(1));
        InsuranceProductModalityDAO modalityDAO2 = new InsuranceProductModalityDAO("533741", "Plan 2", "02","N", new BigDecimal(2));
        lista.add(modalityDAO1);
        lista.add(modalityDAO2);
        return lista;
    }

    @NotNull
    private static CustomerListASO getCustomerListASO() {
        BirthDataBO birthDataBO = new BirthDataBO();
        birthDataBO.setBirthDate("1994-04-25");
        CustomerBO customerBO = new CustomerBO();
        List<CustomerBO> data = new ArrayList<>();
        customerBO.setBirthData(birthDataBO);
        data.add(customerBO);
        CustomerListASO customerListASO = new CustomerListASO();
        customerListASO.setData(data);
        return customerListASO;
    }

    @Test(expected = BusinessException.class)
    public void executeQuotationRimacServiceResultNull() {
    //given

        when(applicationConfigurationService.getProperty("IS_MOCK_MODIFY_QUOTATION_DYNAMIC")).thenReturn("N");
        when(rbvdR301.executeSimulationModificationRimacService(anyObject(), anyString(), anyString())).
                thenReturn(null);

    //when
        InsuranceLifeSimulationBO result = insrVidaDinamicoBusiness.executeQuotationRimacService(
                requestInput,
                "",
                customerList,
                BigDecimal.valueOf(1),
                new ArrayList<>()
                );
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
                "VIDADINAMICO",
                customerList,
                BigDecimal.valueOf(1),
                new ArrayList<>()
                );
    }

    @Test
    public void doDynamicLifeTestWithExecuteModifyQuotationRimacServiceOK(){

        when(applicationConfigurationService.getProperty("IS_MOCK_MODIFY_QUOTATION_DYNAMIC")).thenReturn("N");
        when(rbvdR301.executeSimulationModificationRimacService(anyObject(), anyString(), anyString())).thenReturn(responseRimac);

        PayloadStore response = insrVidaDinamicoBusiness.doDynamicLife(payloadConfig);

        Assert.assertNotNull(response);
    }

}