package com.bbva.rbvd.lib.r302.pattern.product;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;
import com.bbva.rbvd.lib.r301.RBVDR301;
import com.bbva.rbvd.lib.r302.business.IGifoleBusiness;
import com.bbva.rbvd.lib.r302.business.IInsrEasyYesBusiness;
import com.bbva.rbvd.lib.r302.business.impl.GifoleBusinessImpl;
import com.bbva.rbvd.lib.r302.business.impl.InsrEasyYesBusinessImpl;
import com.bbva.rbvd.lib.r302.pattern.PostSimulation;
import com.bbva.rbvd.lib.r302.pattern.PreSimulation;
import com.bbva.rbvd.lib.r302.pattern.impl.SimulationDecorator;
import com.bbva.rbvd.lib.r302.transfer.PayloadConfig;
import com.bbva.rbvd.lib.r302.transfer.PayloadStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SimulationEasyYes extends SimulationDecorator {

	private static final Logger LOGGER = LoggerFactory.getLogger(SimulationEasyYes.class);

	public SimulationEasyYes(PreSimulation preSimulation, PostSimulation postSimulation) {
		super(preSimulation, postSimulation);
	}


	@Override
	public LifeSimulationDTO start(LifeSimulationDTO input, RBVDR301 rbvdR301, ApplicationConfigurationService applicationConfigurationService) {
		LOGGER.info("***** SimulationEasyYes - start - START *****");
		LOGGER.info("***** SimulationEasyYes - start - input : {} *****",input);

		//Configuraciones previas
		PayloadConfig payloadConfig = this.getPreSimulation().getConfig(input);

		IInsrEasyYesBusiness seguroEasyYes = new InsrEasyYesBusinessImpl(rbvdR301,applicationConfigurationService);

		PayloadStore payloadStore = seguroEasyYes.doEasyYes(payloadConfig);

		//guardar en bd
		this.getPostSimulation().end(payloadStore);

		//Actualizacion tipo documento en salida trx
		payloadStore.getResponse().getHolder().getIdentityDocument().getDocumentType().setId(payloadConfig.getProperties().getDocumentTypeIdAsText());

		//llamada a gifole
		IGifoleBusiness iGifoleBusiness = new GifoleBusinessImpl(rbvdR301,applicationConfigurationService);
		iGifoleBusiness.serviceAddGifole(payloadStore.getResponse(),payloadConfig.getCustomerListASO());


		LOGGER.info("***** RBVDR302Impl - SimulationEasyYes RESPONSE : {} *****",payloadStore.getResponse());
		LOGGER.info("***** RBVDR302Impl - SimulationEasyYes.start() END *****");
		return payloadStore.getResponse();
	}


}
