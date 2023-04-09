package com.bbva.rbvd.lib.r302.impl;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.rbvd.dto.lifeinsrc.bo.simulation.InsuranceLifeSimulationBO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDErrors;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * The RBVDR302Impl class...
 */
public class RBVDR302Impl extends RBVDR302Abstract {

	private static final Logger LOGGER = LoggerFactory.getLogger(RBVDR302Impl.class);

	/**
	 * The execute method...
	 */
	@Override
	public LifeSimulationDTO executeGetSimulation(LifeSimulationDTO input) {
		// TODO - Implementation of business logic
		LOGGER.info("***** RBVDR302Impl - executeGetSimulation START *****");
		LOGGER.info("***** RBVDR302Impl - executeGetSimulation ***** {}", input);

		try{
			InsuranceLifeSimulationBO simulationBo = mapperHelper.mapInRequestRimacLife(input);
			InsuranceLifeSimulationBO simulationBos = rbvdR301.executeSimulationRimacService(simulationBo, "");
			validation(simulationBos);

		} catch(
		BusinessException ex) {
			LOGGER.debug("***** RBVDR302Impl - executeGetGenerate | Business exception message: {} *****", ex.getMessage());
			this.addAdviceWithDescription(ex.getAdviceCode(), ex.getMessage());
			return null;
		}

		LOGGER.info("***** RBVDR302Impl - executeGetSimulation END *****");
		return null;
	}

	private void validation(InsuranceLifeSimulationBO simulationBos){
		if(Objects.isNull(simulationBos)){
			throw RBVDValidation.build(RBVDErrors.ERROR_FROM_RIMAC);
		}
	}

}
