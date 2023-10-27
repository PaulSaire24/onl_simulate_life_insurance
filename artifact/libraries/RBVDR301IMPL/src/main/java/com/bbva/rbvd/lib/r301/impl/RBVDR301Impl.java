package com.bbva.rbvd.lib.r301.impl;

import com.bbva.ksmk.dto.caas.CredentialsDTO;
import com.bbva.ksmk.dto.caas.InputDTO;
import com.bbva.ksmk.dto.caas.OutputDTO;
import com.bbva.pbtq.dto.validatedocument.response.host.pewu.PEWUResponse;
import com.bbva.pisd.dto.insurance.amazon.SignatureAWS;

import com.bbva.pisd.dto.insurance.aso.crypto.CryptoASO;
import com.bbva.pisd.dto.insurance.aso.gifole.GifoleInsuranceRequestASO;
import com.bbva.pisd.dto.insurance.aso.tier.TierASO;
import com.bbva.pisd.dto.insurance.bo.customer.CustomerBO;
import com.bbva.pisd.dto.insurance.utils.PISDErrors;
import com.bbva.pisd.dto.insurance.utils.PISDProperties;
import com.bbva.pisd.dto.insurance.utils.PISDValidation;

import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.InsuranceLifeSimulationBO;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDProperties;

import com.bbva.rbvd.lib.r301.impl.transform.bean.CustomerBOBean;
import com.bbva.rbvd.lib.r301.impl.util.JsonHelper;
import com.bbva.rbvd.lib.r301.impl.util.RimacExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClientException;

import javax.ws.rs.HttpMethod;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Base64;

import static java.util.Collections.singletonMap;

import static com.bbva.rbvd.dto.lifeinsrc.utils.RBVDConstants.Crypto.APP_NAME;
import static com.bbva.rbvd.dto.lifeinsrc.utils.RBVDConstants.Crypto.OAUTH_TOKEN;
import static com.bbva.rbvd.dto.lifeinsrc.utils.RBVDConstants.Crypto.CRE_EXTRA_PARAMS;
import static com.bbva.rbvd.dto.lifeinsrc.utils.RBVDConstants.Crypto.INPUT_TEXT_SECURITY;
import static com.bbva.rbvd.dto.lifeinsrc.utils.RBVDConstants.Crypto.B64URL;
import static com.bbva.rbvd.dto.lifeinsrc.utils.RBVDConstants.Headers.AUTHORIZATION;
import static com.bbva.rbvd.dto.lifeinsrc.utils.RBVDConstants.Headers.APPLICATION;
import static com.bbva.rbvd.dto.lifeinsrc.utils.RBVDConstants.Headers.JSON;
import static com.bbva.rbvd.dto.lifeinsrc.utils.RBVDConstants.Headers.AMZ_DATE;
import static com.bbva.rbvd.dto.lifeinsrc.utils.RBVDConstants.Headers.API_KEY;
import static com.bbva.rbvd.dto.lifeinsrc.utils.RBVDConstants.Headers.TRACE_ID;

public class RBVDR301Impl extends RBVDR301Abstract {

	private static final Logger LOGGER = LoggerFactory.getLogger(RBVDR301Impl.class);


	//ejecuta la simulación del servicio Rímac
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

	@Override
	public InsuranceLifeSimulationBO executeSimulationModificationRimacService(InsuranceLifeSimulationBO payload, String quotationId, String traceId) {

		LOGGER.info("***** RBVDR301Impl - executeSimulationModificationRimacService START *****");

		String requestJson = getRequestJson(payload);

		LOGGER.info("***** RBVDR301Impl - executeSimulationModificationRimacService ***** Request body: {}", requestJson);

		InsuranceLifeSimulationBO response = null;

		String uri = this.applicationConfigurationService.getProperty(RBVDProperties.SIMULATION_UPDATE_LIFE_URI.getValue()).
				replace("{idCotizacion}", quotationId);

		SignatureAWS signatureAWS = this.pisdR014.executeSignatureConstruction(requestJson, org.springframework.http.HttpMethod.PATCH.toString(),
				uri,null, traceId);

		HttpEntity<String> entity = new HttpEntity<>(requestJson, createHttpHeadersAWS(signatureAWS));

		try {

			ResponseEntity<InsuranceLifeSimulationBO> output =this.externalApiConnector.exchange(
					RBVDProperties.SIMULATION_UPDATE_LIFE_RIMAC.getValue(), org.springframework.http.HttpMethod.PATCH,entity,InsuranceLifeSimulationBO.class,singletonMap("idCotizacion",quotationId));

			response = output.getBody();
			LOGGER.info("***** RBVDR301Impl - executeSimulationModificationRimacService ***** Response: {}", getRequestJson(response));
			return response;
		} catch (RestClientException ex) {
			LOGGER.debug("***** RBVDR301Impl - executeSimulationModificationRimacService ***** Exception: {}", ex.getMessage());
			RimacExceptionHandler exceptionHandler = new RimacExceptionHandler();
			exceptionHandler.handler(ex);
			return null;
		}

	}

	private String getRequestJson(Object o) {
		return JsonHelper.getInstance().serialization(o);
	}


	@Override
	public CustomerBO executeGetCustomer(String customerId){
		LOGGER.info("***** RBVDR301Impl - executeGetListCustomer Start *****");
		PEWUResponse result = this.pbtqR002.executeSearchInHostByCustomerId(customerId);
		LOGGER.info("***** RBVDR301Impl - executeGetListCustomer  ***** Response Host: {}", result);

		if( Objects.isNull(result.getHostAdviceCode()) || result.getHostAdviceCode().isEmpty()){
			CustomerBOBean customerListAsoBean = new CustomerBOBean(this.applicationConfigurationService);
			return customerListAsoBean.mapperCustomer(result);
		}
		this.addAdviceWithDescription(result.getHostAdviceCode(), result.getHostMessage());
		LOGGER.info("***** RBVDR301Impl - executeGetListCustomer ***** with error: {}", result.getHostMessage());
		return null;
	}

	//ejecuta el servicio de Gifole para vida
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

	public String executeGetCustomerIdEncrypted(CryptoASO cryptoASO){
		LOGGER.info("***** RBVDR301Impl - executeGetCustomerIdEncrypted START *****");
		String appName = this.applicationConfigurationService.getProperty(APP_NAME);
		String password =  OAUTH_TOKEN;
		String credExtraParams = this.applicationConfigurationService.getProperty(CRE_EXTRA_PARAMS);
		String inputContext = this.applicationConfigurationService.getProperty(INPUT_TEXT_SECURITY);  //provided by security
		List<InputDTO> listDecodedCredential = new ArrayList<>();

		listDecodedCredential.add(new InputDTO(Base64.getEncoder().encodeToString(cryptoASO.getStream().getBytes()), B64URL));

		List<OutputDTO> listEncodedCredentials = ksmkR002.execute(listDecodedCredential, "", inputContext, new CredentialsDTO(appName, password, credExtraParams));
		if (Objects.nonNull(listEncodedCredentials) && !CollectionUtils.isEmpty(listEncodedCredentials)) {
			return listEncodedCredentials.get(0).getData();
		}
		LOGGER.info("***** RBVDR301Impl - executeGetCustomerIdEncrypted END with error *****");

		return null;
	}

	//Ejecuta para obtener el servicio Tier
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
	//Crea las cabeceras Http
	private HttpHeaders createHttpHeaders() {
		HttpHeaders headers = new HttpHeaders();
		MediaType mediaType = new MediaType(APPLICATION,JSON, StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		return headers;
	}
	//Crea las cabeceras AWS de Http
	private HttpHeaders createHttpHeadersAWS(SignatureAWS signature) {
		HttpHeaders headers = new HttpHeaders();
		MediaType mediaType = new MediaType(APPLICATION, JSON, StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		headers.set(AUTHORIZATION, signature.getAuthorization());
		headers.set(AMZ_DATE, signature.getxAmzDate());
		headers.set(API_KEY, signature.getxApiKey());
		headers.set(TRACE_ID, signature.getTraceId());
		return headers;
	}
}
