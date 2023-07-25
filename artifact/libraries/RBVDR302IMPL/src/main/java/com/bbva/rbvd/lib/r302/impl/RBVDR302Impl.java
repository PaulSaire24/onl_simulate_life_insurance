package com.bbva.rbvd.lib.r302.impl;

import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;
import com.bbva.rbvd.lib.r302.pattern.Simulation;
import com.bbva.rbvd.lib.r302.pattern.impl.SimulationEasyYes;
import com.bbva.rbvd.lib.r302.pattern.impl.SimulationParameter;
import com.bbva.rbvd.lib.r302.pattern.impl.SimulationStore;
import com.bbva.rbvd.lib.r302.pattern.impl.SimulationVidaDinamico;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RBVDR302Impl extends RBVDR302Abstract {

	private static final Logger LOGGER = LoggerFactory.getLogger(RBVDR302Impl.class);

	//Ejecuta cómo obtener la simulación
	@Override
	public LifeSimulationDTO executeGetSimulation(LifeSimulationDTO input) {

		LOGGER.info("***** RBVDR302Impl - executeGetSimulation START *****");
		LOGGER.info("***** RBVDR302Impl - executeGetSimulation ***** {}", input);

		LifeSimulationDTO response = new LifeSimulationDTO();
		Simulation simulation;
		if (input.getProduct().getId().equals("840")) {

			simulation = new SimulationEasyYes(
					new SimulationParameter(this.pisdR350, this.rbvdR301, this.applicationConfigurationService)
					, new SimulationStore(this.pisdR350)
			);

			LOGGER.info("***** RBVDR302Impl - SimulationEasyYes ***** {}", simulation);
			response = simulation.start(input, this.rbvdR301, this.applicationConfigurationService);

		} else if (input.getProduct().getId().equals("841")) {

			simulation = new SimulationVidaDinamico(
					new SimulationParameter(this.pisdR350, this.rbvdR301, this.applicationConfigurationService),
					new SimulationStore(this.pisdR350)
			);

			LOGGER.info("***** RBVDR302Impl - SimulationVidaDinamico ***** {}", simulation);
			 response = simulation.start(input, this.rbvdR301, this.applicationConfigurationService);
		}

		//inicio


		LOGGER.info("***** RBVDR302Impl - executeGetSimulation response  ***** {}", response);
		LOGGER.info("***** RBVDR302Impl - executeGetSimulation END  *****");

		return response;

	}
}
