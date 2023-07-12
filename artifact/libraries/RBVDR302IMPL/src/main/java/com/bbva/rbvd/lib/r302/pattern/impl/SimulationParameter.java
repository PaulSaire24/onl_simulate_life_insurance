package com.bbva.rbvd.lib.r302.pattern.impl;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.pisd.lib.r350.PISDR350;
import com.bbva.rbvd.dto.lifeinsrc.dao.ProductInformationDAO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;
import com.bbva.rbvd.lib.r302.Transfer.PayloadConfig;
import com.bbva.rbvd.lib.r302.service.dao.IProductDAO;
import com.bbva.rbvd.lib.r302.service.dao.impl.ProductDAOImpl;
import com.bbva.rbvd.lib.r302.util.ConfigConsola;
import com.bbva.rbvd.lib.r302.pattern.PreSimulation;


public class SimulationParameter implements PreSimulation {

	private PISDR350 pisdR350;
	private LifeSimulationDTO input;
	private ApplicationConfigurationService applicationConfigurationService;
	private PayloadConfig payloadConfig;

	public SimulationParameter(PISDR350 pisdR350, LifeSimulationDTO input, ApplicationConfigurationService applicationConfigurationService) {
		this.input = input;
		this.applicationConfigurationService = applicationConfigurationService;
		this.pisdR350 = pisdR350;
	}

	@Override
	public PayloadConfig getConfig() {
		this.getProperties();
		ProductInformationDAO productInformation = this.getProduct(input.getProduct().getId());
		this.getCumulos();
		CustomerListASO customer = this.getCustomer(input.getHolder().getId());
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
	public CustomerListASO getCustomer(String customerId) {

		return null;
	}

	//@Override
		public void getTier() {
			// TODO Auto-generated method stub
			System.out.println(" get Tier :D  ....");

		}

		

}
