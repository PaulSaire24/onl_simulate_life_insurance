package com.bbva.rbvd.lib.r302.pattern.impl;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;
import com.bbva.rbvd.lib.r301.RBVDR301;
import com.bbva.rbvd.lib.r302.transfer.PayloadConfig;
import com.bbva.rbvd.lib.r302.transfer.PayloadStore;
import com.bbva.rbvd.lib.r302.business.IInsrDynamicLifeBusiness;
import com.bbva.rbvd.lib.r302.business.impl.InsrVidaDinamicoBusinessImpl;
import com.bbva.rbvd.lib.r302.pattern.PostSimulation;
import com.bbva.rbvd.lib.r302.pattern.PreSimulation;
import com.bbva.rbvd.lib.r302.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimulationVidaDinamico extends SimulationDecorator{
	private static final Logger LOGGER = LoggerFactory.getLogger(SimulationVidaDinamico.class);

	public SimulationVidaDinamico(PreSimulation preSimulation, PostSimulation postSimulation) {
		super(preSimulation, postSimulation );
	}

	@Override
	public LifeSimulationDTO start(LifeSimulationDTO input, RBVDR301 rbvdR301, ApplicationConfigurationService applicationConfigurationService) {
		LOGGER.info("***** SimulationVidaDinamico - start START | input {} *****",input);

		PayloadConfig payloadConfig = this.getPreSimulation().getConfig(input);

		IInsrDynamicLifeBusiness seguroVidaDinamico = new InsrVidaDinamicoBusinessImpl(rbvdR301, applicationConfigurationService);

		String simulationId = payloadConfig.getInput().getExternalSimulationId();
		//ejecucion servicio rimac
		PayloadStore payloadStore = seguroVidaDinamico.doDynamicLife(payloadConfig);
		LOGGER.info("***** SimulationVidaDinamico - start | payloadStore {} *****",payloadStore);

		if(ValidationUtil.isFirstCalled(simulationId)){
			this.getPostSimulation().end(payloadStore);
		}

		//Actualizacion tipo documento en salida trx
		payloadStore.getResponse().getHolder().getIdentityDocument().getDocumentType().setId(payloadConfig.getProperties().getDocumentTypeIdAsText());

		LOGGER.info("***** RBVDR302Impl - SimulationVidaDinamico.start()  ***** Response: {}", payloadStore.getResponse());

		LOGGER.info("***** RBVDR302Impl - SimulationVidaDinamico.start() END *****");

		return payloadStore.getResponse();
	}


}
