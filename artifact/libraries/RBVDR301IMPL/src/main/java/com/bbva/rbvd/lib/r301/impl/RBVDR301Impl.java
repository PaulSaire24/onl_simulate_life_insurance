package com.bbva.rbvd.lib.r301.impl;

import com.bbva.ksmk.dto.caas.CredentialsDTO;
import com.bbva.ksmk.dto.caas.InputDTO;
import com.bbva.ksmk.dto.caas.OutputDTO;
import com.bbva.pbtq.dto.validatedocument.response.host.pewu.PEWUResponse;
import com.bbva.pisd.dto.insurance.amazon.SignatureAWS;

import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.pisd.dto.insurance.aso.crypto.CryptoASO;
import com.bbva.pisd.dto.insurance.aso.crypto.CryptoDataASO;
import com.bbva.pisd.dto.insurance.aso.gifole.GifoleInsuranceRequestASO;
import com.bbva.pisd.dto.insurance.aso.tier.TierASO;
import com.bbva.pisd.dto.insurance.bo.BirthDataBO;
import com.bbva.pisd.dto.insurance.bo.CountryBO;
import com.bbva.pisd.dto.insurance.bo.ContactDetailsBO;
import com.bbva.pisd.dto.insurance.bo.IdentityDocumentsBO;
import com.bbva.pisd.dto.insurance.bo.ContactTypeBO;
import com.bbva.pisd.dto.insurance.bo.GenderBO;
import com.bbva.pisd.dto.insurance.bo.DocumentTypeBO;
import com.bbva.pisd.dto.insurance.bo.customer.CustomerBO;
import com.bbva.pisd.dto.insurance.utils.PISDErrors;
import com.bbva.pisd.dto.insurance.utils.PISDProperties;
import com.bbva.pisd.dto.insurance.utils.PISDValidation;

import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.InsuranceLifeSimulationBO;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDProperties;

import com.bbva.rbvd.lib.r301.impl.util.Constans;
import com.bbva.rbvd.lib.r301.impl.util.JsonHelper;
import com.bbva.rbvd.lib.r301.impl.util.RimacExceptionHandler;
import org.apache.commons.lang3.StringUtils;
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
import java.util.Collections;
import java.util.Objects;
import java.util.Base64;

import static java.util.Collections.singletonMap;

public class RBVDR301Impl extends RBVDR301Abstract {

	private static final Logger LOGGER = LoggerFactory.getLogger(RBVDR301Impl.class);

	private static final String AUTHORIZATION = "Authorization";

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

	public CustomerListASO executeCallListCustomerResponse(String customerId) {
		LOGGER.info("***** RBVDR301Impl - executeCallListCustomerResponse START *****");
		Map<String, Object> pathParams = new HashMap<>();
		pathParams.put("customerId", customerId);

		CustomerListASO responseList= null;

		try {
			responseList = this.internalApiConnector.getForObject(PISDProperties.ID_API_CUSTOMER_INFORMATION.getValue(), CustomerListASO.class, pathParams);
			LOGGER.info("***** RBVDR301Impl - executeCallListCustomerResponse ***** Custumer body: {}",responseList);
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

	@Override
	public CustomerListASO executeGetListCustomerHost(String customerId){
		LOGGER.info("***** RBVDR301Impl - executeGetListCustomer Start *****");
		PEWUResponse result = this.pbtqR002.executeSearchInHostByCustomerId(customerId);
		LOGGER.info("***** RBVDR301Impl - executeGetListCustomer  ***** Response Host: {}", result);
		if( Objects.isNull(result.getHostAdviceCode()) || result.getHostAdviceCode().isEmpty()){
			CustomerListASO customerList = new CustomerListASO();
			/* section customer data */
			CustomerBO customer = new CustomerBO();
			customer.setCustomerId(result.getPemsalwu().getNroclie());
			customer.setFirstName(result.getPemsalwu().getNombres());
			customer.setLastName(result.getPemsalwu().getApellip());
			customer.setSecondLastName(result.getPemsalwu().getApellim());
			customer.setBirthData(new BirthDataBO());
			customer.getBirthData().setBirthDate(result.getPemsalwu().getFechan());
			customer.getBirthData().setCountry(new CountryBO());
			customer.getBirthData().getCountry().setId(result.getPemsalwu().getPaisn());
			customer.setGender(new GenderBO());
			customer.getGender().setId(result.getPemsalwu().getSexo().equals("M") ? "MALE" : "FEMALE");

			/* section identity document*/
			IdentityDocumentsBO identityDocumentsBO = new IdentityDocumentsBO();
			identityDocumentsBO.setDocumentNumber(result.getPemsalwu().getNdoi());
			identityDocumentsBO.setDocumentType(new DocumentTypeBO());

			/* map document type host ? yes*/
			identityDocumentsBO.getDocumentType().setId(this.applicationConfigurationService.getProperty(result.getPemsalwu().getTdoi()));

			identityDocumentsBO.setExpirationDate(result.getPemsalwu().getFechav());
			customer.setIdentityDocuments(Collections.singletonList(identityDocumentsBO));

			/* section contact Details */
			List<ContactDetailsBO> contactDetailsBOList = new ArrayList<>();

			/* section contact PHONE_NUMBER */
			LOGGER.info("***** PISDR008Impl - executeGetCustomerHost  ***** Map getTipocon: {}", result.getPemsalwu().getTipocon());
			if (StringUtils.isNotEmpty(result.getPemsalwu().getContact())) {
				ContactDetailsBO contactDetailPhone = new ContactDetailsBO();
				contactDetailPhone.setContactDetailId(result.getPemsalwu().getIdencon());
				contactDetailPhone.setContact(result.getPemsalwu().getContact());
				contactDetailPhone.setContactType(new ContactTypeBO());
				contactDetailPhone.getContactType().setId("PHONE_NUMBER");
				contactDetailPhone.getContactType().setName(result.getPemsalw5().getDescmco());
				contactDetailsBOList.add(contactDetailPhone);
			}

			/* section contact2 type, validate MOBILE_NUMBER */
			LOGGER.info("***** PISDR008Impl - executeGetCustomerHost  ***** Map getTipoco2: {}", result.getPemsalwu().getTipoco2());
			if (StringUtils.isNotEmpty(result.getPemsalwu().getContac2())) {
				ContactDetailsBO contactDetailMobileNumber = new ContactDetailsBO();
				contactDetailMobileNumber.setContactDetailId(result.getPemsalwu().getIdenco2());
				contactDetailMobileNumber.setContact(result.getPemsalwu().getContac2());
				contactDetailMobileNumber.setContactType(new ContactTypeBO());
				contactDetailMobileNumber.getContactType().setId("MOBILE_NUMBER");
				contactDetailMobileNumber.getContactType().setName(result.getPemsalw5().getDescmc1());
				contactDetailsBOList.add(contactDetailMobileNumber);
			}

			/* section contact2 type, validate EMAIL */
			LOGGER.info("***** PISDR008Impl - executeGetCustomerHost  ***** Map getTipoco3: {}", result.getPemsalwu().getTipoco3());
			if (StringUtils.isNotEmpty(result.getPemsalwu().getContac3())) {
				ContactDetailsBO contactDetailEmail = new ContactDetailsBO();
				contactDetailEmail.setContactDetailId(result.getPemsalwu().getIdenco3());
				contactDetailEmail.setContact(result.getPemsalwu().getContac3());
				contactDetailEmail.setContactType(new ContactTypeBO());
				contactDetailEmail.getContactType().setId("EMAIL");
				contactDetailEmail.getContactType().setName(result.getPemsalw5().getDescmc2());
				contactDetailsBOList.add(contactDetailEmail);
			}

			customer.setContactDetails(contactDetailsBOList);
			/* section contact Details */

			customerList.setData(Collections.singletonList(customer));
			LOGGER.info("***** RBVDR301Impl - executeGetListCustomer End ***** ListCustomer: {}", customerList);
			return customerList;
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
	//Ejecuta el servicio Cryto (entrada: cryptoASO)
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

	public CryptoASO executeGetCustomerIdEncrypted(CryptoASO cryptoASO){
		LOGGER.info("***** RBVDR301Impl - executeGetCustomerIdEncrypted START *****");
		String appName = this.applicationConfigurationService.getProperty(Constans.APP_NAME);
		String password =  Constans.OAUTH_TOKEN;
		String credExtraParams = this.applicationConfigurationService.getProperty(Constans.CRE_EXTRA_PARAMS);
		String inputContext = this.applicationConfigurationService.getProperty(Constans.INPUT_TEXT_SECURITY);  //provided by security
		List<InputDTO> listDecodedCredential = new ArrayList<>();

		listDecodedCredential.add(new InputDTO(Base64.getEncoder().encodeToString(cryptoASO.getStream().getBytes()), "B64URL"));

		List<OutputDTO> listEncodedCredentials = ksmkR002.execute(listDecodedCredential, "", inputContext, new CredentialsDTO(appName, password, credExtraParams));
		if (Objects.nonNull(listEncodedCredentials) && !CollectionUtils.isEmpty(listEncodedCredentials)) {
			LOGGER.info("***** RBVDR301Impl - executeGetCustomerIdEncrypted ***** encoded: {}", listEncodedCredentials );
			cryptoASO.setData(new CryptoDataASO());
			cryptoASO.getData().setDocument(listEncodedCredentials.get(0).getData());
			LOGGER.info("***** RBVDR301Impl - executeGetCustomerIdEncrypted END *****: cryptoAso: {}", cryptoASO);
			return cryptoASO;
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
		MediaType mediaType = new MediaType("application","json", StandardCharsets.UTF_8);
		headers.setContentType(mediaType);
		return headers;
	}
	//Crea las cabeceras AWS de Http
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
