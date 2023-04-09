package com.bbva.rbvd.lib.r302;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.elara.domain.transaction.Context;
import com.bbva.elara.domain.transaction.ThreadContext;
import javax.annotation.Resource;

import com.bbva.rbvd.dto.lifeinsrc.bo.simulation.InsuranceLifeSimulationBO;
import com.bbva.rbvd.dto.lifeinsrc.mock.MockData;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;
import com.bbva.rbvd.lib.r301.RBVDR301;
import com.bbva.rbvd.lib.r302.impl.RBVDR302Impl;
import com.bbva.rbvd.lib.r302.impl.util.MapperHelper;
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

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		context = new Context();
		ThreadContext.set(context);

		mockData = MockData.getInstance();

		rbvdr301 = mock(RBVDR301.class);
		rbvdR302.setRbvdR301(rbvdr301);

		responseRimac = mockData.getInsuranceRimacSimulationResponse();

		requestRimac = mockData.getInsuranceRimacSimulationRequest();

		requestInput = mockData.getInsuranceSimulationRequest();

		responseInput = mockData.getInsuranceSimulationResponse();

		mapperHelper = mock(MapperHelper.class);
		rbvdR302.setMapperHelper(mapperHelper);

		input = new LifeSimulationDTO();
	}

	@Test
	public void executeGetGenerateTest() throws IOException {
		LOGGER.info("RBVDR302Test - Executing executeGetGenerateTest...");
		when(this.mapperHelper.mapInRequestRimacLife(anyObject())).thenReturn(requestRimac);
		when(this.rbvdr301.executeSimulationRimacService(anyObject(), anyString())).thenReturn(responseRimac);

		LifeSimulationDTO validation = this.rbvdR302.executeGetSimulation(input);

		assertNull(validation);
	}

	@Test
	public void executeGetGenerateTest_Exception() throws IOException {
		LOGGER.info("RBVDR302Test - Executing executeGetGenerateTest_Exception...");
		when(this.mapperHelper.mapInRequestRimacLife(anyObject())).thenReturn(null);
		when(this.rbvdr301.executeSimulationRimacService(anyObject(), anyString())).thenReturn(null);

		LifeSimulationDTO validation = this.rbvdR302.executeGetSimulation(input);

		assertNull(validation);
	}

}
