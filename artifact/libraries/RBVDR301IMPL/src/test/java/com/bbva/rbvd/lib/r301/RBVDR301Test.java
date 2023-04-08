package com.bbva.rbvd.lib.r301;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.elara.domain.transaction.Context;
import com.bbva.elara.domain.transaction.ThreadContext;
import javax.annotation.Resource;

import com.bbva.elara.utility.api.connector.APIConnector;
import com.bbva.pisd.dto.insurance.amazon.SignatureAWS;
import com.bbva.pisd.lib.r014.PISDR014;
import com.bbva.rbvd.dto.lifeinsrc.bo.simulation.InsuranceLifeSimulationBO;
import com.bbva.rbvd.dto.lifeinsrc.mock.MockData;
import com.bbva.rbvd.lib.r301.factory.ApiConnectorFactoryTest;
import com.bbva.rbvd.lib.r301.impl.RBVDR301Impl;
import com.bbva.rbvd.mock.MockBundleContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.Advised;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestClientException;

import static org.junit.Assert.assertNotNull;
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

	private PISDR014 pisdr014;

	private MockData mockData;
	@Spy
	private Context context;

	private static final Logger LOGGER = LoggerFactory.getLogger(RBVDR301Test.class);

	private static final String MESSAGE_EXCEPTION = "Something went wrong!";

	@Resource(name = "applicationConfigurationService")
	private ApplicationConfigurationService applicationConfigurationService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		context = new Context();
		ThreadContext.set(context);

		applicationConfigurationService = mock(ApplicationConfigurationService.class);
		MockBundleContext mockBundleContext = mock(MockBundleContext.class);

		rbvdr301Impl.setApplicationConfigurationService(applicationConfigurationService);
		when(applicationConfigurationService.getProperty(anyString())).thenReturn("uri");

		ApiConnectorFactoryTest apiConnectorFactoryMock = new ApiConnectorFactoryTest();
		externalApiConnector = apiConnectorFactoryMock.getAPIConnector(mockBundleContext);
		rbvdr301Impl.setExternalApiConnector(externalApiConnector);


		mockData = MockData.getInstance();

		pisdr014 = mock(PISDR014.class);
		rbvdr301Impl.setPisdR014(pisdr014);

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
	
}
