package com.bbva.rbvd.lib.r302;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.elara.domain.transaction.Context;
import com.bbva.elara.domain.transaction.ThreadContext;

import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.pisd.dto.insurance.aso.gifole.GifoleInsuranceRequestASO;
import com.bbva.pisd.dto.insurance.aso.tier.TierASO;
import com.bbva.pisd.dto.insurance.bo.BirthDataBO;
import com.bbva.pisd.dto.insurance.bo.customer.CustomerBO;
import com.bbva.pisd.dto.insurance.mock.MockDTO;

import com.bbva.pisd.lib.r350.PISDR350;

import com.bbva.rbvd.dto.lifeinsrc.commons.DocumentTypeDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.IdentityDocumentDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.RefundsDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.InsuranceProductDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.InsurancePlanDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.TermDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.UnitDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.TotalInstallmentDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.InstallmentsDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.GenderDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.PeriodDTO;
import com.bbva.rbvd.dto.lifeinsrc.mock.MockData;
import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.InsuranceLifeSimulationBO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.ParticipantDTO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.ContractDetailsDTO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.ContactDTO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.ParticipantTypeDTO;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDProperties;

import com.bbva.rbvd.lib.r301.RBVDR301;
import com.bbva.rbvd.lib.r302.impl.RBVDR302Impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;


import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Arrays;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RBVDR302Test {

	@Spy
	private Context context;

	@InjectMocks
	private RBVDR302Impl rbvdR302 ;

	@Mock
	private ApplicationConfigurationService applicationConfigurationService;

	@Mock
	private RBVDR301 rbvdr301;


	private MockData mockData;

	private static final Logger LOGGER = LoggerFactory.getLogger(RBVDR302Test.class);

	private InsuranceLifeSimulationBO responseRimac;


	private LifeSimulationDTO requestInput;

	@Mock
	private PISDR350 pisdR350;

	private TierASO tier;

	private MockDTO mockDTO;

	private Map<String, Object> responseQueryModalities;

	private Map<String, Object> responseQuerySumCumulus;

	private GifoleInsuranceRequestASO gifoleInsReqAso;


	@Before
	public void setUp() throws Exception {
		context = new Context();
		ThreadContext.set(context);

		mockData = MockData.getInstance();

		responseRimac = mockData.getInsuranceRimacSimulationResponse();
		requestInput = mockData.getInsuranceSimulationRequest();


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
		participantDTO.setId("10225879");
		participantDTO.setBirthDate( new Date());
		participantDTO.setIdentityDocument(identityDocumentDTO);
		participantDTO.setGender(new GenderDTO());
		participantDTO.getGender().setId("MALE");


		participantDTO.setParticipantType(new ParticipantTypeDTO());
		participantDTO.getParticipantType().setId("455");
		participantDTO.setLastName("HGHHH");
		participantDTO.setSecondLastName("HGHHH");
		ContractDetailsDTO contractDetail = new ContractDetailsDTO();
		contractDetail.setContact(new ContactDTO());
		contractDetail.getContact().setAddress("4555");
		ContractDetailsDTO contractDetail2 = new ContractDetailsDTO();
		contractDetail2.setContact(new ContactDTO());
		contractDetail2.getContact().setAddress("@gmail.com");
		List<ContractDetailsDTO> contractDetailsList = new ArrayList<>();
		contractDetailsList.add(contractDetail);
		contractDetailsList.add(contractDetail2);
		participantDTO.setContactDetails(contractDetailsList);
		this.requestInput.setTerm(new TermDTO());
		this.requestInput.getTerm().setNumber(45);

		this.requestInput.setParticipants(Collections.singletonList(participantDTO));

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

		LifeSimulationDTO response = this.rbvdR302.executeGetSimulation(requestInput);

		assertNotNull(response);

		Mockito.verify(pisdR350, Mockito.atLeastOnce()).executeGetASingleRow(RBVDProperties.QUERY_SELECT_INSURANCE_SIMULATION_ID.getValue(), new HashMap<>());

	}

	@Test
	public void executeTestParticipantNull() {
		this.requestInput.getProduct().setId("840");
		this.requestInput.setTerm(new TermDTO());
		this.requestInput.getTerm().setNumber(45);

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
	public void executeGetGenerateDynamicLifeTest() throws IOException {

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
		participantDTO.setId("PE0074000000446");
		participantDTO.setBirthDate(new Date());
		participantDTO.setIdentityDocument(identityDocumentDTO);
		participantDTO.setGender(new GenderDTO());
		participantDTO.getGender().setId("FEMALE");

		participantDTO.setParticipantType(new ParticipantTypeDTO());
		participantDTO.getParticipantType().setId("455");
		participantDTO.setLastName("HGHHH");
		participantDTO.setSecondLastName("HGHHH");
		ContractDetailsDTO contractDetail = new ContractDetailsDTO();
		contractDetail.setContact(new ContactDTO());
		contractDetail.getContact().setAddress("4555");
		ContractDetailsDTO contractDetail2 = new ContractDetailsDTO();
		contractDetail2.setContact(new ContactDTO());
		contractDetail2.getContact().setAddress("@gmail.com");
		List<ContractDetailsDTO> contractDetailsList = new ArrayList<>();
		contractDetailsList.add(contractDetail);
		contractDetailsList.add(contractDetail2);
		participantDTO.setContactDetails(contractDetailsList);
		this.requestInput.setTerm(new TermDTO());
		this.requestInput.getTerm().setNumber(45);

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

		responseRimac.getPayload().setProducto("VIDADINAMICO");
		responseRimac.getPayload().getCotizaciones().get(0).setIndicadorBloqueo(Long.parseLong("0"));
		responseRimac.getPayload().getCotizaciones().get(0).getPlan().setPlan(Long.parseLong("533726"));

		when(this.rbvdr301.executeSimulationRimacService(anyObject(), anyString())).thenReturn(responseRimac);
		when(this.rbvdr301.executeSimulationModificationRimacService(anyObject(),anyString(),anyString())).thenReturn(responseRimac);
		when(this.pisdR350.executeInsertSingleRow(anyString(), anyMap())).thenReturn(1);

		List<InsurancePlanDTO> insurancePlanList = new ArrayList<>();
		InsurancePlanDTO insurancePlan = new InsurancePlanDTO();
		insurancePlan.setTotalInstallment(new TotalInstallmentDTO());
		insurancePlan.getTotalInstallment().setAmount(BigDecimal.valueOf(555484));
		insurancePlanList.add(insurancePlan);

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
