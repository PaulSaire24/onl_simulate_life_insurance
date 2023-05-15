package com.bbva.rbvd.lib.r302.impl;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.pisd.dto.insurance.aso.CustomerListASO;

import com.bbva.pisd.dto.insurance.aso.crypto.CryptoASO;
import com.bbva.pisd.dto.insurance.aso.gifole.GifoleInsuranceRequestASO;
import com.bbva.pisd.dto.insurance.aso.tier.TierASO;
import com.bbva.pisd.dto.insurance.utils.PISDProperties;

import com.bbva.rbvd.dto.lifeinsrc.commons.InsurancePlanDTO;
import com.bbva.rbvd.dto.lifeinsrc.dao.InsuranceProductModalityDAO;
import com.bbva.rbvd.dto.lifeinsrc.dao.ProductInformationDAO;
import com.bbva.rbvd.dto.lifeinsrc.dao.SimulationDAO;
import com.bbva.rbvd.dto.lifeinsrc.dao.SimulationProductDAO;
import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.InsuranceLifeSimulationBO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDErrors;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDProperties;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDValidation;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Date;

import static java.util.stream.Collectors.toList;
import static org.springframework.util.CollectionUtils.isEmpty;


public class RBVDR302Impl extends RBVDR302Abstract {

	private static final Logger LOGGER = LoggerFactory.getLogger(RBVDR302Impl.class);

	private static final String ENABLE_GIFOLE_LIFE_ASO = "ENABLE_GIFOLE_LIFE_ASO";

	/**
	 * The execute method...
	 */
	@Override
	public LifeSimulationDTO executeGetSimulation(LifeSimulationDTO input) {
		LOGGER.info("***** RBVDR302Impl - executeGetSimulation START *****");
		LOGGER.info("***** RBVDR302Impl - executeGetSimulation ***** {}", input);

		LifeSimulationDTO response = new LifeSimulationDTO();

		try{

			String inputProductId = input.getProduct().getId();
			String documentTypeIdAsText = input.getHolder().getIdentityDocument().getDocumentType().getId();
			String documentTypeId = this.applicationConfigurationService.getProperty(input.getHolder().getIdentityDocument().getDocumentType().getId());

			input.getHolder().getIdentityDocument().getDocumentType().setId(documentTypeId);

			LOGGER.info("***** PISDR302Impl - Invoking PISDR350 QUERY_GET_PRODUCT_INFORMATION *****");
			Map<String, Object> responseQueryGetProductInformation =
					this.pisdR350.executeGetASingleRow(RBVDProperties.QUERY_GET_PRODUCT_INFORMATION.getValue(), this.mapperHelper.mapProductId(input.getProduct().getId()));

			ProductInformationDAO productInformationDAO = validateQueryGetProductInformation(responseQueryGetProductInformation);

			Map<String, Object> responseQueryGetCumulus =
					this.pisdR350.executeGetListASingleRow(RBVDProperties.QUERY_GET_INSURANCE_AMOUNT.getValue(), this.mapperHelper.mapInsuranceAmount(
							((BigDecimal) responseQueryGetProductInformation
									.get(RBVDProperties.FIELD_OR_FILTER_INSURANCE_PRODUCT_ID.getValue())), input.getHolder().getId()));

			BigDecimal sumCumulus = validateQueryGetInsuranceAmount(responseQueryGetCumulus);

			InsuranceLifeSimulationBO rimacRequest = mapperHelper.mapInRequestRimacLife(input, sumCumulus);
			rimacRequest.getPayload().setProducto(productInformationDAO.getInsuranceBusinessName());
			InsuranceLifeSimulationBO responseRimac = rbvdR301.executeSimulationRimacService(rimacRequest, input.getTraceId());
			validation(responseRimac);

			response = input;

			TierASO responseTierASO = validateTier(input);
			this.mapperHelper.mappingTierASO(input, responseTierASO);

			String segmentoLifePlan1 = applicationConfigurationService.getProperty("segmentoLifePlan1");
			String segmentoLifePlan2 = applicationConfigurationService.getProperty("segmentoLifePlan2");
			String segmentoLifePlan3 = applicationConfigurationService.getProperty("segmentoLifePlan3");

			Boolean seglifePlan1 = this.mapperHelper.selectValuePlansDescription(segmentoLifePlan1, input);
			Boolean seglifePlan2 =this.mapperHelper.selectValuePlansDescription(segmentoLifePlan2, input);
			Boolean seglifePlan3 =this.mapperHelper.selectValuePlansDescription(segmentoLifePlan3, input);

			mapperHelper.mapOutRequestRimacLife(responseRimac, response);

			String planesLife = applicationConfigurationService.getProperty("plansLife");

			Map<String, Object> filtersModalitiesInfo = this.mapperHelper.
					createModalitiesInformationFilters(planesLife, productInformationDAO.getInsuranceProductId(), input.getSaleChannelId());
			LOGGER.info("***** PISDR302Impl - Invoking PISDR350 QUERY_GET_PRODUCT_MODALITIES_INFORMATION *****");
			Map<String, Object> responseQueryModalitiesInformation =
					this.pisdR350.executeGetListASingleRow(RBVDProperties.QUERY_GET_PRODUCT_MODALITIES_INFORMATION.getValue(), filtersModalitiesInfo);
			List<InsuranceProductModalityDAO> productModalitiesDAO = validateQueryInsuranceProductModality(responseQueryModalitiesInformation);

			List<InsurancePlanDTO> plansWithNameAndRecommendedValueAndInstallmentPlan = this.mapperHelper.getPlansNamesAndRecommendedValuesAndInstallmentsPlans
					(productModalitiesDAO, responseRimac, seglifePlan1, seglifePlan2, seglifePlan3);

			response.getProduct().setPlans(plansWithNameAndRecommendedValueAndInstallmentPlan);

			Map<String, Object> arguments = new HashMap<>();
			LOGGER.info("***** PISDR302Impl - Invoking PISDR350 QUERY_SELECT_INSURANCE_SIMULATION_ID *****");
			Map<String, Object> responseGetInsuranceSimulationId = this.pisdR350.executeGetASingleRow(RBVDProperties.QUERY_SELECT_INSURANCE_SIMULATION_ID.getValue(),arguments);
			BigDecimal insuranceSimulationId = (BigDecimal) responseGetInsuranceSimulationId.get(RBVDProperties.FIELD_Q_PISD_SIMULATION_ID0_NEXTVAL.getValue());

			String creationUser = input.getCreationUser();
			String userAudit = input.getUserAudit();

			Date maturityDate = this.generateDate(responseRimac.getPayload().getCotizaciones().get(0).getFechaFinVigencia());

			SimulationDAO simulationDAO = mapperHelper.createSimulationDAO(insuranceSimulationId, maturityDate, response);

			Map<String, Object> argumentsForSaveSimulation = this.mapperHelper.createArgumentsForSaveSimulation(simulationDAO, creationUser, userAudit, documentTypeId);

			LOGGER.info("***** PISDR302Impl - Invoking PISDR350 QUERY_INSERT_INSURANCE_SIMULATION *****");
			validateInsertion(this.pisdR350.executeInsertSingleRow(RBVDProperties.QUERY_INSERT_INSURANCE_SIMULATION.getValue(),argumentsForSaveSimulation), RBVDErrors.INSERTION_ERROR_IN_SIMULATION_TABLE);

			SimulationProductDAO simulationProductDAO = this.mapperHelper.createSimulationProductDAO(insuranceSimulationId, productInformationDAO.getInsuranceProductId(), creationUser, userAudit, response);
			Map<String, Object> argumentsForSaveSimulationProduct = this.mapperHelper.createArgumentsForSaveSimulationProduct(simulationProductDAO);
			LOGGER.info("***** PISDR302Impl - Invoking PISDR350 QUERY_INSERT_INSRNC_SIMLT_PRD *****");
			validateInsertion(this.pisdR350.executeInsertSingleRow(RBVDProperties.QUERY_INSERT_INSRNC_SIMLT_PRD.getValue(), argumentsForSaveSimulationProduct), RBVDErrors.INSERTION_ERROR_IN_SIMULATION_PRD_TABLE);

			response.getProduct().setId(inputProductId);
			response.getHolder().getIdentityDocument().getDocumentType().setId(documentTypeIdAsText);

			CustomerListASO responseListCustomers = this.rbvdR301.executeCallListCustomerResponse(response.getHolder().getId());

			this.serviceAddGifole(response, responseListCustomers);

			LOGGER.debug("***** RBVDR302Impl - executeGetSimulation deb ***** Response: {}", response);
			LOGGER.info("***** RBVDR302Impl - executeGetSimulation info ***** Response: {}", response);

			LOGGER.info("***** RBVDR302Impl - executeGetSimulation END *****");

			return response;

		} catch(BusinessException ex) {
			LOGGER.debug("***** RBVDR302Impl - executeGetGenerate | Business exception message: {} *****", ex.getMessage());
			this.addAdviceWithDescription(ex.getAdviceCode(), ex.getMessage());
			return null;
		}

	}

	private  BigDecimal validateQueryGetInsuranceAmount(Map<String, Object>  responseQueryGetCumulus){

		List<Map<String, Object>> rows = (List<Map<String, Object>>) responseQueryGetCumulus.get(PISDProperties.KEY_OF_INSRC_LIST_RESPONSES.getValue());
		BigDecimal sum = BigDecimal.ZERO;
		if(!isEmpty(rows) && rows.size()!=0) {
			List<BigDecimal> listCumulus = rows.stream().map(this::createListCumulus).collect(toList());
			for (BigDecimal amt : listCumulus) {
				sum = sum.add(amt);
			}
		}
		return sum;
	}

	private BigDecimal createListCumulus(Map < String, Object > mapElement){
		return (BigDecimal) mapElement.get(PISDProperties.FIELD_INSURED_AMOUNT.getValue());
	}

	private void serviceAddGifole(LifeSimulationDTO response, CustomerListASO responseListCustomers){

		LOGGER.info("Param 1 - LifeSimulationDTO : {}", response);
		LOGGER.info("Param 2 - CustomerListASO : {}", responseListCustomers);

		String flag = this.applicationConfigurationService.getProperty(ENABLE_GIFOLE_LIFE_ASO);

		LOGGER.info("FLAG : {}", flag);

		if(flag.equals("true")){

			LOGGER.info("***** RBVDR302Impl - executeGetSimulation | createGifoleInsuranceRequest invokation *****");

			LOGGER.info("FLAG after -> {}", flag);

			GifoleInsuranceRequestASO gifoleInsuranceRequest = this.mapperHelper.createGifoleASO(response, responseListCustomers);
			LOGGER.info("**** RBVDR302Impl - GifoleInsuranceRequestASO gifoleInsuranceRequest: {}", gifoleInsuranceRequest);

			Integer httpStatusGifole = rbvdR301.executeGifolelifeService(gifoleInsuranceRequest);

			LOGGER.info("***** RBVDR302Impl - executeGetSimulation ***** Gifole Response Status: {}", httpStatusGifole);
		}
	}

	private void validation(InsuranceLifeSimulationBO responseRimac){
		if(Objects.isNull(responseRimac)){
			throw RBVDValidation.build(RBVDErrors.ERROR_FROM_RIMAC);
		}
	}

	private ProductInformationDAO validateQueryGetProductInformation(Map<String, Object> responseQueryGetProductInformation) {
		if(isEmpty(responseQueryGetProductInformation)) {
			throw RBVDValidation.build(RBVDErrors.WRONG_PRODUCT_CODE);
		}
		ProductInformationDAO productInformationDAO = new ProductInformationDAO();
		productInformationDAO.setInsuranceProductId((BigDecimal) responseQueryGetProductInformation.get(RBVDProperties.FIELD_OR_FILTER_INSURANCE_PRODUCT_ID.getValue()));
		productInformationDAO.setInsuranceProductDescription((String) responseQueryGetProductInformation.get(RBVDProperties.FIELD_INSURANCE_PRODUCT_DESC.getValue()));
		productInformationDAO.setInsuranceBusinessName((String) responseQueryGetProductInformation.get(RBVDProperties.FIELD_INSURANCE_BUSINESS_NAME.getValue()));
		return productInformationDAO;
	}

	private List<InsuranceProductModalityDAO> validateQueryInsuranceProductModality(Map<String, Object> responseQueryInsuranceProductModality) {
		List<Map<String, Object>> rows = (List<Map<String, Object>>) responseQueryInsuranceProductModality.get(RBVDProperties.KEY_OF_INSRC_LIST_RESPONSES.getValue());
		if (isEmpty(rows)) {
			throw RBVDValidation.build(RBVDErrors.WRONG_PLAN_CODES);
		}
		return rows.stream().map(this::createInsuranceProductModalityDAO).collect(toList());
	}

	private InsuranceProductModalityDAO createInsuranceProductModalityDAO(Map<String, Object> mapElement) {
		return new InsuranceProductModalityDAO((String) mapElement.get(RBVDProperties.FIELD_INSURANCE_COMPANY_MODALITY_ID.getValue()),
				(String) mapElement.get(RBVDProperties.FIELD_INSURANCE_MODALITY_NAME.getValue()),
				(String) mapElement.get(RBVDProperties.FIELD_OR_FILTER_INSURANCE_MODALITY_TYPE.getValue()),
				(String) mapElement.get(RBVDProperties.FIELD_SUGGESTED_MODALITY_IND_TYPE.getValue()),
				(BigDecimal) mapElement.get(RBVDProperties.FIELD_PUBLICATION_ORDER_NUMBER.getValue()));
	}

	private void validateInsertion(int insertedRows, RBVDErrors error) {
		LOGGER.info("***** VALOR inSERrow {} ", insertedRows);
		if(insertedRows != 1) {
			throw RBVDValidation.build(error);
		}
	}

	private Date generateDate(String fechaFinVigencia) {
		DateTime dateTime = new DateTime(fechaFinVigencia);
		dateTime.withZone(DateTimeZone.forID("America/Lima"));
		return dateTime.toDate();
	}

	private TierASO validateTier (LifeSimulationDTO input){
		LOGGER.info("***** RBVDR302Impl - validateTier START *****");
		TierASO responseTierASO = null;
		if (Objects.isNull(input.getTier())) {
			LOGGER.info("Invoking Service ASO Tier");
			CryptoASO crypto = rbvdR301.executeCryptoService(new CryptoASO(input.getHolder().getId()));
			responseTierASO = rbvdR301.executeGetTierService(crypto.getData().getDocument());
		}
		LOGGER.info("***** RBVDR302Impl - validateTier ***** Response: {}", responseTierASO);
		LOGGER.info("***** RBVDR302Impl - validateTier END *****");
		return responseTierASO;
	}


}
