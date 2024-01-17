package com.bbva.rbvd;

import com.bbva.elara.domain.transaction.Context;
import com.bbva.elara.domain.transaction.RequestHeaderParamsName;
import com.bbva.elara.domain.transaction.Severity;
import com.bbva.elara.domain.transaction.TransactionParameter;
import com.bbva.elara.domain.transaction.request.TransactionRequest;
import com.bbva.elara.domain.transaction.request.body.CommonRequestBody;
import com.bbva.elara.domain.transaction.request.header.CommonRequestHeader;
import com.bbva.elara.test.osgi.DummyBundleContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;

import com.bbva.rbvd.dto.lifeinsrc.commons.TermDTO;
import com.bbva.rbvd.dto.lifeinsrc.mock.MockData;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.ParticipantDTO;
import com.bbva.rbvd.lib.r302.RBVDR302;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;

/**
 * Test for transaction RBVDT30101PETransaction
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:/META-INF/spring/elara-test.xml",
		"classpath:/META-INF/spring/RBVDT30101PETest.xml" })
public class RBVDT30101PETransactionTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(RBVDT30101PETransactionTest.class);

	@Resource
	private RBVDR302 rbvdr302;
	@Autowired
	private RBVDT30101PETransaction transaction;

	@Resource(name = "dummyBundleContext")
	private DummyBundleContext bundleContext;

	@Mock
	private CommonRequestHeader header;

	@Mock
	private TransactionRequest transactionRequest;

	private MockData mockData;

	@Before
	public void initializeClass() throws Exception {
		MockitoAnnotations.initMocks(this);

		this.transaction.start(bundleContext);
		this.transaction.setContext(new Context());

		CommonRequestBody commonRequestBody = new CommonRequestBody();
		commonRequestBody.setTransactionParameters(new ArrayList<>());

		this.transactionRequest.setBody(commonRequestBody);

		this.transactionRequest.setHeader(header);

		when(header.getHeaderParameter(RequestHeaderParamsName.REQUESTID)).thenReturn("traceId");

		this.transaction.getContext().setTransactionRequest(transactionRequest);

		mockData = MockData.getInstance();
	}

	private List<ParticipantDTO> createParticipants(){
		List<ParticipantDTO> participants = new ArrayList<>();
		ParticipantDTO participnt1 = new ParticipantDTO();
		participnt1.setId("89485832");
		participnt1.setName("Peter");
		participants.add(participnt1);
		return participants;
	}

	private TermDTO createTerm(){
		TermDTO term = new TermDTO();
		term.setNumber(5);
		return term;
	}

	@Test
	public void testNotNull() throws IOException {
		LOGGER.info("RBVDT30101PETransactionTest - Executing testNotNull...");

		assertNotNull(this.transaction);

		LifeSimulationDTO life = mockData.getInsuranceSimulationResponse();
		life.setParticipants(createParticipants());
		life.setTerm(createTerm());
		life.setEndorsed(false);

		when(rbvdr302.executeGetSimulation(anyObject())).thenReturn(life);
		Assert.assertNotNull(this.transaction);

		this.transaction.execute();

		assertEquals(Severity.OK, this.transaction.getSeverity());
	}

	@Test
	public void testNull() {
		LOGGER.info("RBVDT30101PETransactionTest - Executing testNull...");

		assertNotNull(this.transaction);

		when(rbvdr302.executeGetSimulation(anyObject())).thenReturn(null);
		this.transaction.execute();

		assertEquals(Severity.EWR, this.transaction.getSeverity());
	}

	// Add Parameter to Transaction
	private void addParameter(final String parameter, final Object value) {
		final TransactionParameter tParameter = new TransactionParameter(parameter, value);
		transaction.getContext().getParameterList().put(parameter, tParameter);
	}

	// Get Parameter from Transaction
	private Object getParameter(final String parameter) {
		final TransactionParameter param = transaction.getContext().getParameterList().get(parameter);
		return param != null ? param.getValue() : null;
	}
}
