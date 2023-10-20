package com.bbva.rbvd.lib.r302.pattern.product;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;
import com.bbva.rbvd.lib.r044.RBVDR044;
import com.bbva.rbvd.lib.r301.RBVDR301;
import com.bbva.rbvd.lib.r302.business.IGifoleBusiness;
import com.bbva.rbvd.lib.r302.business.IInsrDynamicLifeBusiness;
import com.bbva.rbvd.lib.r302.business.impl.GifoleBusinessImpl;
import com.bbva.rbvd.lib.r302.business.impl.InsrVidaDinamicoBusinessImpl;
import com.bbva.rbvd.lib.r302.pattern.PostSimulation;
import com.bbva.rbvd.lib.r302.pattern.PreSimulation;
import com.bbva.rbvd.lib.r302.pattern.impl.SimulationDecorator;
import com.bbva.rbvd.lib.r302.transfer.PayloadConfig;
import com.bbva.rbvd.lib.r302.transfer.PayloadStore;
import com.bbva.rbvd.lib.r302.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SimulationVidaDinamico extends SimulationDecorator {
	private static final Logger LOGGER = LoggerFactory.getLogger(SimulationVidaDinamico.class);

	private RBVDR044 rbvdr044;

	public SimulationVidaDinamico(PreSimulation preSimulation, PostSimulation postSimulation, RBVDR044 rbvdr044) {
		super(preSimulation, postSimulation);
		this.rbvdr044 = rbvdr044;
	}

	@Override
	public LifeSimulationDTO start(LifeSimulationDTO input, RBVDR301 rbvdR301, ApplicationConfigurationService applicationConfigurationService) {
		LOGGER.info("***** RBVDR302Impl - SimulationVidaDinamico - start START | input {} *****",input);

		PayloadConfig payloadConfig = this.getPreSimulation().getConfig(input);

		IInsrDynamicLifeBusiness seguroVidaDinamico = new InsrVidaDinamicoBusinessImpl(rbvdR301, applicationConfigurationService);

		String simulationId = input.getExternalSimulationId();
		//ejecucion servicio rimac
		PayloadStore payloadStore = seguroVidaDinamico.doDynamicLife(payloadConfig);
		LOGGER.info("***** RBVDR302Impl - SimulationVidaDinamico.start() -  payloadStore {} *****",payloadStore);
		LOGGER.info("***** RBVDR302Impl - SimulationVidaDinamico.start()  ***** Response: {}", payloadStore.getResponse());

		if(ValidationUtil.isFirstCalled(simulationId)){
			this.getPostSimulation().end(payloadStore);

			//LLAMADA A GIFOLE!
			IGifoleBusiness iGifoleBusiness = new GifoleBusinessImpl(rbvdR301, applicationConfigurationService);
			iGifoleBusiness.callGifoleDynamicService(payloadStore.getResponse(), payloadConfig.getCustomerListASO(), this.rbvdr044);
		}

		LOGGER.info("***** RBVDR302Impl - SimulationVidaDinamico.start() END *****");

		return payloadStore.getResponse();
	}

	public static final class Builder {
		private PreSimulation preSimulation;
		private PostSimulation postSimulation;
		private RBVDR044 rbvdr044;

		private Builder() {
		}

		public static Builder An() {
			return new Builder();
		}

		public Builder withPreSimulation(PreSimulation preSimulation) {
			this.preSimulation = preSimulation;
			return this;
		}

		public Builder withPostSimulation(PostSimulation postSimulation) {
			this.postSimulation = postSimulation;
			return this;
		}

		public Builder withRbvdr044(RBVDR044 rbvdr044) {
			this.rbvdr044 = rbvdr044;
			return this;
		}

		public SimulationVidaDinamico build() {
			return new SimulationVidaDinamico(preSimulation, postSimulation, rbvdr044);
		}
	}
}
