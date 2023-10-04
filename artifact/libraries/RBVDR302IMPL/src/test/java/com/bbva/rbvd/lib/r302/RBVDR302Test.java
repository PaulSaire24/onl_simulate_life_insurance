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

import com.bbva.rbvd.dto.lifeinsrc.commons.DocumentTypeDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.IdentityDocumentDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.RefundsDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.UnitDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.InsuranceProductDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.InsurancePlanDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.InstallmentsDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.PeriodDTO;
import com.bbva.rbvd.dto.lifeinsrc.dao.ProductInformationDAO;
import com.bbva.rbvd.dto.lifeinsrc.mock.MockData;
import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.InsuranceLifeSimulationBO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.ParticipantDTO;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDProperties;

import com.bbva.rbvd.lib.r301.RBVDR301;
import com.bbva.rbvd.lib.r302.transfer.PayloadConfig;
import com.bbva.rbvd.lib.r302.transfer.PayloadStore;
import com.bbva.rbvd.lib.r302.business.IInsrEasyYesBusiness;
import com.bbva.rbvd.lib.r302.impl.RBVDR302Impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.*;

import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;


import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

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

	private InsuranceLifeSimulationBO requestRimac;

	private LifeSimulationDTO input;

	private LifeSimulationDTO requestInput;

	private LifeSimulationDTO responseInput;

	@Mock
	private PISDR350 pisdR350;


	private TierASO tier;

	private MockDTO mockDTO;


	private Map<String, Object> responseQueryGetProductInformation;


	private Map<String, Object> responseQueryModalities;

	private Map<String, Object> responseQuerySumCumulus;

	private int executeInsertSingleRow;

	private GifoleInsuranceRequestASO gifoleInsReqAso;
	private IInsrEasyYesBusiness seguroEasyYes;
	private PayloadStore payloadStore;
	private PayloadConfig payloadConfig;

	@Before
	public void setUp() throws Exception {
		//MockitoAnnotations.initMocks(this);
		context = new Context();
		ThreadContext.set(context);

		mockData = MockData.getInstance();

		responseRimac = mockData.getInsuranceRimacSimulationResponse();
		requestRimac = mockData.getInsuranceRimacSimulationRequest();
		requestInput = mockData.getInsuranceSimulationRequest();

		input = new LifeSimulationDTO();

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
		tier = mockDTO.getTierMockResponse();
	}

	@Test
	public void executeGetGenerateEasyYesTest() {
		this.requestInput.getProduct().setId("840");

		DocumentTypeDTO documentTypeDTO = new DocumentTypeDTO();
		documentTypeDTO.setId("DNI");
		IdentityDocumentDTO identityDocumentDTO = new IdentityDocumentDTO();
		identityDocumentDTO.setDocumentNumber("14457841");
		identityDocumentDTO.setDocumentType(documentTypeDTO);
		ParticipantDTO participantDTO = new ParticipantDTO();
		participantDTO.setBirthDate( new Date());
		participantDTO.setIdentityDocument(identityDocumentDTO);

		this.requestInput.setParticipants(Collections.singletonList(participantDTO));

		LOGGER.info("RBVDR302Test - Executing executeGetGenerateEasyYesTest...");
		when(applicationConfigurationService.getProperty(anyString())).thenReturn("L");

		//when(applicationConfigurationService.getProperty("ENABLE_GIFOLE_LIFE_ASO")).thenReturn("true");

		Map<String,Object> responseQueryGetProductInformation2 = new HashMap<>();
		responseQueryGetProductInformation2.put(RBVDProperties.FIELD_OR_FILTER_INSURANCE_PRODUCT_ID.getValue(),new BigDecimal(840));
		responseQueryGetProductInformation2.put(RBVDProperties.FIELD_INSURANCE_PRODUCT_DESC.getValue(),"desc easy yes");
		responseQueryGetProductInformation2.put("PRODUCT_SHORT_DESC","EASYYES");
		when(pisdR350.executeGetASingleRow(anyString(), anyMap())).thenReturn(responseQueryGetProductInformation2);

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
		when(this.rbvdr301.executeGetCustomerIdEncrypted(anyObject())).thenReturn("45qyxsw7");
		when(this.rbvdr301.executeGetTierService(anyObject())).thenReturn(tier);
		when(this.rbvdr301.executeSimulationRimacService(anyObject(), anyString())).thenReturn(responseRimac);
		when(this.pisdR350.executeInsertSingleRow(anyString(), anyMap())).thenReturn(1);

		payloadStore = new PayloadStore("1234","P02X2021",responseRimac, responseInput, "",new ProductInformationDAO());

		LifeSimulationDTO response = this.rbvdR302.executeGetSimulation(requestInput);

		assertNotNull(response);

		Mockito.verify(pisdR350, Mockito.atLeastOnce()).executeGetASingleRow(RBVDProperties.QUERY_SELECT_INSURANCE_SIMULATION_ID.getValue(), new HashMap<>());

	}

	@Test
	public void executeTestParticipantNull() {
		this.requestInput.getProduct().setId("840");

		LOGGER.info("RBVDR302Test - Executing executeGetGenerateEasyYesTest...");
		when(applicationConfigurationService.getProperty(anyString())).thenReturn("L");

		Map<String,Object> responseQueryGetProductInformation2 = new HashMap<>();
		responseQueryGetProductInformation2.put(RBVDProperties.FIELD_OR_FILTER_INSURANCE_PRODUCT_ID.getValue(),new BigDecimal(840));
		responseQueryGetProductInformation2.put(RBVDProperties.FIELD_INSURANCE_PRODUCT_DESC.getValue(),"desc easy yes");
		responseQueryGetProductInformation2.put("PRODUCT_SHORT_DESC","EASYYES");
		when(pisdR350.executeGetASingleRow(anyString(), anyMap())).thenReturn(responseQueryGetProductInformation2);

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
		when(this.rbvdr301.executeGetCustomerIdEncrypted(anyObject())).thenReturn("45qyxsw7");
		when(this.rbvdr301.executeGetTierService(anyObject())).thenReturn(tier);
		when(this.rbvdr301.executeSimulationRimacService(anyObject(), anyString())).thenReturn(responseRimac);
		when(this.pisdR350.executeInsertSingleRow(anyString(), anyMap())).thenReturn(1);


		payloadStore = new PayloadStore("1234","P02X2021",responseRimac, responseInput, "",new ProductInformationDAO());

		LifeSimulationDTO response = this.rbvdR302.executeGetSimulation(requestInput);

		assertNotNull(response);

		Mockito.verify(pisdR350, Mockito.atLeastOnce()).executeGetASingleRow(RBVDProperties.QUERY_SELECT_INSURANCE_SIMULATION_ID.getValue(), new HashMap<>());

	}

	private List<RefundsDTO> generateRefunds0PercentageInput(){
		List<RefundsDTO> refunds = new ArrayList<>();
		RefundsDTO percentage = new RefundsDTO();
		UnitDTO unit = new UnitDTO();
		unit.setUnitType("PERCENTAGE");
		unit.setPercentage(new BigDecimal("0"));
		percentage.setUnit(unit);

		refunds.add(percentage);
		return refunds;
	}

	private List<RefundsDTO> generateRefunds125PercentageInput(){
		List<RefundsDTO> refunds = new ArrayList<>();
		RefundsDTO percentage = new RefundsDTO();
		UnitDTO unit = new UnitDTO();
		unit.setUnitType("PERCENTAGE");
		unit.setPercentage(new BigDecimal("125"));
		percentage.setUnit(unit);

		refunds.add(percentage);
		return refunds;
	}

	@Test
	public void executeGetGenerateDynamicLifeTest() {

		LOGGER.info("RBVDR302Test - Executing executeGetGenerateDynamicLifeTest...");
		this.requestInput.getProduct().setId("841");
		this.requestInput.setExternalSimulationId(null);
		this.requestInput.setEndorsed(true);
		DocumentTypeDTO documentTypeDTO = new DocumentTypeDTO();
		documentTypeDTO.setId("DNI");
		IdentityDocumentDTO identityDocumentDTO = new IdentityDocumentDTO();
		identityDocumentDTO.setDocumentNumber("14457841");
		identityDocumentDTO.setDocumentType(documentTypeDTO);

		ParticipantDTO participantDTO = new ParticipantDTO();
		participantDTO.setBirthDate(new Date());
		participantDTO.setIdentityDocument(identityDocumentDTO);

		this.requestInput.setParticipants(Collections.singletonList(participantDTO));




		responseRimac.getPayload().setProducto("VIDADINAMICO");
		when(applicationConfigurationService.getProperty(anyString())).thenReturn("L");

		Map<String,Object> responseQueryGetProductInformation2 = new HashMap<>();
		responseQueryGetProductInformation2.put(RBVDProperties.FIELD_OR_FILTER_INSURANCE_PRODUCT_ID.getValue(),new BigDecimal(841));
		responseQueryGetProductInformation2.put(RBVDProperties.FIELD_INSURANCE_PRODUCT_DESC.getValue(),"desc dynamic");
		responseQueryGetProductInformation2.put("PRODUCT_SHORT_DESC","VIDADINAMICO");
		when(pisdR350.executeGetASingleRow(anyString(), anyMap())).thenReturn(responseQueryGetProductInformation2);

		responseQueryModalities = new HashMap<>();
		responseQueryModalities.put(RBVDProperties.FIELD_OR_FILTER_INSURANCE_PRODUCT_ID.getValue(), new BigDecimal(841));
		responseQueryModalities.put(RBVDProperties.FIELD_OR_FILTER_INSURANCE_MODALITY_TYPE.getValue(), Arrays.asList("01","02"));
		responseQueryModalities.put(RBVDProperties.FIELD_SALE_CHANNEL_ID.getValue(), "PC");

		List<Map<String, Object>> listResponse = new ArrayList<>();
		Map<String, Object> responseAmount = new HashMap<>();
		responseAmount.put("INSURED_AMOUNT", new BigDecimal("13.3"));
		listResponse.add(responseAmount);
		responseQueryModalities.put(RBVDProperties.KEY_OF_INSRC_LIST_RESPONSES.getValue(), listResponse);
		responseQuerySumCumulus = new HashMap<>();
		responseQuerySumCumulus.put(RBVDProperties.KEY_OF_INSRC_LIST_RESPONSES.getValue(), listResponse);
		when(pisdR350.executeGetListASingleRow(anyString(), anyMap()))
				.thenReturn(responseQueryModalities)
				.thenReturn(responseQuerySumCumulus);
		when(this.rbvdr301.executeGetCustomerIdEncrypted(anyObject())).thenReturn("45qyxsw7");
		when(this.rbvdr301.executeGetTierService(anyObject())).thenReturn(tier);
		when(this.rbvdr301.executeCallListCustomerResponse(anyString())).thenReturn(getCustomerListASO());
		when(this.rbvdr301.executeSimulationRimacService(anyObject(), anyString())).thenReturn(responseRimac);
		when(this.rbvdr301.executeSimulationModificationRimacService(anyObject(),anyString(),anyString())).thenReturn(responseRimac);
		when(this.pisdR350.executeInsertSingleRow(anyString(), anyMap())).thenReturn(1);

		payloadStore = new PayloadStore("1234","P02X2021",responseRimac, responseInput, "",new ProductInformationDAO());

		LifeSimulationDTO response = this.rbvdR302.executeGetSimulation(requestInput);

		assertNotNull(response);

		Mockito.verify(pisdR350, Mockito.atLeastOnce()).executeGetASingleRow(anyString(), anyMap());

	}

	@Test
	public void executeGetGenerateDynamicLife_PLAN1Test() {

		LOGGER.info("RBVDR302Test - Executing executeGetGenerateDynamicLife_PLAN1Test...");
		this.requestInput.getProduct().setId("841");
		this.requestInput.setListRefunds(generateRefunds0PercentageInput());
		this.requestInput.getProduct().setPlans(null);
		this.requestInput.setEndorsed(true);
		DocumentTypeDTO documentTypeDTO = new DocumentTypeDTO();
		documentTypeDTO.setId("DNI");
		IdentityDocumentDTO identityDocumentDTO = new IdentityDocumentDTO();
		identityDocumentDTO.setDocumentNumber("14457841");
		identityDocumentDTO.setDocumentType(documentTypeDTO);
		ParticipantDTO participantDTO = new ParticipantDTO();
		participantDTO.setBirthDate(new Date());
		participantDTO.setFirstName("gh");
		participantDTO.setMiddleName("gh");
		participantDTO.setLastName("gh");
		participantDTO.setSecondLastName("gh");
		participantDTO.setIdentityDocument(identityDocumentDTO);

		this.requestInput.setParticipants(Collections.singletonList(participantDTO));

		responseRimac.getPayload().setProducto("VIDADINAMICO");
		when(applicationConfigurationService.getProperty(anyString())).thenReturn("L");

		Map<String,Object> responseQueryGetProductInformation2 = new HashMap<>();
		responseQueryGetProductInformation2.put(RBVDProperties.FIELD_OR_FILTER_INSURANCE_PRODUCT_ID.getValue(),new BigDecimal(841));
		responseQueryGetProductInformation2.put(RBVDProperties.FIELD_INSURANCE_PRODUCT_DESC.getValue(),"desc dynamic");
		responseQueryGetProductInformation2.put("PRODUCT_SHORT_DESC","VIDADINAMICO");
		when(pisdR350.executeGetASingleRow(anyString(), anyMap())).thenReturn(responseQueryGetProductInformation2);

		responseQueryModalities = new HashMap<>();
		responseQueryModalities.put(RBVDProperties.FIELD_OR_FILTER_INSURANCE_PRODUCT_ID.getValue(), new BigDecimal(841));
		responseQueryModalities.put(RBVDProperties.FIELD_OR_FILTER_INSURANCE_MODALITY_TYPE.getValue(), Arrays.asList("01"));
		responseQueryModalities.put(RBVDProperties.FIELD_SALE_CHANNEL_ID.getValue(), "PC");

		List<Map<String, Object>> listResponse = new ArrayList<>();
		Map<String, Object> responseAmount = new HashMap<>();
		responseAmount.put("INSURED_AMOUNT", new BigDecimal("13.3"));
		listResponse.add(responseAmount);
		responseQueryModalities.put(RBVDProperties.KEY_OF_INSRC_LIST_RESPONSES.getValue(), listResponse);
		responseQuerySumCumulus = new HashMap<>();
		responseQuerySumCumulus.put(RBVDProperties.KEY_OF_INSRC_LIST_RESPONSES.getValue(), listResponse);
		when(pisdR350.executeGetListASingleRow(anyString(), anyMap()))
				.thenReturn(responseQueryModalities)
				.thenReturn(responseQuerySumCumulus);
		when(this.rbvdr301.executeGetCustomerIdEncrypted(anyObject())).thenReturn("45qyxsw7");
		when(this.rbvdr301.executeGetTierService(anyObject())).thenReturn(tier);
		when(this.rbvdr301.executeCallListCustomerResponse(anyString())).thenReturn(getCustomerListASO());
		when(this.rbvdr301.executeSimulationRimacService(anyObject(), anyString())).thenReturn(responseRimac);
		when(this.rbvdr301.executeSimulationModificationRimacService(anyObject(),anyString(),anyString())).thenReturn(responseRimac);
		when(this.pisdR350.executeInsertSingleRow(anyString(), anyMap())).thenReturn(1);

		payloadStore = new PayloadStore("1234","P02X2021",responseRimac, responseInput, "",new ProductInformationDAO());

		LifeSimulationDTO response = this.rbvdR302.executeGetSimulation(requestInput);

		assertNotNull(response);

		Mockito.verify(pisdR350, Mockito.atLeastOnce()).executeGetASingleRow(anyString(), anyMap());

	}

	@Test
	public void executeGetGenerateDynamicLife_PLAN2Test() {

		LOGGER.info("RBVDR302Test - Executing executeGetGenerateDynamicLife_PLAN2Test...");
		this.requestInput.getProduct().setId("841");
		this.requestInput.setListRefunds(generateRefunds125PercentageInput());

		this.requestInput.setEndorsed(true);
		DocumentTypeDTO documentTypeDTO = new DocumentTypeDTO();
		documentTypeDTO.setId("DNI");
		IdentityDocumentDTO identityDocumentDTO = new IdentityDocumentDTO();
		identityDocumentDTO.setDocumentNumber("14457841");
		identityDocumentDTO.setDocumentType(documentTypeDTO);
		ParticipantDTO participantDTO = new ParticipantDTO();
		participantDTO.setBirthDate(new Date());
		participantDTO.setFirstName("gh");
		participantDTO.setMiddleName("gh");
		participantDTO.setLastName("gh");
		participantDTO.setSecondLastName("gh");
		participantDTO.setIdentityDocument(identityDocumentDTO);

		InsuranceProductDTO product = new InsuranceProductDTO();
		List<InsurancePlanDTO> plansList = new ArrayList<>();
		InsurancePlanDTO plans = new InsurancePlanDTO();
		List<InstallmentsDTO> installmentsDTOList = new ArrayList<>();
		InstallmentsDTO installmentsDTO = new InstallmentsDTO();
		installmentsDTO.setPaymentsTotalNumber(45L);
		PeriodDTO periodDTO = new PeriodDTO();
		periodDTO.setId("L");
		installmentsDTO.setPeriod(periodDTO);
		installmentsDTOList.add(installmentsDTO);
		plans.setInstallmentPlans(installmentsDTOList);
		plansList.add(plans);

		/*product.setPlans(plansList);*/
		this.requestInput.getProduct().setPlans(plansList);

		this.requestInput.setParticipants(Collections.singletonList(participantDTO));

		responseRimac.getPayload().setProducto("VIDADINAMICO");
		if(!CollectionUtils.isEmpty(requestInput.getProduct().getPlans()) && !CollectionUtils.isEmpty(requestInput.getProduct().getPlans().get(0).getInstallmentPlans())){
			when(applicationConfigurationService.getProperty(anyString())).thenReturn("15");
		}else{
			when(applicationConfigurationService.getProperty(anyString())).thenReturn("L");
		}


		Map<String,Object> responseQueryGetProductInformation2 = new HashMap<>();
		responseQueryGetProductInformation2.put(RBVDProperties.FIELD_OR_FILTER_INSURANCE_PRODUCT_ID.getValue(),new BigDecimal(841));
		responseQueryGetProductInformation2.put(RBVDProperties.FIELD_INSURANCE_PRODUCT_DESC.getValue(),"desc dynamic");
		responseQueryGetProductInformation2.put("PRODUCT_SHORT_DESC","VIDADINAMICO");
		when(pisdR350.executeGetASingleRow(anyString(), anyMap())).thenReturn(responseQueryGetProductInformation2);

		responseQueryModalities = new HashMap<>();
		responseQueryModalities.put(RBVDProperties.FIELD_OR_FILTER_INSURANCE_PRODUCT_ID.getValue(), new BigDecimal(841));
		responseQueryModalities.put(RBVDProperties.FIELD_OR_FILTER_INSURANCE_MODALITY_TYPE.getValue(), Arrays.asList("02"));
		responseQueryModalities.put(RBVDProperties.FIELD_SALE_CHANNEL_ID.getValue(), "PC");

		List<Map<String, Object>> listResponse = new ArrayList<>();
		Map<String, Object> responseAmount = new HashMap<>();
		responseAmount.put("INSURED_AMOUNT", new BigDecimal("1359.3"));
		listResponse.add(responseAmount);
		responseQueryModalities.put(RBVDProperties.KEY_OF_INSRC_LIST_RESPONSES.getValue(), listResponse);
		responseQuerySumCumulus = new HashMap<>();
		responseQuerySumCumulus.put(RBVDProperties.KEY_OF_INSRC_LIST_RESPONSES.getValue(), listResponse);
		when(pisdR350.executeGetListASingleRow(anyString(), anyMap()))
				.thenReturn(responseQueryModalities)
				.thenReturn(responseQuerySumCumulus);
		when(this.rbvdr301.executeGetCustomerIdEncrypted(anyObject())).thenReturn("45qyxsw7");
		when(this.rbvdr301.executeGetTierService(anyObject())).thenReturn(tier);
		when(this.rbvdr301.executeCallListCustomerResponse(anyString())).thenReturn(getCustomerListASO());
		when(this.rbvdr301.executeSimulationRimacService(anyObject(), anyString())).thenReturn(responseRimac);
		when(this.rbvdr301.executeSimulationModificationRimacService(anyObject(),anyString(),anyString())).thenReturn(responseRimac);
		when(this.pisdR350.executeInsertSingleRow(anyString(), anyMap())).thenReturn(1);

		payloadStore = new PayloadStore("1234","P02X2021",responseRimac, responseInput, "",new ProductInformationDAO());

		LifeSimulationDTO response = this.rbvdR302.executeGetSimulation(requestInput);

		assertNotNull(response);

		Mockito.verify(pisdR350, Mockito.atLeastOnce()).executeGetASingleRow(anyString(), anyMap());

	}

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

}
