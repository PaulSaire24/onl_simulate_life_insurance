package com.bbva.rbvd.lib.r302;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.elara.domain.transaction.Context;
import com.bbva.elara.domain.transaction.ThreadContext;

import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.pisd.dto.insurance.aso.crypto.CryptoASO;
import com.bbva.pisd.dto.insurance.aso.crypto.CryptoDataASO;
import com.bbva.pisd.dto.insurance.aso.gifole.GifoleInsuranceRequestASO;
import com.bbva.pisd.dto.insurance.aso.tier.TierASO;
import com.bbva.pisd.dto.insurance.bo.BirthDataBO;
import com.bbva.pisd.dto.insurance.bo.customer.CustomerBO;
import com.bbva.pisd.dto.insurance.mock.MockDTO;

import com.bbva.pisd.lib.r350.PISDR350;

import com.bbva.rbvd.dto.lifeinsrc.dao.ProductInformationDAO;
import com.bbva.rbvd.dto.lifeinsrc.mock.MockData;
import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.InsuranceLifeSimulationBO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDProperties;

import com.bbva.rbvd.lib.r301.RBVDR301;
import com.bbva.rbvd.lib.r302.Transfer.PayloadStore;
import com.bbva.rbvd.lib.r302.business.IInsrEasyYesBusiness;
import com.bbva.rbvd.lib.r302.impl.RBVDR302Impl;
//import com.bbva.rbvd.lib.r302.impl.util.MapperHelper;

import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.*;

import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/*@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:/META-INF/spring/RBVDR302-app.xml",
		"classpath:/META-INF/spring/RBVDR302-app-test.xml",
		"classpath:/META-INF/spring/RBVDR302-arc.xml",
		"classpath:/META-INF/spring/RBVDR302-arc-test.xml" })
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)*/
@RunWith(MockitoJUnitRunner.class)
public class RBVDR302Test {

	@Spy
	private Context context;

	@InjectMocks
	private RBVDR302Impl rbvdR302 ;//= new RBVDR302Impl();

	@Mock
	private ApplicationConfigurationService applicationConfigurationService;

	@Mock
	private RBVDR301 rbvdr301;

	private MockData mockData;

	private static final Logger LOGGER = LoggerFactory.getLogger(RBVDR302Test.class);

	private InsuranceLifeSimulationBO responseRimac;

	//private MapperHelper mapperHelper;

	private InsuranceLifeSimulationBO requestRimac;

	private LifeSimulationDTO input;

	private LifeSimulationDTO requestInput;

	private LifeSimulationDTO responseInput;

	@Mock
	private PISDR350 pisdR350;

	private CryptoASO crypto;

	private TierASO tier;

	private MockDTO mockDTO;


	private Map<String, Object> responseQueryGetProductInformation;


	private Map<String, Object> responseQueryModalities;

	private Map<String, Object> responseQuerySumCumulus;

	private int executeInsertSingleRow;

	private GifoleInsuranceRequestASO gifoleInsReqAso;
	private IInsrEasyYesBusiness seguroEasyYes;
	private PayloadStore payloadStore;

	@Before
	public void setUp() throws Exception {
		//MockitoAnnotations.initMocks(this);
		context = new Context();
		ThreadContext.set(context);

		mockData = MockData.getInstance();

		//applicationConfigurationService = mock(ApplicationConfigurationService.class);
		//rbvdr301 = mock(RBVDR301.class);
		//pisdR350 = mock(PISDR350.class);

		//rbvdR302.setRbvdR301(rbvdr301);
		//rbvdR302.setApplicationConfigurationService(applicationConfigurationService);
		//rbvdR302.setPisdR350(pisdR350);

		responseRimac = mockData.getInsuranceRimacSimulationResponse();
		requestRimac = mockData.getInsuranceRimacSimulationRequest();
		requestInput = mockData.getInsuranceSimulationRequest();

		//mapperHelper = mock(MapperHelper.class);
		//rbvdR302.setMapperHelper(mapperHelper);

		input = new LifeSimulationDTO();

		//responseQueryGetProductInformation = mock(Map.class);



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

		//seguroEasyYes = new InsrEasyYesBusinessImpl(this.rbvdr301,this.applicationConfigurationService);

	}

	@Test
	public void executeGetGenerateEasyYesTest(){
		this.requestInput.getProduct().setId("840");
		LOGGER.info("RBVDR302Test - Executing executeGetGenerateEasyYesTest...");
		when(applicationConfigurationService.getProperty(anyString())).thenReturn("L");

		//when(applicationConfigurationService.getProperty("ENABLE_GIFOLE_LIFE_ASO")).thenReturn("true");

		Map<String,Object> responseQueryGetProductInformation2 = new HashMap<>();
		responseQueryGetProductInformation2.put(RBVDProperties.FIELD_OR_FILTER_INSURANCE_PRODUCT_ID.getValue(),new BigDecimal(840));
		responseQueryGetProductInformation2.put(RBVDProperties.FIELD_INSURANCE_PRODUCT_DESC.getValue(),"desc easy yes");
		responseQueryGetProductInformation2.put("PRODUCT_SHORT_DESC","EASYYES");
		when(pisdR350.executeGetASingleRow(Mockito.anyString(), Mockito.anyMap())).thenReturn(responseQueryGetProductInformation2);

		responseQueryModalities = new HashMap<>();
		responseQueryModalities.put(RBVDProperties.FIELD_OR_FILTER_INSURANCE_PRODUCT_ID.getValue(), new BigDecimal(840));
		responseQueryModalities.put(RBVDProperties.FIELD_OR_FILTER_INSURANCE_MODALITY_TYPE.getValue(), Arrays.asList("01","02"));
		responseQueryModalities.put(RBVDProperties.FIELD_SALE_CHANNEL_ID.getValue(), "PC");


		List<Map<String, Object>> listResponse = new ArrayList<>();
		Map<String, Object> responseAmount = new HashMap<>();
		responseAmount.put("INSURED_AMOUNT", new BigDecimal(13.3));
		listResponse.add(responseAmount);
		responseQueryModalities.put(RBVDProperties.KEY_OF_INSRC_LIST_RESPONSES.getValue(), listResponse);
		responseQuerySumCumulus = new HashMap<>();
		responseQuerySumCumulus.put(RBVDProperties.KEY_OF_INSRC_LIST_RESPONSES.getValue(), listResponse);
		when(pisdR350.executeGetListASingleRow(anyString(), anyMap()))
				.thenReturn(responseQueryModalities)
				.thenReturn(responseQuerySumCumulus);
		when(this.rbvdr301.executeCryptoService(anyObject())).thenReturn(crypto);
		when(this.rbvdr301.executeGetTierService(anyObject())).thenReturn(tier);
		when(this.rbvdr301.executeSimulationRimacService(anyObject(), anyString())).thenReturn(responseRimac);
		when(this.pisdR350.executeInsertSingleRow(Mockito.anyString(), Mockito.anyMap())).thenReturn(1);
		//when(this.pisdR350.executeInsertSingleRow(RBVDProperties.QUERY_INSERT_INSRNC_SIMLT_PRD.getValue(), new HashMap<>())).thenReturn(1);

		/*when(this.rbvdr301.executeSimulationModificationRimacService(anyObject(), anyString(), anyString())).thenReturn(responseRimac);
		List<Map<String, Object>> responseConsiderations = new ArrayList<>();
		Map<String, Object> uniqueExample = new HashMap<>();
		uniqueExample.put(RBVDProperties.FIELD_OR_FILTER_INSURANCE_MODALITY_TYPE.getValue(), "01");
		responseConsiderations.add(uniqueExample);
		when(responseQueryConsiderations.get(RBVDProperties.KEY_OF_INSRC_LIST_RESPONSES.getValue())).
				thenReturn(responseConsiderations);*/

		//when(pisdR350.executeGetListASingleRow(RBVDProperties.QUERY_GET_PRODUCT_MODALITIES_INFORMATION.getValue(), new HashMap<>())).thenReturn(responseQueryConsiderations);
		/*responseQuerySumCumulus = new ArrayMap<>();

		//when(QuotationRimac.mapInRequestRimacLife(anyObject(), anyObject())).thenReturn(requestRimac);
		//when(seguroEasyYes.createGifoleASO(anyObject(), anyObject())).thenReturn(gifoleInsReqAso);
		when(this.rbvdr301.executeGifolelifeService(anyObject())).thenReturn(201);*/

		payloadStore = new PayloadStore("1234","P02X2021",responseRimac, responseInput, "",new ProductInformationDAO());

		LifeSimulationDTO response = this.rbvdR302.executeGetSimulation(requestInput);

		assertNotNull(response);

		Mockito.verify(pisdR350, Mockito.atLeastOnce()).executeGetASingleRow(RBVDProperties.QUERY_SELECT_INSURANCE_SIMULATION_ID.getValue(), new HashMap<>());

	}

	@Test
	public void executeGetGenerateDynamicLifeTest(){

		LOGGER.info("RBVDR302Test - Executing executeGetGenerateDynamicLifeTest...");
		this.requestInput.getProduct().setId("841");
		this.requestInput.setExternalSimulationId(null);
		responseRimac.getPayload().setProducto("VIDADINAMICO");
		when(applicationConfigurationService.getProperty(anyString())).thenReturn("L");

		Map<String,Object> responseQueryGetProductInformation2 = new HashMap<>();
		responseQueryGetProductInformation2.put(RBVDProperties.FIELD_OR_FILTER_INSURANCE_PRODUCT_ID.getValue(),new BigDecimal(841));
		responseQueryGetProductInformation2.put(RBVDProperties.FIELD_INSURANCE_PRODUCT_DESC.getValue(),"desc dynamic");
		responseQueryGetProductInformation2.put("PRODUCT_SHORT_DESC","VIDADINAMICO");
		when(pisdR350.executeGetASingleRow(Mockito.anyString(), Mockito.anyMap())).thenReturn(responseQueryGetProductInformation2);

		responseQueryModalities = new HashMap<>();
		responseQueryModalities.put(RBVDProperties.FIELD_OR_FILTER_INSURANCE_PRODUCT_ID.getValue(), new BigDecimal(841));
		responseQueryModalities.put(RBVDProperties.FIELD_OR_FILTER_INSURANCE_MODALITY_TYPE.getValue(), Arrays.asList("01","02"));
		responseQueryModalities.put(RBVDProperties.FIELD_SALE_CHANNEL_ID.getValue(), "PC");

		List<Map<String, Object>> listResponse = new ArrayList<>();
		Map<String, Object> responseAmount = new HashMap<>();
		responseAmount.put("INSURED_AMOUNT", new BigDecimal(13.3));
		listResponse.add(responseAmount);
		responseQueryModalities.put(RBVDProperties.KEY_OF_INSRC_LIST_RESPONSES.getValue(), listResponse);
		responseQuerySumCumulus = new HashMap<>();
		responseQuerySumCumulus.put(RBVDProperties.KEY_OF_INSRC_LIST_RESPONSES.getValue(), listResponse);
		when(pisdR350.executeGetListASingleRow(anyString(), anyMap()))
				.thenReturn(responseQueryModalities)
				.thenReturn(responseQuerySumCumulus);
		when(this.rbvdr301.executeCryptoService(anyObject())).thenReturn(crypto);
		when(this.rbvdr301.executeGetTierService(anyObject())).thenReturn(tier);
		when(this.rbvdr301.executeCallListCustomerResponse(anyString())).thenReturn(getCustomerListASO());
		when(this.rbvdr301.executeSimulationRimacService(anyObject(), anyString())).thenReturn(responseRimac);
		when(this.rbvdr301.executeSimulationModificationRimacService(anyObject(),anyString(),anyString())).thenReturn(responseRimac);
		when(this.pisdR350.executeInsertSingleRow(Mockito.anyString(), Mockito.anyMap())).thenReturn(1);
		//when(this.pisdR350.executeInsertSingleRow(RBVDProperties.QUERY_INSERT_INSRNC_SIMLT_PRD.getValue(), new HashMap<>())).thenReturn(1);

		/*when(this.rbvdr301.executeSimulationModificationRimacService(anyObject(), anyString(), anyString())).thenReturn(responseRimac);
		List<Map<String, Object>> responseConsiderations = new ArrayList<>();
		Map<String, Object> uniqueExample = new HashMap<>();
		uniqueExample.put(RBVDProperties.FIELD_OR_FILTER_INSURANCE_MODALITY_TYPE.getValue(), "01");
		responseConsiderations.add(uniqueExample);
		when(responseQueryConsiderations.get(RBVDProperties.KEY_OF_INSRC_LIST_RESPONSES.getValue())).
				thenReturn(responseConsiderations);*/

		//when(pisdR350.executeGetListASingleRow(RBVDProperties.QUERY_GET_PRODUCT_MODALITIES_INFORMATION.getValue(), new HashMap<>())).thenReturn(responseQueryConsiderations);
		/*responseQuerySumCumulus = new ArrayMap<>();

		//when(QuotationRimac.mapInRequestRimacLife(anyObject(), anyObject())).thenReturn(requestRimac);
		//when(seguroEasyYes.createGifoleASO(anyObject(), anyObject())).thenReturn(gifoleInsReqAso);
		when(this.rbvdr301.executeGifolelifeService(anyObject())).thenReturn(201);*/

		payloadStore = new PayloadStore("1234","P02X2021",responseRimac, responseInput, "",new ProductInformationDAO());

		LifeSimulationDTO response = this.rbvdR302.executeGetSimulation(requestInput);

		assertNotNull(response);

		Mockito.verify(pisdR350, Mockito.atLeastOnce()).executeGetASingleRow(anyString(), anyMap());
		//Mockito.verify(rbvdr301,Mockito.times(1)).executeSimulationRimacService(anyObject(),anyString());

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
/*
	@Test
	public void executeGetGenerateFalseTest(){
		LOGGER.info("RBVDR302Test - Executing executeGetGenerateFalseTest...");
		when(applicationConfigurationService.getProperty(anyString())).thenReturn("L");
		when(applicationConfigurationService.getProperty("ENABLE_GIFOLE_LIFE_ASO")).thenReturn("false");
		when(pisdR350.executeGetASingleRow(RBVDProperties.QUERY_GET_PRODUCT_INFORMATION.getValue(), new HashMap<>())).thenReturn(responseQueryGetProductInformation);
		responseQuerySumCumulus = new ArrayMap<>();
		when(pisdR350.executeGetListASingleRow(RBVDProperties.QUERY_GET_INSURANCE_AMOUNT.getValue(), new HashMap<>())).thenReturn(responseQuerySumCumulus);
		when(pisdR350.executeGetListASingleRow(RBVDProperties.QUERY_GET_PRODUCT_MODALITIES_INFORMATION.getValue(), new HashMap<>())).thenReturn(responseQueryConsiderations);
		when(QuotationRimac.mapInRequestRimacLife(anyObject(), anyObject())).thenReturn(requestRimac);
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
		//when(seguroEasyYes.createGifoleASO(anyObject(), anyObject())).thenReturn(gifoleInsReqAso);
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
		when(QuotationRimac.mapInRequestRimacLife(anyObject(), anyObject())).thenReturn(requestRimac);
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
		//when(seguroEasyYes.createGifoleASO(anyObject(), anyObject())).thenReturn(gifoleInsReqAso);
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
		when(QuotationRimac.mapInRequestRimacLife(anyObject(), anyObject())).thenReturn(requestRimac);
		when(this.rbvdr301.executeCryptoService(anyObject())).thenReturn(crypto);
		when(this.rbvdr301.executeGetTierService(anyObject())).thenReturn(tier);
		when(this.rbvdr301.executeSimulationRimacService(anyObject(), anyString())).thenReturn(responseRimac);
		when(pisdR350.executeInsertSingleRow(RBVDProperties.QUERY_INSERT_INSURANCE_SIMULATION.getValue(), new HashMap<>())).thenReturn(executeInsertSingleRow);
		when(responseQueryConsiderations.get(RBVDProperties.KEY_OF_INSRC_LIST_RESPONSES.getValue())).
				thenReturn(new ArrayList<>());
		//when(seguroEasyYes.createGifoleASO(anyObject(), anyObject())).thenReturn(gifoleInsReqAso);
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
		when(QuotationRimac.mapInRequestRimacLife(anyObject(), anyObject())).thenReturn(requestRimac);
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
		when(QuotationRimac.mapInRequestRimacLife(anyObject(), anyObject())).thenReturn(requestRimac);
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
		when(QuotationRimac.mapInRequestRimacLife(anyObject(), anyObject())).thenReturn(requestRimac);
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
		when(QuotationRimac.mapInRequestRimacLife(anyObject(), anyObject())).thenReturn(requestRimac);
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
		when(QuotationRimac.mapInRequestRimacLife(anyObject(), anyObject())).thenReturn(requestRimac);
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
		when(QuotationRimac.mapInRequestRimacLife(anyObject(), anyObject())).thenReturn(null);
		when(this.rbvdr301.executeSimulationRimacService(anyObject(), anyString())).thenReturn(null);

		LifeSimulationDTO validation = this.rbvdR302.executeGetSimulation(requestInput);

		assertNull(validation);
	}

	@Test
	public void executeGetGenerateTest_DynamicLife(){
		LOGGER.info("RBVDR302Test - Executing executeGetGenerateTest_DynamicLife...");
		when(applicationConfigurationService.getProperty(anyString())).thenReturn("L");
		when(applicationConfigurationService.getProperty("ENABLE_GIFOLE_LIFE_ASO")).thenReturn("true");
		when(pisdR350.executeGetASingleRow(RBVDProperties.QUERY_GET_PRODUCT_INFORMATION.getValue(), new HashMap<>())).thenReturn(responseQueryGetProductInformation);
		responseQuerySumCumulus = new ArrayMap<>();
		List<Map<String, Object>> listResponse = new ArrayList<>();
		Map<String, Object> response = new HashMap<>();
		response.put("INSURED_AMOUNT", new BigDecimal(187.2));
		listResponse.add(response);
		responseQuerySumCumulus.put("dtoInsurance", listResponse);

		requestRimac.getPayload().setProducto("VIDADINAMICO");
		requestInput.getProduct().setId("841");

		when(pisdR350.executeGetListASingleRow(RBVDProperties.QUERY_GET_INSURANCE_AMOUNT.getValue(), new HashMap<>())).thenReturn(responseQuerySumCumulus);
		when(pisdR350.executeGetListASingleRow(RBVDProperties.QUERY_GET_PRODUCT_MODALITIES_INFORMATION.getValue(), new HashMap<>())).thenReturn(responseQueryConsiderations);
		when(QuotationRimac.mapInRequestRimacLife(anyObject(), anyObject())).thenReturn(requestRimac);
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
		//when(seguroEasyYes.createGifoleASO(anyObject(), anyObject())).thenReturn(gifoleInsReqAso);
		when(this.rbvdr301.executeGifolelifeService(anyObject())).thenReturn(201);
		LifeSimulationDTO validation = this.rbvdR302.executeGetSimulation(requestInput);

		assertNotNull(validation);
	}
	*/


}
