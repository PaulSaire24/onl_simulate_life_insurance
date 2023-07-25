package com.bbva.rbvd.lib.r302.pattern.impl;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.pisd.dto.insurance.aso.crypto.CryptoASO;
import com.bbva.pisd.dto.insurance.aso.tier.TierASO;
import com.bbva.pisd.lib.r350.PISDR350;
import com.bbva.rbvd.dto.lifeinsrc.commons.CommonFieldsDTO;
import com.bbva.rbvd.dto.lifeinsrc.commons.TierDTO;
import com.bbva.rbvd.dto.lifeinsrc.dao.InsuranceProductModalityDAO;
import com.bbva.rbvd.dto.lifeinsrc.dao.ProductInformationDAO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;
import com.bbva.rbvd.lib.r301.RBVDR301;
import com.bbva.rbvd.lib.r302.transfer.PayloadConfig;
import com.bbva.rbvd.lib.r302.transfer.PayloadProperties;
import com.bbva.rbvd.lib.r302.util.ValidationUtil;
import com.bbva.rbvd.lib.r302.service.api.ConsumerInternalService;
import com.bbva.rbvd.lib.r302.service.dao.IModalitiesDAO;
import com.bbva.rbvd.lib.r302.service.dao.IProductDAO;
import com.bbva.rbvd.lib.r302.service.dao.impl.ModalitiesDAOImpl;
import com.bbva.rbvd.lib.r302.service.dao.IContractDAO;
import com.bbva.rbvd.lib.r302.service.dao.impl.ContractDAOImpl;
import com.bbva.rbvd.lib.r302.service.dao.impl.ProductDAOImpl;
import com.bbva.rbvd.lib.r302.pattern.PreSimulation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class SimulationParameter implements PreSimulation {

	private static final Logger LOGGER = LoggerFactory.getLogger(SimulationParameter.class);

	private final PISDR350 pisdR350;
	private final RBVDR301 rbvdR301;
	private final ApplicationConfigurationService applicationConfigurationService;

	private final com.bbva.rbvd.lib.r302.util.ValidationUtil validationUtil;

	public SimulationParameter(PISDR350 pisdR350,RBVDR301 rbvdR301, ApplicationConfigurationService applicationConfigurationService) {
		this.applicationConfigurationService = applicationConfigurationService;
		this.pisdR350 = pisdR350;
		this.rbvdR301 = rbvdR301;
		this.validationUtil = new ValidationUtil();
	}


	@Override
	public PayloadConfig getConfig(LifeSimulationDTO input) {
		LOGGER.info("***** SimulationParameter getConfig START *****");
		LOGGER.info("***** SimulationParameter getConfig - input : {} *****",input);
		
		PayloadConfig payloadConfig = new PayloadConfig();

		PayloadProperties properties = this.getProperties(input);
		ProductInformationDAO productInformation = this.getProduct(input.getProduct().getId());

		CustomerListASO customerResponse = this.getCustomer(input.getHolder().getId());

		List<InsuranceProductModalityDAO> insuranceProductModalityDAOList =
				this.getModalities(this.getModalitiesSelected(input), productInformation.getInsuranceProductId(), input.getSaleChannelId());

		BigDecimal cumulo = this.getCumulos(productInformation.getInsuranceProductId(), input.getHolder().getId());

		this.getTierToUpdateRequest(input);

		input.getHolder().getIdentityDocument().getDocumentType().setId(properties.getDocumentTypeId());

		payloadConfig.setProductInformation(productInformation);
		payloadConfig.setCustomerListASO(customerResponse);
		payloadConfig.setListInsuranceProductModalityDAO(insuranceProductModalityDAOList);
		payloadConfig.setSumCumulus(cumulo);
		payloadConfig.setInput(input);
		payloadConfig.setProperties(properties);

		LOGGER.info("***** SimulationParameter getConfig - END  payloadConfig: {} *****",payloadConfig);

		return payloadConfig;
	}

	private String getModalitiesSelected(LifeSimulationDTO input){
		String plans;

		if(Objects.nonNull(input.getProduct()) && !CollectionUtils.isEmpty(input.getProduct().getPlans())){
			List<String> plansIn = input.getProduct().getPlans().stream().map(CommonFieldsDTO::getId).collect(Collectors.toList());
			plans = String.join(",",plansIn);
		}else{
			plans = this.applicationConfigurationService.getProperty("plansLife");
		}

		return plans;
	}
	

	public PayloadProperties getProperties(LifeSimulationDTO input) {

		LOGGER.info("***** SimulationParameter getProperties START *****");

		PayloadProperties properties = new PayloadProperties();
		properties.setDocumentTypeId(this.applicationConfigurationService.getProperty(input.getHolder().getIdentityDocument().getDocumentType().getId()));
		properties.setDocumentTypeIdAsText(input.getHolder().getIdentityDocument().getDocumentType().getId());

		String segmentoLifePlan1 = applicationConfigurationService.getProperty("segmentoLifePlan1");
		String segmentoLifePlan2 = applicationConfigurationService.getProperty("segmentoLifePlan2");
		String segmentoLifePlan3 = applicationConfigurationService.getProperty("segmentoLifePlan3");

		Boolean seglifePlan1 = validationUtil.selectValuePlansDescription(segmentoLifePlan1,input);
		Boolean seglifePlan2 = validationUtil.selectValuePlansDescription(segmentoLifePlan2,input);
		Boolean seglifePlan3 = validationUtil.selectValuePlansDescription(segmentoLifePlan3,input);

		List<Boolean> segmentLifePlans = new ArrayList<>();
		segmentLifePlans.add(seglifePlan1);
		segmentLifePlans.add(seglifePlan2);
		segmentLifePlans.add(seglifePlan3);

		properties.setSegmentLifePlans(segmentLifePlans);

		LOGGER.info("***** SimulationParameter getProperties END - properties: {} *****",properties);

		return properties;
	}


	public ProductInformationDAO getProduct(String productId) {

		LOGGER.info("***** SimulationParameter getProduct START - productId: {} *****",productId);

		IProductDAO productDAO = new ProductDAOImpl(this.pisdR350);
		ProductInformationDAO response = productDAO.getProductInformationById(productId);

		LOGGER.info("***** SimulationParameter getProduct END - response: {} *****",response);

		return response;
	}


	public BigDecimal getCumulos(BigDecimal insuranceProductId, String customerId) {

		LOGGER.info("***** SimulationParameter getCumulos START - insuranceProductId: {} *****",insuranceProductId);
		LOGGER.info("***** SimulationParameter getCumulos START - customerId: {} *****",customerId);

		IContractDAO contractDAO = new ContractDAOImpl(this.pisdR350);
		BigDecimal cumulus = contractDAO.getInsuranceAmountDAO(insuranceProductId, customerId);

		LOGGER.info("***** SimulationParameter getCumulos END - cumulus: {} *****",cumulus);

		return cumulus;
	}


	public CustomerListASO getCustomer(String customerId) {

		LOGGER.info("***** SimulationParameter getCustomer START - customerId: {} *****",customerId);

		ConsumerInternalService consumer = new ConsumerInternalService(rbvdR301);
		CustomerListASO customer = consumer.callListCustomerResponse(customerId);

		LOGGER.info("***** SimulationParameter getCustomer END - customer: {} *****",customer);

		return customer;
	}

	public List<InsuranceProductModalityDAO> getModalities(String plansPT, BigDecimal insuranceProductId, String saleChannel){

		LOGGER.info("***** SimulationParameter getModalities START argument - plansPT: {} *****",plansPT);
		LOGGER.info("***** SimulationParameter getModalities START argument - insuranceProductId: {} *****",insuranceProductId);
		LOGGER.info("***** SimulationParameter getModalities START argument - saleChannel: {} *****",saleChannel);

		IModalitiesDAO iModalitiesDAO = new ModalitiesDAOImpl(pisdR350);
		List<InsuranceProductModalityDAO> list = iModalitiesDAO.getModalitiesInfo(plansPT, insuranceProductId, saleChannel);

		LOGGER.info("***** SimulationParameter getModalities END - list: {} *****",list);

		return list;
	}

	public void getTierToUpdateRequest(LifeSimulationDTO input) {
		TierASO responseTierASO = validateTier(input);

		if (Objects.nonNull(responseTierASO)) {
			TierDTO tierDTO = new TierDTO();
			tierDTO.setId(responseTierASO.getData().get(0).getId());
			tierDTO.setName(responseTierASO.getData().get(0).getDescription());
			input.setTier(tierDTO);
			input.setBankingFactor(responseTierASO.getData().get(0).getChargeFactor());
			if(Objects.nonNull(responseTierASO.getData().get(0).getSegments())) {
				input.setId(responseTierASO.getData().get(0).getSegments().get(0).getId());
			}else {
				input.setId(null);
			}
		}

		LOGGER.info("***** SimulationParameter - getTierToUpdateRequest END | input {} *****",input);
	}

	private TierASO validateTier (LifeSimulationDTO input){
		LOGGER.info("***** SimulationParameter - validateTier START *****");
		ConsumerInternalService consumerInternalService = new ConsumerInternalService(this.rbvdR301);
		TierASO responseTierASO = null;
		if (Objects.isNull(input.getTier())) {
			LOGGER.info("Invoking Service ASO Tier");
			CryptoASO crypto = consumerInternalService.callCryptoService(input.getHolder().getId());
			responseTierASO = consumerInternalService.callGetTierService(crypto.getData().getDocument());
		}
		LOGGER.info("***** SimulationParameter - validateTier ***** Response: {}", responseTierASO);
		LOGGER.info("***** SimulationParameter - validateTier END *****");
		return responseTierASO;
	}

}
