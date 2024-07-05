package com.bbva.rbvd.lib.r302.impl;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;
import com.bbva.rbvd.lib.r302.pattern.Simulation;
import com.bbva.rbvd.lib.r302.pattern.product.SimulationEasyYes;
import com.bbva.rbvd.lib.r302.pattern.impl.SimulationParameter;
import com.bbva.rbvd.lib.r302.pattern.impl.SimulationStore;
import com.bbva.rbvd.lib.r302.pattern.product.SimulationVidaDinamico;
import com.bbva.rbvd.lib.r302.util.ConstantsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RBVDR302Impl extends RBVDR302Abstract {

	private static final Logger LOGGER = LoggerFactory.getLogger(RBVDR302Impl.class);

	//Ejecuta cómo obtener la simulación
	@Override
	public LifeSimulationDTO executeGetSimulation(LifeSimulationDTO input) {

		try{
			LOGGER.info("***** RBVDR302Impl - executeGetSimulation START *****");
			LOGGER.info("***** RBVDR302Impl - executeGetSimulation ***** {}", input);

			LifeSimulationDTO response = new LifeSimulationDTO();
			Simulation simulation;
			if (ConstantsUtil.Product.EASY_YES.equals(input.getProduct().getId())) {
				simulation = SimulationEasyYes.Builder.An()
						.withPreSimulation( SimulationParameter.Builder.an().withPisdR350(this.pisdR350).withRbvdR301(this.rbvdR301).withApplicationConfigurationService(this.applicationConfigurationService).build())
						.withPostSimulation(new SimulationStore(this.pisdR350))
						.build();
				LOGGER.info("***** RBVDR302Impl - SimulationEasyYes ***** {}", simulation);
				response = simulation.start(input, this.rbvdR301, this.applicationConfigurationService);

			} else if (ConstantsUtil.Product.DYNAMIC_LIFE.equals(input.getProduct().getId())) {

				simulation = SimulationVidaDinamico.Builder.An()
						.withPreSimulation(SimulationParameter.Builder.an().withPisdR350(this.pisdR350).withRbvdR301(this.rbvdR301).withApplicationConfigurationService(this.applicationConfigurationService).build())
						.withPostSimulation( new SimulationStore(this.pisdR350))
						.withRbvdr044(this.rbvdR044)
						.build();
				LOGGER.info("***** RBVDR302Impl - SimulationVidaDinamico ***** {}", simulation);
				response = simulation.start(input, this.rbvdR301, this.applicationConfigurationService);
			}

			LOGGER.info("***** RBVDR302Impl - executeGetSimulation response  ***** {}", response);
			LOGGER.info("***** RBVDR302Impl - executeGetSimulation END  *****");

			return response;
		} catch (BusinessException ex) {
			this.addAdviceWithDescription(ex.getAdviceCode(),ex.getMessage());
			return null;
		}

	}
}
