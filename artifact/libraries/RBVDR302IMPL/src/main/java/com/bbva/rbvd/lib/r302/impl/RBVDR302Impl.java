package com.bbva.rbvd.lib.r302.impl;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.pisd.dto.insurance.aso.CustomerListASO;

import com.bbva.pisd.dto.insurance.aso.gifole.GifoleInsuranceRequestASO;
import com.bbva.pisd.dto.insurance.aso.tier.TierASO;

import com.bbva.rbvd.dto.lifeinsrc.commons.InsurancePlanDTO;
import com.bbva.rbvd.dto.lifeinsrc.dao.InsuranceProductModalityDAO;
import com.bbva.rbvd.dto.lifeinsrc.dao.ProductInformationDAO;
import com.bbva.rbvd.dto.lifeinsrc.dao.SimulationDAO;
import com.bbva.rbvd.dto.lifeinsrc.dao.SimulationProductDAO;
import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.InsuranceLifeSimulationBO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDErrors;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDProperties;

import com.bbva.rbvd.lib.r302.service.dao.impl.ContractDAOImpl;
import com.bbva.rbvd.lib.r302.service.dao.impl.ProductDAO;
import com.bbva.rbvd.lib.r302.transform.bean.SimulationBean;
import com.bbva.rbvd.lib.r302.transform.bean.SimulationProductBean;
import com.bbva.rbvd.lib.r302.transform.map.ProductMap;
import com.bbva.rbvd.lib.r302.transform.map.SimulationMap;
import com.bbva.rbvd.lib.r302.transform.map.SimulationProductMap;
import com.bbva.rbvd.lib.r302.business.util.ConvertUtil;
import com.bbva.rbvd.lib.r302.business.util.ValidationUtil;
import com.bbva.rbvd.lib.r302.pattern.Simulation;
import com.bbva.rbvd.lib.r302.pattern.impl.SimulationEasyYes;
import com.bbva.rbvd.lib.r302.pattern.impl.SimulationParameter;
import com.bbva.rbvd.lib.r302.pattern.impl.SimulationStore;
import com.bbva.rbvd.lib.r302.pattern.impl.SimulationVidaDinamico;
import com.bbva.rbvd.lib.r302.service.dao.IProductDAO;
import com.bbva.rbvd.lib.r302.service.dao.impl.ProductDAOImpl;
import com.bbva.rbvd.lib.r302.impl.util.MockResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Date;

import static org.springframework.util.CollectionUtils.isEmpty;


public class RBVDR302Impl extends RBVDR302Abstract {

	private static final Logger LOGGER = LoggerFactory.getLogger(RBVDR302Impl.class);

	private static final String ENABLE_GIFOLE_LIFE_ASO = "ENABLE_GIFOLE_LIFE_ASO";


	/**
	 * The execute method...
	 */
	//Ejecuta cómo obtener la simulación
	@Override
	public LifeSimulationDTO executeGetSimulation(LifeSimulationDTO input) {

		LOGGER.info("***** RBVDR302Impl - executeGetSimulation START *****");
		LOGGER.info("***** RBVDR302Impl - executeGetSimulation ***** {}", input);

		Simulation simulation = null;
		if(input.getProduct().getId().equals("840")){
			simulation = new SimulationEasyYes(new SimulationParameter(input,this.applicationConfigurationService),new SimulationStore());
		}else if(input.getProduct().getId().equals("841")){
			simulation = new SimulationVidaDinamico(new SimulationParameter(input,this.applicationConfigurationService), new SimulationStore());
		}

		//inicio
		simulation.start();

		//////////////////////////////////////////////////////------------


		LifeSimulationDTO response = new LifeSimulationDTO();
		ValidationUtil validationUtil = new ValidationUtil(this.rbvdR301);

		try{

			String inputProductId = input.getProduct().getId();
			String documentTypeIdAsText = input.getHolder().getIdentityDocument().getDocumentType().getId();
			String documentTypeId = this.applicationConfigurationService.getProperty(input.getHolder().getIdentityDocument().getDocumentType().getId());

			input.getHolder().getIdentityDocument().getDocumentType().setId(documentTypeId);
			LOGGER.info("***** PISDR302Impl - Invoking PISDR350 QUERY_GET_PRODUCT_INFORMATION *****");
			//llama a la R350 para ejecutar la query a la base de datos
			IProductDAO productDAO = new ProductDAOImpl();
			productDAO.getProductInformationById(input.getProduct().getId());

			ProductDAO productDAO1 = new ProductDAO(this.pisdR350);
			Map<String, Object> responseQueryGetProductInformation = productDAO1.getProductInformationDAO(input.getProduct().getId());

			ProductInformationDAO productInformationDAO = validationUtil.validateQueryGetProductInformation(responseQueryGetProductInformation);
			//llama a la R350 para obtener el cúmulo de la base de datos
			ContractDAOImpl contractDAO = new ContractDAOImpl(this.pisdR350);
			Map<String, Object> responseQueryGetCumulus = contractDAO.getInsuranceAmountDAO((BigDecimal) responseQueryGetProductInformation
					.get(RBVDProperties.FIELD_OR_FILTER_INSURANCE_PRODUCT_ID.getValue()),input.getHolder().getId());

			BigDecimal sumCumulus = validationUtil.validateQueryGetInsuranceAmount(responseQueryGetCumulus);
			//valida la cantidad asegurada

			CustomerListASO responseListCustomers = this.rbvdR301.executeCallListCustomerResponse(input.getHolder().getId());

			InsuranceLifeSimulationBO rimacRequest = mapperHelper.mapInRequestRimacLife(input, sumCumulus);
			InsuranceLifeSimulationBO responseRimac = null;

			rimacRequest.getPayload().setProducto(productInformationDAO.getInsuranceBusinessName());

			if(input.getProduct().getId().equals("841")){
				this.mapperHelper.addFieldsDatoParticulares(rimacRequest, input, responseListCustomers);


				LOGGER.info("***** PISDR302Impl - Rimac Request: {} *****", rimacRequest);


				if(this.applicationConfigurationService.getProperty("IS_MOCK_QUOTATION_DYNAMIC").equals("S")){
					//usar mock de rimac
					responseRimac = new MockResponse().getMockResponseRimacService();
				}else{
					if(Objects.nonNull(input.getExternalSimulationId())){
						responseRimac = this.rbvdR301.executeSimulationModificationRimacService(rimacRequest, input.getExternalSimulationId(), input.getTraceId());
					} else {
						responseRimac = rbvdR301.executeSimulationRimacService(rimacRequest, input.getTraceId());
					}

				}

			}else{
				responseRimac = rbvdR301.executeSimulationRimacService(rimacRequest, input.getTraceId());
			}

			LOGGER.info("***** PISDR302Impl - Response Rimac : {} *****", responseRimac);

			validationUtil.validation(responseRimac);

			response = input;

			TierASO responseTierASO = validationUtil.validateTier(input);
			this.mapperHelper.mappingTierASO(input, responseTierASO);

			String segmentoLifePlan1 = applicationConfigurationService.getProperty("segmentoLifePlan1");
			String segmentoLifePlan2 = applicationConfigurationService.getProperty("segmentoLifePlan2");
			String segmentoLifePlan3 = applicationConfigurationService.getProperty("segmentoLifePlan3");

			Boolean seglifePlan1 = this.mapperHelper.selectValuePlansDescription(segmentoLifePlan1, input);
			Boolean seglifePlan2 =this.mapperHelper.selectValuePlansDescription(segmentoLifePlan2, input);
			Boolean seglifePlan3 =this.mapperHelper.selectValuePlansDescription(segmentoLifePlan3, input);

			mapperHelper.mapOutRequestRimacLife(responseRimac, response);

			//get Modalities()
			String planesLife = applicationConfigurationService.getProperty("plansLife");
			Map<String, Object> filtersModalitiesInfo = ProductMap.createModalitiesInformationFilters(planesLife, productInformationDAO.getInsuranceProductId(), input.getSaleChannelId());
			LOGGER.info("***** PISDR302Impl - Invoking PISDR350 QUERY_GET_PRODUCT_MODALITIES_INFORMATION *****");
			Map<String, Object> responseQueryModalitiesInformation =
					this.pisdR350.executeGetListASingleRow(RBVDProperties.QUERY_GET_PRODUCT_MODALITIES_INFORMATION.getValue(), filtersModalitiesInfo);
			List<InsuranceProductModalityDAO> productModalitiesDAO = validationUtil.validateQueryInsuranceProductModality(responseQueryModalitiesInformation);

			List<InsurancePlanDTO> plansWithNameAndRecommendedValueAndInstallmentPlan = this.mapperHelper.getPlansNamesAndRecommendedValuesAndInstallmentsPlans
					(productModalitiesDAO, responseRimac, seglifePlan1, seglifePlan2, seglifePlan3);

			response.getProduct().setPlans(plansWithNameAndRecommendedValueAndInstallmentPlan);

			Map<String, Object> arguments = new HashMap<>();
			LOGGER.info("***** PISDR302Impl - Invoking PISDR350 QUERY_SELECT_INSURANCE_SIMULATION_ID *****");
			Map<String, Object> responseGetInsuranceSimulationId = this.pisdR350.executeGetASingleRow(RBVDProperties.QUERY_SELECT_INSURANCE_SIMULATION_ID.getValue(),arguments);
			BigDecimal insuranceSimulationId = (BigDecimal) responseGetInsuranceSimulationId.get(RBVDProperties.FIELD_Q_PISD_SIMULATION_ID0_NEXTVAL.getValue());

			String creationUser = input.getCreationUser();
			String userAudit = input.getUserAudit();

			Date maturityDate = ConvertUtil.generateDate(responseRimac.getPayload().getCotizaciones().get(0).getFechaFinVigencia());

			SimulationDAO simulationDAO = SimulationBean.createSimulationDAO(insuranceSimulationId, maturityDate, response);

			Map<String, Object> argumentsForSaveSimulation = SimulationMap.createArgumentsForSaveSimulation(simulationDAO, creationUser, userAudit, documentTypeId);

			LOGGER.info("***** PISDR302Impl - Invoking PISDR350 QUERY_INSERT_INSURANCE_SIMULATION *****");
			validationUtil.validateInsertion(this.pisdR350.executeInsertSingleRow(RBVDProperties.QUERY_INSERT_INSURANCE_SIMULATION.getValue(),argumentsForSaveSimulation), RBVDErrors.INSERTION_ERROR_IN_SIMULATION_TABLE);

			SimulationProductDAO simulationProductDAO = SimulationProductBean.createSimulationProductDAO(insuranceSimulationId, productInformationDAO.getInsuranceProductId(), creationUser, userAudit, response);
			Map<String, Object> argumentsForSaveSimulationProduct = SimulationProductMap.createArgumentsForSaveSimulationProduct(simulationProductDAO);
			LOGGER.info("***** PISDR302Impl - Invoking PISDR350 QUERY_INSERT_INSRNC_SIMLT_PRD *****");
			validationUtil.validateInsertion(this.pisdR350.executeInsertSingleRow(RBVDProperties.QUERY_INSERT_INSRNC_SIMLT_PRD.getValue(), argumentsForSaveSimulationProduct), RBVDErrors.INSERTION_ERROR_IN_SIMULATION_PRD_TABLE);

			response.getProduct().setId(inputProductId);
			response.getHolder().getIdentityDocument().getDocumentType().setId(documentTypeIdAsText);

			if(!input.getProduct().getId().equals("841")){
				this.serviceAddGifole(response, responseListCustomers);
			}

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


	//Agrega el servicio Gifole (información de clientes)
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



}
