package com.bbva.rbvd.lib.r301.impl;

import com.bbva.pisd.dto.insurance.amazon.SignatureAWS;
import com.bbva.rbvd.dto.lifeinsrc.bo.simulation.InsuranceLifeSimulationBO;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDProperties;
import com.bbva.rbvd.lib.r301.impl.util.JsonHelper;
import com.bbva.rbvd.lib.r301.impl.util.RimacExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;

import javax.ws.rs.HttpMethod;
import java.nio.charset.StandardCharsets;

/**
 * The RBVDR301Impl class...
 */
public class RBVDR301Impl extends RBVDR301Abstract {

	private static final Logger LOGGER = LoggerFactory.getLogger(RBVDR301Impl.class);

	private static final String AUTHORIZATION = "Authorization";

	/**
	 * The execute method...
	 */
	@Override
	public InsuranceLifeSimulationBO executeSimulationRimacService(final InsuranceLifeSimulationBO payload, String traceId) {
		// TODO - Implementation of business logic
		String requestJson = getRequestJson(payload);

		LOGGER.info("***** RBVDR301Impl - executeSimulationRimacService ***** Request body: {}", requestJson);

		String uri = this.applicationConfigurationService.getProperty(RBVDProperties.SIMULATION_LIFE_URI.getValue());

		SignatureAWS signatureAWS = this.pisdR014.executeSignatureConstruction(requestJson , HttpMethod.POST,
				uri, null, traceId);

		HttpEntity<String> entity = new HttpEntity<>(requestJson, createHttpHeadersAWS(signatureAWS));

		InsuranceLifeSimulationBO response = null;
		try {
			response = this.externalApiConnector.postForObject(RBVDProperties.SIMULATION_LIFE_RIMAC.getValue(), entity, InsuranceLifeSimulationBO.class);
			LOGGER.info("***** RBVDR301Impl - executeSimulationRimacService ***** Response: {}", getRequestJson(response));
		} catch(RestClientException ex) {
			LOGGER.debug("***** RBVDR301Impl - executeSimulationRimacService ***** Exception: {}", ex.getMessage());
			RimacExceptionHandler exceptionHandler = new RimacExceptionHandler();
			exceptionHandler.handler(ex);
		}
		return response;

	}

	private String getRequestJson(Object o) {
		return JsonHelper.getInstance().serialization(o);
	}

	private HttpHeaders createHttpHeadersAWS(SignatureAWS signature) {
		HttpHeaders headers = new HttpHeaders();
		MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.set(AUTHORIZATION, signature.getAuthorization());
		headers.set("X-Amz-Date", signature.getxAmzDate());
		headers.set("x-api-key", signature.getxApiKey());
		headers.set("traceId", signature.getTraceId());
		return headers;
	}
}
