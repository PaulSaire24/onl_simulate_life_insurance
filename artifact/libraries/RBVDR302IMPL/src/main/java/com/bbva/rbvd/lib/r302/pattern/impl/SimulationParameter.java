package com.bbva.rbvd.lib.r302.pattern.impl;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.rbvd.dto.lifeinsrc.dao.ProductInformationDAO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;
import com.bbva.rbvd.lib.r302.util.ConfigConsola;
import com.bbva.rbvd.lib.r302.pattern.PreSimulation;

public class SimulationParameter implements PreSimulation {

	private LifeSimulationDTO input;
	private ApplicationConfigurationService applicationConfigurationService;
	private String payloadConfig;

	public SimulationParameter(LifeSimulationDTO input, ApplicationConfigurationService applicationConfigurationService) {
		this.input = input;
		this.applicationConfigurationService = applicationConfigurationService;
	}

	@Override
	public void getConfig() {
		this.getProperties();
		ProductInformationDAO productInformationDAO = this.getProduct("841");
		this.getCumulos();
		this.getCustomer();
		this.getTier();
		
	}
	
	
	//@Override
	public void getProperties() {
		ConfigConsola configConsola = new ConfigConsola(this.applicationConfigurationService);
		configConsola.getConfigConsola(input);
		System.out.println(" get Properties ....");
	}

	//@Override
	public ProductInformationDAO getProduct(String idProduct) {
		// TODO Auto-generated method stub
		System.out.println(" get Products >>> ....");
		return null;
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
		public void getTier() {
			// TODO Auto-generated method stub
			System.out.println(" get Tier :D  ....");

		}

		

}
