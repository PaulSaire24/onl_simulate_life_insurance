package com.bbva.rbvd.lib.r301;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.apx.exception.io.network.TimeoutException;
import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.elara.domain.transaction.Context;
import com.bbva.elara.domain.transaction.ThreadContext;
import javax.annotation.Resource;

import com.bbva.elara.utility.api.connector.APIConnector;
import com.bbva.ksmk.dto.caas.OutputDTO;
import com.bbva.ksmk.lib.r002.KSMKR002;
import com.bbva.pbtq.dto.validatedocument.response.host.pewu.PEMSALW5;
import com.bbva.pbtq.dto.validatedocument.response.host.pewu.PEMSALWU;
import com.bbva.pbtq.dto.validatedocument.response.host.pewu.PEWUResponse;
import com.bbva.pbtq.lib.r002.PBTQR002;
import com.bbva.pisd.dto.insurance.amazon.SignatureAWS;

import com.bbva.pisd.dto.insurance.aso.crypto.CryptoASO;
import com.bbva.pisd.dto.insurance.aso.gifole.GifoleInsuranceRequestASO;
import com.bbva.pisd.dto.insurance.aso.tier.TierASO;

import com.bbva.pisd.dto.insurance.bo.customer.CustomerBO;
import com.bbva.pisd.dto.insurance.mock.MockDTO;
import com.bbva.pisd.lib.r014.PISDR014;
import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.InsuranceLifeSimulationBO;
import com.bbva.rbvd.lib.r301.factory.ApiConnectorFactoryMock;
import com.bbva.rbvd.lib.r301.impl.RBVDR301Impl;
import com.bbva.rbvd.mock.MockBundleContext;

import com.bbva.rbvd.mock.MockService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.util.Collections;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:/META-INF/spring/RBVDR301-app.xml",
		"classpath:/META-INF/spring/RBVDR301-app-test.xml",
		"classpath:/META-INF/spring/RBVDR301-arc.xml",
		"classpath:/META-INF/spring/RBVDR301-arc-test.xml" })
public class RBVDR301Test {


	private RBVDR301Impl rbvdr301Impl = new RBVDR301Impl();

	private APIConnector externalApiConnector;

	private APIConnector internalApiConnector;

	private PISDR014 pisdr014;

	private PBTQR002 pbtqR002;

	private KSMKR002 ksmkr002;

	private MockDTO mockDTO;
	@Spy
	private Context context;

	private GifoleInsuranceRequestASO gifoleInsReqAso;

	private static final Logger LOGGER = LoggerFactory.getLogger(RBVDR301Test.class);

	private static final String MESSAGE_EXCEPTION = "Something went wrong!";

	private MockService mockService;
	private TierASO responseTier;
	private static final String HOLDER_ID = "bWZReWNSdE8";

	@Resource(name = "applicationConfigurationService")
	private ApplicationConfigurationService applicationConfigurationService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		context = new Context();
		ThreadContext.set(context);

		mockDTO = MockDTO.getInstance();
		responseTier = mockDTO.getTierMockResponse();
		mockService = mock(MockService.class);
		rbvdr301Impl.setMockService(mockService);

		applicationConfigurationService = mock(ApplicationConfigurationService.class);
		MockBundleContext mockBundleContext = mock(MockBundleContext.class);

		rbvdr301Impl.setApplicationConfigurationService(applicationConfigurationService);
		when(applicationConfigurationService.getProperty(anyString())).thenReturn("uri");

		ApiConnectorFactoryMock apiConnectorFactoryMock = new ApiConnectorFactoryMock();
		externalApiConnector = apiConnectorFactoryMock.getAPIConnector(mockBundleContext);
		rbvdr301Impl.setExternalApiConnector(externalApiConnector);

		internalApiConnector = apiConnectorFactoryMock.getAPIConnector(mockBundleContext);
		rbvdr301Impl.setInternalApiConnector(internalApiConnector);


		mockDTO = MockDTO.getInstance();

		pisdr014 = mock(PISDR014.class);
		rbvdr301Impl.setPisdR014(pisdr014);

		pbtqR002 = mock(PBTQR002.class);
		rbvdr301Impl.setPbtqR002(pbtqR002);

		ksmkr002 = mock(KSMKR002.class);
		rbvdr301Impl.setKsmkR002(ksmkr002);

		gifoleInsReqAso = new GifoleInsuranceRequestASO();


		when(pisdr014.executeSignatureConstruction(anyString(), anyString(), anyString(), anyString(), anyString()))
				.thenReturn(new SignatureAWS("", "", "", ""));
	}

	@Test
	public void executeSimulationRimacService_OK() {
		LOGGER.info("RBVDR301Test - Executing executeSimulationRimacService_OK...");

		when(this.externalApiConnector.postForObject(anyString(), anyObject(), any())).
				thenReturn(new InsuranceLifeSimulationBO());

		InsuranceLifeSimulationBO validation = rbvdr301Impl.executeSimulationRimacService(new InsuranceLifeSimulationBO(),
				"traceId");
		assertNotNull(validation);
	}

	@Test(expected = BusinessException.class)
	public void executeSimulationRimacService_RestClientException() {
		LOGGER.info("RBVDR301Test - Executing executeSimulationRimacService_RestClientException...");

		when(this.externalApiConnector.postForObject(anyString(), anyObject(), any())).
				thenThrow(new RestClientException(MESSAGE_EXCEPTION));

		this.rbvdr301Impl.executeSimulationRimacService(new InsuranceLifeSimulationBO(), "traceId");
	}

	@Test(expected = BusinessException.class)
	public void executeSimulationRimacService_TimeoutException(){
		LOGGER.info("RBVDR301Test - Executing executeSimulationRimacService_TimeoutException...");

		when(this.externalApiConnector.postForObject(anyString(), anyObject(), any())).
				thenThrow(new TimeoutException("BBVAE2008411", "Lo sentimos, el servicio de simulación de Rimac está tardando más de lo esperado. Por favor, inténtelo de nuevo más tarde."));

		this.rbvdr301Impl.executeSimulationRimacService(new InsuranceLifeSimulationBO(), "traceId");
	}

	@Test
	public void executeGifolelifeService_OK(){

		LOGGER.info("RBVDR301Test - Executing executeGifolelifeService_OK...");

		when(this.internalApiConnector.exchange(anyString(), any(HttpMethod.class), anyObject(), (Class<Void>) any()))
				.thenReturn(new ResponseEntity<>(HttpStatus.CREATED));

		Integer validation = rbvdr301Impl.executeGifolelifeService(gifoleInsReqAso);

		assertNotNull(validation);
		assertEquals(new Integer(201), validation);
	}


	@Test
	public void executeGifolelifeService_Exception(){

		LOGGER.info("RBVDR301Test - Executing executeGifolelifeService_Exception...");

		when(this.internalApiConnector.exchange(anyString(), any(HttpMethod.class), anyObject(), (Class<Void>) any()))
				.thenThrow(new RestClientException("CONNECTION ERROR"));

		Integer validation = rbvdr301Impl.executeGifolelifeService(gifoleInsReqAso);

		assertNull(validation);
	}

	@Test
	public void executeGetTierServiceOK() {
		LOGGER.info("RBVDR301Test - Executing executeGetTierServiceOK...");

		when(internalApiConnector.getForObject(anyString(), any(), anyMap()))
				.thenReturn(responseTier);

		TierASO validation = rbvdr301Impl.executeGetTierService(HOLDER_ID);
		assertNotNull(validation);
		assertNotNull(validation.getData());
		assertFalse(validation.getData().isEmpty());
		assertNotNull(validation.getData().get(0).getId());
		assertNotNull(validation.getData().get(0).getChargeFactor());
	}

	@Test
	public void executeGetTierServiceMockOK() {
		LOGGER.info("RBVDR301Test - Executing executeGetTierServiceMockOK...");

		when(mockService.isEnabledTierMock()).thenReturn(true);
		when(mockService.getTierASOMock()).thenReturn(responseTier);

		TierASO validation = rbvdr301Impl.executeGetTierService(HOLDER_ID);
		assertNotNull(validation);
		assertNotNull(validation.getData());
		assertFalse(validation.getData().isEmpty());
		assertNotNull(validation.getData().get(0).getId());
		assertNotNull(validation.getData().get(0).getChargeFactor());
	}

	@Test
	public void executeGetTierServiceWithRestClientException() {
		LOGGER.info("RBVDR301Test - Executing executeGetTierServiceWithRestClientException...");

		when(internalApiConnector.getForObject(anyString(), any(), anyMap()))
				.thenThrow(new RestClientException(MESSAGE_EXCEPTION));

		TierASO validation = rbvdr301Impl.executeGetTierService(HOLDER_ID);
		assertNull(validation);
	}


	@Test
	public void executeSimulationModificationRimacService_OK(){
		LOGGER.info("RBVDR301Test - Executing executeSimulationModificationRimacService_OK...");

		when(this.externalApiConnector.exchange(anyString(), anyObject(), anyObject(),(Class<InsuranceLifeSimulationBO>) any(),anyMap())).
				thenReturn(new ResponseEntity<>(new InsuranceLifeSimulationBO(),HttpStatus.OK));

		InsuranceLifeSimulationBO validation = rbvdr301Impl.executeSimulationModificationRimacService(
				new InsuranceLifeSimulationBO(),
				"0ab2d917-a610-4939-9401-93cd716f991c",
				"traceId");
		assertNotNull(validation);
	}

	@Test(expected = BusinessException.class)
	public void executeSimulationModificationRimacService_RestClientException() {
		LOGGER.info("RBVDR301Test - Executing executeSimulationModificationRimacService_RestClientException...");

		when(this.externalApiConnector.exchange(anyString(), anyObject(), anyObject(),(Class<InsuranceLifeSimulationBO>) any(),anyMap()))
				.thenThrow(new HttpServerErrorException(HttpStatus.BAD_REQUEST,""));
				//thenThrow(new RestClientException(MESSAGE_EXCEPTION));

		this.rbvdr301Impl.executeSimulationModificationRimacService(
				new InsuranceLifeSimulationBO(),"1kgd0-493er9-94eer01-93uuhgfgdf45cd", "traceId");
	}

	@Test(expected = BusinessException.class)
	public void executeSimulationModificationRimacService_TimeoutException() {
		LOGGER.info("RBVDR301Test - Executing executeSimulationModificationRimacService_TimeoutException...");

		when(this.externalApiConnector.exchange(anyString(), anyObject(), anyObject(),(Class<InsuranceLifeSimulationBO>) any(),anyMap())).
				thenThrow(new TimeoutException("BBVAE2008411", "Lo sentimos, el servicio de gestionar documento de rimac está tardando más de lo esperado. Por favor, inténtelo de nuevo más tarde."));

		this.rbvdr301Impl.executeSimulationModificationRimacService(
				new InsuranceLifeSimulationBO(),"1kgd0-493er9-94eer01-93uuhgfgdf45cd", "traceId");
	}

	@Test
	public void executeGetListCustomerHostOk() {
		LOGGER.info("RBVDR301Test - Executing executeRegisterAdditionalCustomerResponseOK...");

		PEWUResponse responseHost = new PEWUResponse();

		PEMSALWU data = new PEMSALWU();
		data.setTdoi("L");
		data.setSexo("M");
		data.setContact("123123123");
		data.setContac2("123123123");
		data.setContac3("123123123");
		responseHost.setPemsalwu(data);
		responseHost.setPemsalw5(new PEMSALW5());
		responseHost.setHostAdviceCode(null);
		when(pbtqR002.executeSearchInHostByCustomerId("00000000"))
				.thenReturn(responseHost);
		when(applicationConfigurationService.getProperty(anyString())).thenReturn("DNI");

		CustomerBO validation = rbvdr301Impl.executeGetCustomer("00000000");
		assertNotNull(validation);
	}

	@Test
	public void executeGetListCustomerHostWithAdvise() {
		LOGGER.info("RBVDR301Test - Executing executeGetListCustomerHostWithAdvise...");

		PEWUResponse responseHost = new PEWUResponse();
		responseHost.setHostAdviceCode("code");
		responseHost.setHostMessage("some error");
		when(pbtqR002.executeSearchInHostByCustomerId("00000000"))
				.thenReturn(responseHost);

		CustomerBO validation = rbvdr301Impl.executeGetCustomer("00000000");
		assertNull(validation);
	}

	@Test
	public void executeGetCustomerIdEncryptedOk(){
		LOGGER.info("RBVDR301Test - Executing executeGetCustomerIdEncryptedOk...");
		OutputDTO outputDTO = new OutputDTO();
		outputDTO.setData("encrypted customer id");
		outputDTO.setCodification("PLAIN");
		outputDTO.setExtraParams("extra params");

		when(ksmkr002.execute(anyList(), anyString(), anyString(), anyObject())).thenReturn(Collections.singletonList(outputDTO));

		CryptoASO cryptoASO = new CryptoASO("customerId");
		String documentEncryted = rbvdr301Impl.executeGetCustomerIdEncrypted(cryptoASO);
		assertNotNull(documentEncryted);
	}

	@Test
	public void executeGetCustomerIdEncryptedNull(){
		LOGGER.info("RBVDR301Test - Executing executeGetCustomerIdEncryptedNull...");
		when(ksmkr002.execute(anyList(), anyString(), anyString(), anyObject())).thenReturn(Collections.emptyList());
		CryptoASO cryptoASO = new CryptoASO("customerId");
		String documentEncryted  = rbvdr301Impl.executeGetCustomerIdEncrypted(cryptoASO);
		assertNull(documentEncryted);
	}

}
