package com.bbva.rbvd.lib.r301.impl;

import com.bbva.pisd.dto.insurance.amazon.SignatureAWS;
import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.pisd.dto.insurance.aso.crypto.CryptoASO;
import com.bbva.pisd.dto.insurance.aso.gifole.GifoleInsuranceRequestASO;
import com.bbva.pisd.dto.insurance.aso.tier.TierASO;
import com.bbva.pisd.dto.insurance.utils.PISDErrors;
import com.bbva.pisd.dto.insurance.utils.PISDProperties;
import com.bbva.pisd.dto.insurance.utils.PISDValidation;
import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.InsuranceLifeSimulationBO;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDProperties;
import com.bbva.rbvd.lib.r301.impl.util.JsonHelper;
import com.bbva.rbvd.lib.r301.impl.util.RimacExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import javax.ws.rs.HttpMethod;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


public class RBVDR301Impl extends RBVDR301Abstract {

	private static final Logger LOGGER = LoggerFactory.getLogger(RBVDR301Impl.class);

	private static final String AUTHORIZATION = "Authorization";


	@Override
	public InsuranceLifeSimulationBO executeSimulationRimacService(final InsuranceLifeSimulationBO payload, String traceId) {

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

	public CustomerListASO executeCallListCustomerResponse(String customerId) {
		LOGGER.info("***** RBVDR301Impl - executeCallListCustomerResponse START *****");

		Map<String, Object> pathParams = new HashMap<>();
		pathParams.put("customerId", customerId);

		CustomerListASO responseList= null;

		try {
			responseList = this.internalApiConnector.getForObject(PISDProperties.ID_API_CUSTOMER_INFORMATION.getValue(), CustomerListASO.class, pathParams);
			if (responseList != null) {
				LOGGER.info("***** RBVDR301Impl - executeCallListCustomerResponse ***** Response body: {}",
						getRequestJson(responseList.getData().get(0)));
			}

		} catch(RestClientException e) {
			LOGGER.info("***** RBVDR301Impl - executeCallListCustomerResponse ***** Exception: {}", e.getMessage());
			this.addAdvice(PISDErrors.ERROR_CALL_TO_THIRD_PARTY.getAdviceCode());
		}

		LOGGER.info("***** RBVDR301Impl - executeCallListCustomerResponse END *****");
		return responseList;
	}


	public Integer executeGifolelifeService(GifoleInsuranceRequestASO requestBody) {
		LOGGER.info("***** RBVDR301Impl - executeGifolelifeService START *****");

		String jsonString = getRequestJson(requestBody);

		LOGGER.info("***** RBVDR301Impl - executeGifolelifeService ***** Request body: {}", jsonString);

		ResponseEntity<Void> response = null;
		Integer httpStatus = null;

		HttpEntity<String> entity = new HttpEntity<>(jsonString, createHttpHeaders());

		LOGGER.info("var ENTITY: {}", entity);

		try {
			response = this.internalApiConnector.exchange(PISDProperties.ID_API_GIFOLE_ROYAL_INSURANCE_REQUEST_SERVICE.getValue(),
					org.springframework.http.HttpMethod.POST, entity, Void.class);
			httpStatus = response.getStatusCode().value();
			LOGGER.info("***** RBVDR301Impl - executeGifolelifeService ***** Http code response: {}", httpStatus);
		} catch(RestClientException ex) {
			LOGGER.debug("***** RBVDR301Impl - executeGifolelifeService ***** Exception: {}", ex.getMessage());
			this.addAdvice(PISDErrors.ERROR_CONNECTION_GIFOLE_ROYAL_INSURANCE_REQUEST_ASO_SERVICE.getAdviceCode());
		}

		LOGGER.info("***** RBVDR301Impl - executeGifolelifeService END *****");
		return httpStatus;
	}

	public CryptoASO executeCryptoService(CryptoASO cryptoASO) {
		LOGGER.info("***** RBVDR301Impl - executeCryptoService START *****");
		LOGGER.info("***** RBVDR301Impl - executeCryptoService ***** Param: {}", cryptoASO.getStream());

		CryptoASO output = null;

		HttpEntity<CryptoASO> entity = new HttpEntity<>(cryptoASO, createHttpHeaders());

		try {
			output = this.internalApiConnector.postForObject(PISDProperties.ID_API_CRYPTO.getValue(), entity,
					CryptoASO.class);
		} catch(RestClientException e) {
			LOGGER.debug("***** RBVDR301Impl - executeCryptoService ***** Exception: {}", e.getMessage());
			PISDValidation.build(PISDErrors.ERROR_CONNECTION_CRYPTO_ASO_SERVICE);
		}

		LOGGER.info("***** RBVDR301Impl - executeCryptoService ***** Response: {}", output);
		LOGGER.info("***** RBVDR301Impl - executeCryptoService END *****");
		return output;
	}


	public TierASO executeGetTierService(String holderId) {
		LOGGER.info("***** RBVDR301Impl - executeGetTierService START *****");
		LOGGER.info("***** RBVDR301Impl - executeGetTierService ***** Params: {}", holderId);

		TierASO output = null;

		if(this.mockService.isEnabledTierMock()) {
			LOGGER.info("***** RBVDR301Impl - executeGetTierService [MOCK DATA] *****");
			return this.mockService.getTierASOMock();
		}

		Map<String, Object> pathParams = new HashMap<>();
		pathParams.put("holderId", holderId);

		try {
			output = this.internalApiConnector.getForObject(PISDProperties.ID_API_TIER.getValue(), TierASO.class, pathParams);
		} catch(RestClientException e) {
			LOGGER.debug("***** RBVDR301Impl - executeGetTierService ***** Exception: {}", e.getMessage());
			PISDValidation.build(PISDErrors.ERROR_CONNECTION_TIER_ASO_SERVICE);
		}

		LOGGER.info("***** RBVDR301Impl - executeGetTierService ***** Response: {}", output);
		LOGGER.info("***** RBVDR301Impl - executeGetTierService END *****");
		return output;
	}


	private HttpHeaders createHttpHeaders() {
		HttpHeaders headers = new HttpHeaders();
		MediaType mediaType = new MediaType("application","json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		return headers;
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
