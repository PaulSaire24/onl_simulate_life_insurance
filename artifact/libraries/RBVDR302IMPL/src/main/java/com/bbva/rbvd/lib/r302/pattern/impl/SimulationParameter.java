package com.bbva.rbvd.lib.r302.pattern.impl;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.pisd.dto.insurance.aso.tier.TierASO;
import com.bbva.pisd.lib.r350.PISDR350;
import com.bbva.rbvd.dto.lifeinsrc.commons.TierDTO;
import com.bbva.rbvd.dto.lifeinsrc.dao.ProductInformationDAO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;
import com.bbva.rbvd.lib.r301.RBVDR301;
import com.bbva.rbvd.lib.r302.Transfer.PayloadConfig;
import com.bbva.rbvd.lib.r302.business.util.ValidationUtil;
import com.bbva.rbvd.lib.r302.service.dao.IProductDAO;
import com.bbva.rbvd.lib.r302.service.dao.impl.ProductDAOImpl;
import com.bbva.rbvd.lib.r302.util.ConfigConsola;
import com.bbva.rbvd.lib.r302.pattern.PreSimulation;

import java.util.Objects;


public class SimulationParameter implements PreSimulation {

	private PISDR350 pisdR350;

	private RBVDR301 rbvdR301;
	private LifeSimulationDTO input;
	private ApplicationConfigurationService applicationConfigurationService;
	private PayloadConfig payloadConfig;

	private com.bbva.rbvd.lib.r302.business.util.ValidationUtil validationUtil;

	public SimulationParameter(PISDR350 pisdR350,RBVDR301 rbvdR301, LifeSimulationDTO input, ApplicationConfigurationService applicationConfigurationService) {
		this.input = input;
		this.applicationConfigurationService = applicationConfigurationService;
		this.pisdR350 = pisdR350;
		this.rbvdR301 = rbvdR301;
		this.validationUtil = new ValidationUtil(rbvdR301);
	}



	@Override
	public PayloadConfig getConfig() {
		this.getProperties();
		ProductInformationDAO productInformation = this.getProduct(input.getProduct().getId());
		this.getCumulos();
		this.getCustomer();
		this.getTier();



		payloadConfig.setProductInformation(productInformation);

		return payloadConfig;
	}
	
	
	//@Override
	public void getProperties() {
		ConfigConsola configConsola = new ConfigConsola(this.applicationConfigurationService);
		configConsola.getConfigConsola(input);
		System.out.println(" get Properties ....");
	}

	//@Override
	public ProductInformationDAO getProduct(String productId) {


		IProductDAO  productDAO = new ProductDAOImpl(pisdR350);
		ProductInformationDAO product= productDAO.getProductInformationById(input.getProduct().getId());


		return product;
	}

	//@Override
	public void getCumulos() {
		// TODO Auto-generated method stub
		System.out.println(" get Cumulos () ....");

	}

	//@Override
	public void getCustomer() {
		// TODO Auto-generated method stub
		System.out.println(" get Customer :D  ....");

	}

	//@Override
		public void getTier(LifeSimulationDTO input) {
			// TODO Auto-generated method stub
			TierASO responseTierASO = validationUtil.validateTier(input);
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
			//System.out.println(" get Tier :D  ....");

		}


		

}
