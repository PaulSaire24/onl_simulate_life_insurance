package com.bbva.rbvd.lib.r302;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.elara.domain.transaction.Context;
import com.bbva.elara.domain.transaction.ThreadContext;

import com.bbva.pisd.dto.insurance.aso.crypto.CryptoASO;
import com.bbva.pisd.dto.insurance.aso.crypto.CryptoDataASO;
import com.bbva.pisd.dto.insurance.aso.gifole.GifoleInsuranceRequestASO;
import com.bbva.pisd.dto.insurance.aso.tier.TierASO;
import com.bbva.pisd.dto.insurance.mock.MockDTO;

import com.bbva.pisd.lib.r350.PISDR350;

import com.bbva.rbvd.dto.lifeinsrc.commons.TierDTO;
import com.bbva.rbvd.dto.lifeinsrc.mock.MockData;
import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.InsuranceLifeSimulationBO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDProperties;

import com.bbva.rbvd.lib.r301.RBVDR301;
import com.bbva.rbvd.lib.r302.impl.RBVDR302Impl;
import com.bbva.rbvd.lib.r302.impl.util.MapperHelper;
import org.apache.felix.resolver.util.ArrayMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
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
public class RBVDR302Test {

	@Spy
	private Context context;

	private final RBVDR302Impl rbvdR302 = new RBVDR302Impl();

	private ApplicationConfigurationService applicationConfigurationService;

	private RBVDR301 rbvdr301;

	private MockData mockData;

	private static final Logger LOGGER = LoggerFactory.getLogger(RBVDR302Test.class);

	private InsuranceLifeSimulationBO responseRimac;

	private MapperHelper mapperHelper;

	private InsuranceLifeSimulationBO requestRimac;

	private LifeSimulationDTO input;

	private LifeSimulationDTO requestInput;

	private LifeSimulationDTO responseInput;

	private PISDR350 pisdR350;

	private CryptoASO crypto;

	private TierASO tier;

	private MockDTO mockDTO;

	private Map<String, Object> responseQueryGetProductInformation;

	private Map<String, Object> responseQueryConsiderations;

	private Map<String, Object> responseQuerySumCumulus;

	private int executeInsertSingleRow;

	private GifoleInsuranceRequestASO gifoleInsReqAso;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		context = new Context();
		ThreadContext.set(context);

		mockData = MockData.getInstance();
		applicationConfigurationService = mock(ApplicationConfigurationService.class);

		rbvdr301 = mock(RBVDR301.class);
		rbvdR302.setRbvdR301(rbvdr301);

		responseRimac = mockData.getInsuranceRimacSimulationResponse();

		requestRimac = mockData.getInsuranceRimacSimulationRequest();

		requestInput = mockData.getInsuranceSimulationRequest();

		mapperHelper = mock(MapperHelper.class);
		rbvdR302.setMapperHelper(mapperHelper);

		rbvdR302.setApplicationConfigurationService(applicationConfigurationService);

		input = new LifeSimulationDTO();

		pisdR350 = mock(PISDR350.class);
		rbvdR302.setPisdR350(pisdR350);

		responseQueryGetProductInformation = mock(Map.class);
		responseQueryConsiderations = mock(Map.class);

		when(this.applicationConfigurationService.getProperty("ENABLE_GIFOLE_LIFE_ASO")).thenReturn("true");

		gifoleInsReqAso = new GifoleInsuranceRequestASO();

		gifoleInsReqAso.setChannel("Channel");
		gifoleInsReqAso.setOperationType("OperationType");
		gifoleInsReqAso.setOperationDate("OperationDate");
		gifoleInsReqAso.setPolicyNumber("PolicyNumber");

		when(this.applicationConfigurationService.getProperty("ENABLE_GIFOLE_LIFE_ASO")).thenReturn("true");


		gifoleInsReqAso = new GifoleInsuranceRequestASO();

		gifoleInsReqAso.setChannel("Channel");
		gifoleInsReqAso.setOperationType("OperationType");
		gifoleInsReqAso.setOperationDate("OperationDate");
		gifoleInsReqAso.setPolicyNumber("PolicyNumber");

		executeInsertSingleRow = 1;

		mockDTO = MockDTO.getInstance();
		crypto = new CryptoASO();
		CryptoDataASO data = new CryptoDataASO();
		crypto.setData(data);
		tier = mockDTO.getTierMockResponse();
	}

	@Test
	public void executeGetGenerateTest(){
		LOGGER.info("RBVDR302Test - Executing executeGetGenerateTest...");
		when(applicationConfigurationService.getProperty(anyString())).thenReturn("L");
		when(applicationConfigurationService.getProperty("ENABLE_GIFOLE_LIFE_ASO")).thenReturn("true");
		when(pisdR350.executeGetASingleRow(RBVDProperties.QUERY_GET_PRODUCT_INFORMATION.getValue(), new HashMap<>())).thenReturn(responseQueryGetProductInformation);
		responseQuerySumCumulus = new ArrayMap<>();
		List<Map<String, Object>> listResponse = new ArrayList<>();
		Map<String, Object> response = new HashMap<>();
		response.put("INSURED_AMOUNT", new BigDecimal(13.3));
		listResponse.add(response);
		responseQuerySumCumulus.put("dtoInsurance", listResponse);
		when(pisdR350.executeGetListASingleRow(RBVDProperties.QUERY_GET_INSURANCE_AMOUNT.getValue(), new HashMap<>())).thenReturn(responseQuerySumCumulus);
		when(pisdR350.executeGetListASingleRow(RBVDProperties.QUERY_GET_PRODUCT_MODALITIES_INFORMATION.getValue(), new HashMap<>())).thenReturn(responseQueryConsiderations);
		when(this.mapperHelper.mapInRequestRimacLife(anyObject(), anyObject())).thenReturn(requestRimac);
		when(this.rbvdr301.executeCryptoService(anyObject())).thenReturn(crypto);
		when(this.rbvdr301.executeGetTierService(anyObject())).thenReturn(tier);
		when(this.rbvdr301.executeSimulationRimacService(anyObject(), anyString())).thenReturn(responseRimac);

		when(this.rbvdr301.executeSimulationModificationRimacService(anyObject(), anyString(), anyString())).thenReturn(responseRimac);

		when(pisdR350.executeInsertSingleRow(RBVDProperties.QUERY_INSERT_INSURANCE_SIMULATION.getValue(), new HashMap<>())).thenReturn(executeInsertSingleRow);
		when(pisdR350.executeInsertSingleRow(RBVDProperties.QUERY_INSERT_INSRNC_SIMLT_PRD.getValue(), new HashMap<>())).thenReturn(executeInsertSingleRow);
		List<Map<String, Object>> responseConsiderations = new ArrayList<>();
		Map<String, Object> uniqueExample = new HashMap<>();
		uniqueExample.put(RBVDProperties.FIELD_OR_FILTER_INSURANCE_MODALITY_TYPE.getValue(), "01");
		responseConsiderations.add(uniqueExample);
		when(responseQueryConsiderations.get(RBVDProperties.KEY_OF_INSRC_LIST_RESPONSES.getValue())).
				thenReturn(responseConsiderations);
		when(this.mapperHelper.createGifoleASO(anyObject(), anyObject())).thenReturn(gifoleInsReqAso);
		when(this.rbvdr301.executeGifolelifeService(anyObject())).thenReturn(201);
		LifeSimulationDTO validation = this.rbvdR302.executeGetSimulation(requestInput);

		assertNotNull(validation);
	}

	@Test
	public void executeGetGenerateFalseTest(){
		LOGGER.info("RBVDR302Test - Executing executeGetGenerateFalseTest...");
		when(applicationConfigurationService.getProperty(anyString())).thenReturn("L");
		when(applicationConfigurationService.getProperty("ENABLE_GIFOLE_LIFE_ASO")).thenReturn("false");
		when(pisdR350.executeGetASingleRow(RBVDProperties.QUERY_GET_PRODUCT_INFORMATION.getValue(), new HashMap<>())).thenReturn(responseQueryGetProductInformation);
		responseQuerySumCumulus = new ArrayMap<>();
		when(pisdR350.executeGetListASingleRow(RBVDProperties.QUERY_GET_INSURANCE_AMOUNT.getValue(), new HashMap<>())).thenReturn(responseQuerySumCumulus);
		when(pisdR350.executeGetListASingleRow(RBVDProperties.QUERY_GET_PRODUCT_MODALITIES_INFORMATION.getValue(), new HashMap<>())).thenReturn(responseQueryConsiderations);
		when(this.mapperHelper.mapInRequestRimacLife(anyObject(), anyObject())).thenReturn(requestRimac);
		when(this.rbvdr301.executeCryptoService(anyObject())).thenReturn(crypto);
		when(this.rbvdr301.executeGetTierService(anyObject())).thenReturn(tier);
		when(this.rbvdr301.executeSimulationRimacService(anyObject(), anyString())).thenReturn(responseRimac);

		when(this.rbvdr301.executeSimulationModificationRimacService(anyObject(), anyString(), anyString())).thenReturn(responseRimac);

		when(pisdR350.executeInsertSingleRow(RBVDProperties.QUERY_INSERT_INSURANCE_SIMULATION.getValue(), new HashMap<>())).thenReturn(executeInsertSingleRow);
		when(pisdR350.executeInsertSingleRow(RBVDProperties.QUERY_INSERT_INSRNC_SIMLT_PRD.getValue(), new HashMap<>())).thenReturn(executeInsertSingleRow);
		List<Map<String, Object>> responseConsiderations = new ArrayList<>();
		Map<String, Object> uniqueExample = new HashMap<>();
		uniqueExample.put(RBVDProperties.FIELD_OR_FILTER_INSURANCE_MODALITY_TYPE.getValue(), "01");
		responseConsiderations.add(uniqueExample);
		when(responseQueryConsiderations.get(RBVDProperties.KEY_OF_INSRC_LIST_RESPONSES.getValue())).
				thenReturn(responseConsiderations);
		when(this.mapperHelper.createGifoleASO(anyObject(), anyObject())).thenReturn(gifoleInsReqAso);
		when(this.rbvdr301.executeGifolelifeService(anyObject())).thenReturn(201);
		LifeSimulationDTO validation = this.rbvdR302.executeGetSimulation(requestInput);

		assertNotNull(validation);
	}

	@Test
	public void executeGetGenerateInsertSimulationFailTest(){
		LOGGER.info("RBVDR302Test - Executing executeGetGenerateInsertSimulationFailTest...");
		when(applicationConfigurationService.getProperty(anyString())).thenReturn("L");
		when(applicationConfigurationService.getProperty("ENABLE_GIFOLE_LIFE_ASO")).thenReturn("true");
		when(pisdR350.executeGetASingleRow(RBVDProperties.QUERY_GET_PRODUCT_INFORMATION.getValue(), new HashMap<>())).thenReturn(responseQueryGetProductInformation);
		responseQuerySumCumulus = new ArrayMap<>();
		when(pisdR350.executeGetListASingleRow(RBVDProperties.QUERY_GET_INSURANCE_AMOUNT.getValue(), new HashMap<>())).thenReturn(responseQuerySumCumulus);
		when(pisdR350.executeGetListASingleRow(RBVDProperties.QUERY_GET_PRODUCT_MODALITIES_INFORMATION.getValue(), new HashMap<>())).thenReturn(responseQueryConsiderations);
		when(this.mapperHelper.mapInRequestRimacLife(anyObject(), anyObject())).thenReturn(requestRimac);
		when(this.rbvdr301.executeCryptoService(anyObject())).thenReturn(crypto);
		when(this.rbvdr301.executeGetTierService(anyObject())).thenReturn(tier);
		when(this.rbvdr301.executeSimulationRimacService(anyObject(), anyString())).thenReturn(responseRimac);
		when(pisdR350.executeInsertSingleRow(RBVDProperties.QUERY_INSERT_INSURANCE_SIMULATION.getValue(), new HashMap<>())).thenReturn(0);
		List<Map<String, Object>> responseConsiderations = new ArrayList<>();
		Map<String, Object> uniqueExample = new HashMap<>();
		uniqueExample.put(RBVDProperties.FIELD_OR_FILTER_INSURANCE_MODALITY_TYPE.getValue(), "01");
		responseConsiderations.add(uniqueExample);
		when(responseQueryConsiderations.get(RBVDProperties.KEY_OF_INSRC_LIST_RESPONSES.getValue())).
				thenReturn(responseConsiderations);
		when(this.mapperHelper.createGifoleASO(anyObject(), anyObject())).thenReturn(gifoleInsReqAso);
		when(this.rbvdr301.executeGifolelifeService(anyObject())).thenReturn(201);
		LifeSimulationDTO validation = this.rbvdR302.executeGetSimulation(requestInput);

		assertNull(validation);
	}

	@Test
	public void executeGetGenerateGiFoleFalseTest(){
		LOGGER.info("RBVDR302Test - Executing executeGetGenerateGiFoleFalseTest...");
		when(applicationConfigurationService.getProperty(anyString())).thenReturn("L");
		when(applicationConfigurationService.getProperty("ENABLE_GIFOLE_LIFE_ASO")).thenReturn("false");
		when(pisdR350.executeGetASingleRow(RBVDProperties.QUERY_GET_PRODUCT_INFORMATION.getValue(), new HashMap<>())).thenReturn(responseQueryGetProductInformation);
		responseQuerySumCumulus = new ArrayMap<>();
		when(pisdR350.executeGetListASingleRow(RBVDProperties.QUERY_GET_INSURANCE_AMOUNT.getValue(), new HashMap<>())).thenReturn(responseQuerySumCumulus);
		when(pisdR350.executeGetListASingleRow(RBVDProperties.QUERY_GET_PRODUCT_MODALITIES_INFORMATION.getValue(), new HashMap<>())).thenReturn(responseQueryConsiderations);
		when(this.mapperHelper.mapInRequestRimacLife(anyObject(), anyObject())).thenReturn(requestRimac);
		when(this.rbvdr301.executeCryptoService(anyObject())).thenReturn(crypto);
		when(this.rbvdr301.executeGetTierService(anyObject())).thenReturn(tier);
		when(this.rbvdr301.executeSimulationRimacService(anyObject(), anyString())).thenReturn(responseRimac);
		when(pisdR350.executeInsertSingleRow(RBVDProperties.QUERY_INSERT_INSURANCE_SIMULATION.getValue(), new HashMap<>())).thenReturn(executeInsertSingleRow);
		when(responseQueryConsiderations.get(RBVDProperties.KEY_OF_INSRC_LIST_RESPONSES.getValue())).
				thenReturn(new ArrayList<>());
		when(this.mapperHelper.createGifoleASO(anyObject(), anyObject())).thenReturn(gifoleInsReqAso);
		when(this.rbvdr301.executeGifolelifeService(anyObject())).thenReturn(201);
		requestInput.setTier(new TierDTO());
		LifeSimulationDTO validation = this.rbvdR302.executeGetSimulation(requestInput);

		assertNull(validation);
	}

	@Test
	public void executeGetNoSaveSimulationTest(){
		LOGGER.info("RBVDR302Test - Executing executeGetNoSaveSimulationTest...");
		when(applicationConfigurationService.getProperty(anyString())).thenReturn("L");
		when(pisdR350.executeGetASingleRow(RBVDProperties.QUERY_GET_PRODUCT_INFORMATION.getValue(), new HashMap<>())).thenReturn(responseQueryGetProductInformation);
		responseQuerySumCumulus = new ArrayMap<>();
		when(pisdR350.executeGetListASingleRow(RBVDProperties.QUERY_GET_INSURANCE_AMOUNT.getValue(), new HashMap<>())).thenReturn(responseQuerySumCumulus);
		when(pisdR350.executeGetListASingleRow(RBVDProperties.QUERY_GET_PRODUCT_MODALITIES_INFORMATION.getValue(), new HashMap<>())).thenReturn(responseQueryConsiderations);
		when(this.mapperHelper.mapInRequestRimacLife(anyObject(), anyObject())).thenReturn(requestRimac);
		when(this.rbvdr301.executeSimulationRimacService(anyObject(), anyString())).thenReturn(responseRimac);
		when(this.rbvdr301.executeCryptoService(anyObject())).thenReturn(crypto);
		when(this.rbvdr301.executeGetTierService(anyObject())).thenReturn(tier);
		when(pisdR350.executeInsertSingleRow(RBVDProperties.QUERY_INSERT_INSURANCE_SIMULATION.getValue(), new HashMap<>())).thenReturn(0);
		LifeSimulationDTO validation = this.rbvdR302.executeGetSimulation(requestInput);

		assertNull(validation);
	}

	@Test
	public void executeGetGenerateModalityNullTest(){
		LOGGER.info("RBVDR302Test - Executing executeGetGenerateModalityNullTest...");
		when(applicationConfigurationService.getProperty(anyString())).thenReturn("L");
		responseQuerySumCumulus = new ArrayMap<>();
		List<Map<String, Object>> listResponses = new ArrayList<>();
		responseQuerySumCumulus.put("dtoInsurance", listResponses);
		when(pisdR350.executeGetASingleRow(RBVDProperties.QUERY_GET_PRODUCT_INFORMATION.getValue(), new HashMap<>())).thenReturn(responseQueryGetProductInformation);
		when(pisdR350.executeGetListASingleRow(RBVDProperties.QUERY_GET_INSURANCE_AMOUNT.getValue(), new HashMap<>())).thenReturn(responseQuerySumCumulus);
		when(responseQueryConsiderations.get(RBVDProperties.KEY_OF_INSRC_LIST_RESPONSES.getValue())).
				thenReturn(null);
		when(pisdR350.executeGetListASingleRow(RBVDProperties.QUERY_GET_PRODUCT_MODALITIES_INFORMATION.getValue(), new HashMap<>())).thenReturn(responseQueryConsiderations);
		when(this.mapperHelper.mapInRequestRimacLife(anyObject(), anyObject())).thenReturn(requestRimac);
		when(this.rbvdr301.executeSimulationRimacService(anyObject(), anyString())).thenReturn(responseRimac);
		when(this.rbvdr301.executeCryptoService(anyObject())).thenReturn(crypto);
		when(this.rbvdr301.executeGetTierService(anyObject())).thenReturn(tier);
		when(pisdR350.executeInsertSingleRow(RBVDProperties.QUERY_INSERT_INSURANCE_SIMULATION.getValue(), new HashMap<>())).thenReturn(executeInsertSingleRow);
		LifeSimulationDTO validation = this.rbvdR302.executeGetSimulation(requestInput);

		assertNull(validation);
	}

	@Test
	public void executeGetGenerateProductNullTest(){
		LOGGER.info("RBVDR302Test - Executing executeGetGenerateProductNullTest...");
		when(applicationConfigurationService.getProperty(anyString())).thenReturn("L");
		when(pisdR350.executeGetASingleRow(RBVDProperties.QUERY_GET_PRODUCT_INFORMATION.getValue(), new HashMap<>())).thenReturn(null);
		when(pisdR350.executeGetListASingleRow(RBVDProperties.QUERY_GET_INSURANCE_AMOUNT.getValue(), new HashMap<>())).thenReturn(responseQuerySumCumulus);
		when(pisdR350.executeGetListASingleRow(RBVDProperties.QUERY_GET_PRODUCT_MODALITIES_INFORMATION.getValue(), new HashMap<>())).thenReturn(responseQueryConsiderations);
		when(this.mapperHelper.mapInRequestRimacLife(anyObject(), anyObject())).thenReturn(requestRimac);
		when(this.rbvdr301.executeSimulationRimacService(anyObject(), anyString())).thenReturn(responseRimac);
		when(pisdR350.executeInsertSingleRow(RBVDProperties.QUERY_INSERT_INSURANCE_SIMULATION.getValue(), new HashMap<>())).thenReturn(executeInsertSingleRow);
		LifeSimulationDTO validation = this.rbvdR302.executeGetSimulation(requestInput);

		assertNull(validation);
	}

	@Test
	public void executeGetGeneratePlansSimulationFailTest() {
		LOGGER.info("RBVDR302Test - Executing executeGetGeneratePlansSimulationFailTest...");
		when(applicationConfigurationService.getProperty(anyString())).thenReturn("L");
		when(pisdR350.executeGetASingleRow(RBVDProperties.QUERY_GET_PRODUCT_INFORMATION.getValue(), new HashMap<>())).thenReturn(responseQueryGetProductInformation);
		responseQuerySumCumulus = new ArrayMap<>();
		when(pisdR350.executeGetListASingleRow(RBVDProperties.QUERY_GET_INSURANCE_AMOUNT.getValue(), new HashMap<>())).thenReturn(responseQuerySumCumulus);
		when(pisdR350.executeGetListASingleRow(RBVDProperties.QUERY_GET_PRODUCT_MODALITIES_INFORMATION.getValue(), new HashMap<>())).thenReturn(responseQueryConsiderations);
		when(this.mapperHelper.mapInRequestRimacLife(anyObject(), anyObject())).thenReturn(requestRimac);
		when(this.rbvdr301.executeSimulationRimacService(anyObject(), anyString())).thenReturn(responseRimac);
		when(this.rbvdr301.executeCryptoService(anyObject())).thenReturn(crypto);
		when(this.rbvdr301.executeGetTierService(anyObject())).thenReturn(tier);
		when(pisdR350.executeInsertSingleRow(RBVDProperties.QUERY_INSERT_INSURANCE_SIMULATION.getValue(), new HashMap<>())).thenReturn(0);
		LifeSimulationDTO validation = this.rbvdR302.executeGetSimulation(requestInput);

		assertNull(validation);
	}

	@Test
	public void executeGetRimacNullExceptionTest() throws IOException {
		LOGGER.info("RBVDR302Test - Executing executeGetRimacNullExceptionTest...");
		when(applicationConfigurationService.getProperty(anyString())).thenReturn("L");
		when(pisdR350.executeGetASingleRow(RBVDProperties.QUERY_GET_PRODUCT_INFORMATION.getValue(), new HashMap<>())).thenReturn(responseQueryGetProductInformation);
		responseQuerySumCumulus = new ArrayMap<>();
		when(pisdR350.executeGetListASingleRow(RBVDProperties.QUERY_GET_INSURANCE_AMOUNT.getValue(), new HashMap<>())).thenReturn(responseQuerySumCumulus);
		when(this.mapperHelper.mapInRequestRimacLife(anyObject(), anyObject())).thenReturn(requestRimac);
		when(this.rbvdr301.executeSimulationRimacService(anyObject(), anyString())).thenReturn(null);

		LifeSimulationDTO validation = this.rbvdR302.executeGetSimulation(requestInput);

		assertNull(validation);
	}

	@Test(expected = Exception.class)
	public void executeGetGenerateExceptionTest() throws IOException {
		LOGGER.info("RBVDR302Test - Executing executeGetGenerateExceptionTest...");
		when(applicationConfigurationService.getProperty(anyString())).thenReturn("L");
		when(pisdR350.executeGetASingleRow(RBVDProperties.QUERY_GET_PRODUCT_INFORMATION.getValue(), new HashMap<>())).thenReturn(responseQueryGetProductInformation);
		when(pisdR350.executeGetListASingleRow(RBVDProperties.QUERY_GET_INSURANCE_AMOUNT.getValue(), new HashMap<>())).thenReturn(responseQuerySumCumulus);
		when(this.mapperHelper.mapInRequestRimacLife(anyObject(), anyObject())).thenReturn(null);
		when(this.rbvdr301.executeSimulationRimacService(anyObject(), anyString())).thenReturn(null);

		LifeSimulationDTO validation = this.rbvdR302.executeGetSimulation(requestInput);

		assertNull(validation);
	}

}
