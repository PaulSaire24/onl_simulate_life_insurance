package com.bbva.rbvd.lib.r302.pattern.impl;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;
import com.bbva.rbvd.lib.r301.RBVDR301;
import com.bbva.rbvd.lib.r302.Transfer.PayloadConfig;
import com.bbva.rbvd.lib.r302.Transfer.PayloadStore;
import com.bbva.rbvd.lib.r302.business.ISeguroVidaDinamico;
import com.bbva.rbvd.lib.r302.business.impl.InsrVidaDinamicoBusinessImpl;
import com.bbva.rbvd.lib.r302.pattern.PostSimulation;
import com.bbva.rbvd.lib.r302.pattern.PreSimulation;
import com.bbva.rbvd.lib.r302.util.ValidationUtil;


public class SimulationVidaDinamico extends SimulationDecorator{

	public SimulationVidaDinamico(PreSimulation preSimulation, PostSimulation postSimulation) {
		super(preSimulation, postSimulation );
	}

	@Override
	public LifeSimulationDTO start(RBVDR301 rbvdR301, ApplicationConfigurationService applicationConfigurationService) {

		PayloadConfig payloadConfig = this.getPreSimulation().getConfig();

		ISeguroVidaDinamico seguroVidaDinamico = new InsrVidaDinamicoBusinessImpl(rbvdR301);

		String simulationId = payloadConfig.getInput().getExternalSimulationId();
		//ejecucion servicio rimac
		PayloadStore payloadStore = seguroVidaDinamico.doDynamicLife(applicationConfigurationService, payloadConfig);

		if(ValidationUtil.isFirstCalled(simulationId)){
			this.getPostSimulation().end(payloadStore);
		}

		return payloadStore.getResponse();
	}

}
