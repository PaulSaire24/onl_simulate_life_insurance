package com.bbva.rbvd.lib.r302.impl;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.rbvd.dto.lifeinsrc.aso.crypto.CryptoASO;
import com.bbva.rbvd.dto.lifeinsrc.aso.tier.TierASO;
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

/**
 * The RBVDR302Impl class...
 */
public class RBVDR302Impl extends RBVDR302Abstract {

	private static final Logger LOGGER = LoggerFactory.getLogger(RBVDR302Impl.class);

	/**
	 * The execute method...
	 */
	@Override
	public LifeSimulationDTO executeGetSimulation(LifeSimulationDTO input) {
		// TODO - Implementation of business logic
		LOGGER.info("***** RBVDR302Impl - executeGetSimulation START *****");
		LOGGER.info("***** RBVDR302Impl - executeGetSimulation ***** {}", input);

		LifeSimulationDTO response = new LifeSimulationDTO();

		try{

			String inputProductId = input.getProduct().getId();
			String documentTypeIdAsText = input.getHolder().getIdentityDocument().getDocumentType().getId();
			String documentTypeId = this.applicationConfigurationService.getProperty(input.getHolder().getIdentityDocument().getDocumentType().getId());

			input.getHolder().getIdentityDocument().getDocumentType().setId(documentTypeId);

			Map<String, Object> responseQueryGetProductInformation =
					this.pisdR350.executeGetASingleRow(RBVDProperties.QUERY_GET_PRODUCT_INFORMATION.getValue(), this.mapperHelper.mapProductId(input.getProduct().getId()));

			ProductInformationDAO productInformationDAO = validateQueryGetProductInformation(responseQueryGetProductInformation);

			InsuranceLifeSimulationBO rimacRequest = mapperHelper.mapInRequestRimacLife(input);
			rimacRequest.getPayload().setProducto(productInformationDAO.getInsuranceBusinessName());
			InsuranceLifeSimulationBO responseRimac = rbvdR301.executeSimulationRimacService(rimacRequest, input.getTraceId());
			validation(responseRimac);

			response = input;
			mapperHelper.mapOutRequestRimacLife(responseRimac, response);

			String planesLife = applicationConfigurationService.getProperty("plansLife");

			Map<String, Object> filtersModalitiesInfo = this.mapperHelper.
					createModalitiesInformationFilters(planesLife, productInformationDAO.getInsuranceProductId(), input.getSaleChannelId());
			LOGGER.info("***** PISDR302Impl - insuranceProductModalityFiltersCreation | Invoking PISDR350 executeGetConsiderations *****");
			Map<String, Object> responseQueryModalitiesInformation =
					this.pisdR350.executeGetListASingleRow(RBVDProperties.QUERY_GET_PRODUCT_MODALITIES_INFORMATION.getValue(), filtersModalitiesInfo);

			List<InsuranceProductModalityDAO> productModalitiesDAO = validateQueryInsuranceProductModality(responseQueryModalitiesInformation);
			List<InsurancePlanDTO> plansWithNameAndRecommendedValueAndInstallmentPlan = this.mapperHelper.getPlansNamesAndRecommendedValuesAndInstallmentsPlans(productModalitiesDAO, responseRimac);
			response.getProduct().setPlans(plansWithNameAndRecommendedValueAndInstallmentPlan);

			this.mapperHelper.putConsiderations(response.getProduct().getPlans(), responseRimac.getPayload().getCotizaciones());

			Map<String, Object> arguments = new HashMap<>();
			Map<String, Object> responseGetInsuranceSimulationId = this.pisdR350.executeGetASingleRow(RBVDProperties.QUERY_SELECT_INSURANCE_SIMULATION_ID.getValue(),arguments);
			BigDecimal insuranceSimulationId = (BigDecimal) responseGetInsuranceSimulationId.get(RBVDProperties.FIELD_Q_PISD_SIMULATION_ID0_NEXTVAL.getValue());

			String creationUser = input.getCreationUser();
			String userAudit = input.getUserAudit();

			Date maturityDate = this.generateDate(responseRimac.getPayload().getCotizaciones().get(0).getFechaFinVigencia());

			SimulationDAO simulationDAO = mapperHelper.createSimulationDAO(insuranceSimulationId, maturityDate, response);

			Map<String, Object> argumentsForSaveSimulation = this.mapperHelper.createArgumentsForSaveSimulation(simulationDAO, creationUser, userAudit, documentTypeId);


			boolean saveSimulationExecuted = validateInsertion(this.pisdR350.executeInsertSingleRow(RBVDProperties.QUERY_INSERT_INSURANCE_SIMULATION.getValue(),argumentsForSaveSimulation), RBVDErrors.INSERTION_ERROR_IN_SIMULATION_TABLE);

			if (saveSimulationExecuted) {
				LOGGER.info("***** PISDR302Impl - executeResponseSimulationValidation (saveSimulationExecuted) SUCCESSFULLY *****");

				SimulationProductDAO simulationProductDAO = this.mapperHelper.createSimulationProductDAO(insuranceSimulationId, productInformationDAO.getInsuranceProductId(), creationUser, userAudit, response);
				Map<String, Object> argumentsForSaveSimulationProduct = this.mapperHelper.createArgumentsForSaveSimulationProduct(simulationProductDAO);
				this.pisdR350.executeInsertSingleRow(RBVDProperties.QUERY_INSERT_INSRNC_SIMLT_PRD.getValue(),argumentsForSaveSimulationProduct);

			}

			response.getProduct().setId(inputProductId);
			response.getHolder().getIdentityDocument().getDocumentType().setId(documentTypeIdAsText);

			LOGGER.info("***** RBVDR302Impl - executeGetSimulation ***** Response: {}", response);
			LOGGER.info("***** RBVDR302Impl - executeGetSimulation END *****");

			return response;

		} catch(BusinessException ex) {
			LOGGER.debug("***** RBVDR302Impl - executeGetGenerate | Business exception message: {} *****", ex.getMessage());
			this.addAdviceWithDescription(ex.getAdviceCode(), ex.getMessage());
			return null;
		}

	}


	private void validation(InsuranceLifeSimulationBO simulationBos){
		if(Objects.isNull(simulationBos)){
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

	private boolean validateInsertion(int insertedRows, RBVDErrors error) {
		LOGGER.info("***** VALOR inSERrow {} ", insertedRows);
		if(insertedRows != 1) {
			throw RBVDValidation.build(error);
		}
		return true;
	}

	private Date generateDate(String fechaFinVigencia) {
		DateTime dateTime = new DateTime(fechaFinVigencia);
		dateTime.withZone(DateTimeZone.forID("America/Lima"));
		return dateTime.toDate();
	}

}
