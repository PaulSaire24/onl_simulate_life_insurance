package com.bbva.rbvd.lib.r301;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.elara.domain.transaction.Context;
import com.bbva.elara.domain.transaction.ThreadContext;
import javax.annotation.Resource;

import com.bbva.elara.utility.api.connector.APIConnector;
import com.bbva.pisd.dto.insurance.amazon.SignatureAWS;
import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.pisd.dto.insurance.aso.crypto.CryptoASO;
import com.bbva.pisd.dto.insurance.aso.gifole.GifoleInsuranceRequestASO;
import com.bbva.pisd.dto.insurance.aso.tier.TierASO;
import com.bbva.pisd.dto.insurance.mock.MockDTO;
import com.bbva.pisd.lib.r014.PISDR014;
import com.bbva.rbvd.dto.lifeinsrc.mock.MockData;
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
import org.springframework.web.client.RestClientException;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
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

	private MockData mockData;

	private MockDTO mockDTO;
	@Spy
	private Context context;

	private CustomerListASO customerList;

	private GifoleInsuranceRequestASO gifoleInsReqAso;

	private static final Logger LOGGER = LoggerFactory.getLogger(RBVDR301Test.class);

	private static final String MESSAGE_EXCEPTION = "Something went wrong!";

	private MockService mockService;
	private CryptoASO inputCrypto;
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
		inputCrypto = mock(CryptoASO.class);
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

		mockData = MockData.getInstance();

		mockDTO = MockDTO.getInstance();

		pisdr014 = mock(PISDR014.class);
		rbvdr301Impl.setPisdR014(pisdr014);

		gifoleInsReqAso = new GifoleInsuranceRequestASO();

		customerList = mockDTO.getCustomerDataResponse();

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

	@Test
	public void executeCallListCustomerResponseOK() {
		LOGGER.info("RBVDR301Test - Executing executeRegisterAdditionalCustomerResponseOK...");

		when(internalApiConnector.getForObject(anyString(), any(), anyMap()))
				.thenReturn(customerList);

		CustomerListASO validation = rbvdr301Impl.executeCallListCustomerResponse("00000000");
		assertNotNull(validation);
		assertNotNull(validation.getData().get(0).getFirstName());
	}

	@Test
	public void executeCallListCustomerResponseNull() {
		LOGGER.info("RBVDR301Test - Executing executeCallListCustomerResponseNull...");

		when(internalApiConnector.getForObject(anyString(), any(), anyMap()))
				.thenReturn(null);

		CustomerListASO validation = rbvdr301Impl.executeCallListCustomerResponse("00000000");
		assertNull(validation);
	}

	@Test
	public void executeCallListCustomerResponseWithRestClientException() {
		LOGGER.info("RBVDR301Test - Executing executeCallListCustomerResponseWithRestClientException...");
		when(internalApiConnector.getForObject(anyString(), any(), anyMap()))
				.thenThrow(new RestClientException(MESSAGE_EXCEPTION));

		CustomerListASO validation = rbvdr301Impl.executeCallListCustomerResponse("00000000");
		assertNull(validation);
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
	public void executeCryptoServiceOK() throws IOException {
		LOGGER.info("RBVDR301Test - Executing executeCryptoServiceOK...");

		CryptoASO responseCrypto = mockDTO.getCryptoMockResponse();

		when(internalApiConnector.postForObject(anyString(), anyObject(), any())).
				thenReturn(responseCrypto);
		CryptoASO validation = rbvdr301Impl.executeCryptoService(inputCrypto);
		assertNotNull(validation);
		assertNotNull(validation.getData());
		assertNotNull(validation.getData().getDocument());
	}

	@Test
	public void executeCryptoServiceWithRestClientException() {
		LOGGER.info("RBVDR301Test - Executing executeCryptoServiceWithRestClientException...");

		when(internalApiConnector.postForObject(anyString(), anyObject(), any())).
				thenThrow(new RestClientException(MESSAGE_EXCEPTION));
		CryptoASO validation = rbvdr301Impl.executeCryptoService(inputCrypto);
		assertNull(validation);
	}

}
