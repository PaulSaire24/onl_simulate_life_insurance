package com.bbva.rbvd.lib.r302.pattern.impl;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.pisd.dto.insurance.aso.crypto.CryptoASO;
import com.bbva.pisd.dto.insurance.aso.tier.TierASO;
import com.bbva.pisd.lib.r350.PISDR350;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class SimulationParameter implements PreSimulation {

	private static final Logger LOGGER = LoggerFactory.getLogger(SimulationParameter.class);

	private PISDR350 pisdR350;
	private RBVDR301 rbvdR301;
	private ApplicationConfigurationService applicationConfigurationService;

	private com.bbva.rbvd.lib.r302.util.ValidationUtil validationUtil;

	public SimulationParameter(PISDR350 pisdR350,RBVDR301 rbvdR301, ApplicationConfigurationService applicationConfigurationService) {
		//input = input;
		this.applicationConfigurationService = applicationConfigurationService;
		this.pisdR350 = pisdR350;
		this.rbvdR301 = rbvdR301;
		this.validationUtil = new ValidationUtil();
	}


	@Override
	public PayloadConfig getConfig(LifeSimulationDTO input) {
		LOGGER.info("***** SimulationParameter getConfig START *****");
		
		PayloadConfig payloadConfig = new PayloadConfig();

		PayloadProperties properties = this.getProperties(input);
		ProductInformationDAO productInformation = this.getProduct(input.getProduct().getId());

		CustomerListASO customerResponse = this.getCustomer(input.getHolder().getId());

		List<InsuranceProductModalityDAO> insuranceProductModalityDAOList =
				this.getModalities(this.applicationConfigurationService.getProperty("plansLife"), productInformation.getInsuranceProductId(), input.getSaleChannelId());

		BigDecimal cumulo = this.getCumulos(productInformation.getInsuranceProductId(), input.getHolder().getId());

		this.getTierToUpdateRequest(input);

		input.getHolder().getIdentityDocument().getDocumentType().setId(properties.getDocumentTypeId());

		payloadConfig.setProductInformation(productInformation);
		payloadConfig.setCustomerListASO(customerResponse);
		payloadConfig.setListInsuranceProductModalityDAO(insuranceProductModalityDAOList);
		payloadConfig.setSumCumulus(cumulo);
		payloadConfig.setInput(input);
		payloadConfig.setProperties(properties);

		return payloadConfig;
	}
	
	
	//@Override
	public PayloadProperties getProperties(LifeSimulationDTO input) {

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

		return properties;
	}

	//@Override
	public ProductInformationDAO getProduct(String productId) {

		IProductDAO productDAO = new ProductDAOImpl(this.pisdR350);
		ProductInformationDAO product= productDAO.getProductInformationById(productId);

		return product;
	}

	//@Override
	public BigDecimal getCumulos(BigDecimal insuranceProductId, String customerId) {
		IContractDAO contractDAO = new ContractDAOImpl(this.pisdR350);
		BigDecimal cumulos = contractDAO.getInsuranceAmountDAO(
				insuranceProductId,
				customerId);

		return cumulos;
	}

	//@Override
	public CustomerListASO getCustomer(String customerId) {

		ConsumerInternalService consumer = new ConsumerInternalService(rbvdR301);

		CustomerListASO customer = consumer.callListCustomerResponse(customerId);

		return customer;
	}

	public List<InsuranceProductModalityDAO> getModalities(String plansPT, BigDecimal insuranceProductId, String saleChannel){

		IModalitiesDAO iModalitiesDAO = new ModalitiesDAOImpl(pisdR350);

		List<InsuranceProductModalityDAO> listInsuranceProductModalityDAO =
				iModalitiesDAO.getModalitiesInfo(plansPT, insuranceProductId, saleChannel);

		return listInsuranceProductModalityDAO;
	}

	public void getTierToUpdateRequest(LifeSimulationDTO input) {
		TierASO responseTierASO = validateTier(input);

		if (Objects.nonNull(responseTierASO)) {
			//Actualizo el input con la data del Tier
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
	}

	private TierASO validateTier (LifeSimulationDTO input){
		//LOGGER.info("***** RBVDR302Impl - validateTier START *****");
		ConsumerInternalService consumerInternalService = new ConsumerInternalService(this.rbvdR301);
		TierASO responseTierASO = null;
		if (Objects.isNull(input.getTier())) {
			//LOGGER.info("Invoking Service ASO Tier");
			CryptoASO crypto = consumerInternalService.callCryptoService(input.getHolder().getId());
			responseTierASO = consumerInternalService.callGetTierService(crypto.getData().getDocument());
		}
		//LOGGER.info("***** RBVDR302Impl - validateTier ***** Response: {}", responseTierASO);
		//LOGGER.info("***** RBVDR302Impl - validateTier END *****");
		return responseTierASO;
	}

}
