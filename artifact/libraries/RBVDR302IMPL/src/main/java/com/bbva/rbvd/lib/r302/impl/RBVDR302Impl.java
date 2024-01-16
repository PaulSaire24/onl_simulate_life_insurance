package com.bbva.rbvd.lib.r302.impl;

import com.bbva.rbvd.dto.insuranceroyal.error.DetailsErrorDTO;
import com.bbva.rbvd.dto.insuranceroyal.error.ErrorRequestDTO;
import com.bbva.rbvd.dto.insuranceroyal.error.ErrorResponseDTO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;
import com.bbva.rbvd.lib.r302.pattern.Simulation;
import com.bbva.rbvd.lib.r302.pattern.product.SimulationEasyYes;
import com.bbva.rbvd.lib.r302.pattern.impl.SimulationParameter;
import com.bbva.rbvd.lib.r302.pattern.impl.SimulationStore;
import com.bbva.rbvd.lib.r302.pattern.product.SimulationVidaDinamico;
import com.bbva.rbvd.lib.r302.util.ConstantsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


public class RBVDR302Impl extends RBVDR302Abstract {

	private static final Logger LOGGER = LoggerFactory.getLogger(RBVDR302Impl.class);

	//Ejecuta cómo obtener la simulación
	@Override
	public LifeSimulationDTO executeGetSimulation(LifeSimulationDTO input) {

		LOGGER.info("***** RBVDR302Impl - executeGetSimulation START *****");
		LOGGER.info("***** RBVDR302Impl - executeGetSimulation ***** {}", input);

		if(true){

			LOGGER.info("***** RBVDR302Impl - executeFindError START ******");

			ErrorRequestDTO errorRequest = new ErrorRequestDTO();
			List<DetailsErrorDTO> listDetailsError = new ArrayList<>();
			DetailsErrorDTO detailsError = new DetailsErrorDTO();
			detailsError.setCode("COT0002001");
			detailsError.setValue("El campo producto es requerido");
			DetailsErrorDTO detailsError1 = new DetailsErrorDTO();
			detailsError1.setCode("COT0002002");
			detailsError1.setValue("El campo valor de datosParticulares en su elemento 1 debe contener como máximo 7 caracteres");
			listDetailsError.add(detailsError);
			listDetailsError.add(detailsError1);
			errorRequest.setDetails(listDetailsError);
			errorRequest.setTypeErrorScope("RIMAC");
			errorRequest.setHttpCode(400L);

			List<ErrorResponseDTO> errorResponse = this.pisdR403.executeFindError(errorRequest);
			LOGGER.info("***** RBVDR302Impl - executeFindError ***** {}", errorResponse);

			LOGGER.info("***** RBVDR302Impl - executeFindError END ******");

			this.addAdviceWithDescription(errorResponse.get(0).getCode(), errorResponse.get(0).getMessage());

		}


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
					.withRbvdr044(this.rbvdR044 )
					.build();
			LOGGER.info("***** RBVDR302Impl - SimulationVidaDinamico ***** {}", simulation);
			response = simulation.start(input, this.rbvdR301, this.applicationConfigurationService);
		}

		//inicio


		LOGGER.info("***** RBVDR302Impl - executeGetSimulation response  ***** {}", response);
		LOGGER.info("***** RBVDR302Impl - executeGetSimulation END  *****");

		return response;

	}
}
