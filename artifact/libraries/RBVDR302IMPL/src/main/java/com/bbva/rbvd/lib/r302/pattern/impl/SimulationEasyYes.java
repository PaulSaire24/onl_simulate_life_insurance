package com.bbva.rbvd.lib.r302.pattern.impl;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;
import com.bbva.rbvd.lib.r301.RBVDR301;
import com.bbva.rbvd.lib.r302.business.IGifoleBusiness;
import com.bbva.rbvd.lib.r302.business.impl.GifoleBusinessImpl;
import com.bbva.rbvd.lib.r302.transfer.PayloadConfig;
import com.bbva.rbvd.lib.r302.transfer.PayloadStore;
import com.bbva.rbvd.lib.r302.business.IInsrEasyYesBusiness;
import com.bbva.rbvd.lib.r302.business.impl.InsrEasyYesBusinessImpl;
import com.bbva.rbvd.lib.r302.pattern.PostSimulation;
import com.bbva.rbvd.lib.r302.pattern.PreSimulation;


public class SimulationEasyYes extends SimulationDecorator{

	public SimulationEasyYes(PreSimulation preSimulation, PostSimulation postSimulation) {
		super(preSimulation, postSimulation);
	}


	@Override
	public LifeSimulationDTO start(RBVDR301 rbvdR301, ApplicationConfigurationService applicationConfigurationService) {

		//Configuraciones previas
		PayloadConfig payloadConfig = this.getPreSimulation().getConfig();

		IInsrEasyYesBusiness seguroEasyYes = new InsrEasyYesBusinessImpl(rbvdR301,applicationConfigurationService);

		PayloadStore payloadStore = seguroEasyYes.doEasyYes(payloadConfig);

		//guardar en bd
		this.getPostSimulation().end(payloadStore);

		//llamada a gifole
		IGifoleBusiness iGifoleBusiness = new GifoleBusinessImpl(rbvdR301,applicationConfigurationService);
		iGifoleBusiness.serviceAddGifole(payloadStore.getResponse(),payloadConfig.getCustomerListASO());


		//LOGGER.info("***** RBVDR302Impl - executeGetSimulation END *****");

		return payloadStore.getResponse();
	}


}
