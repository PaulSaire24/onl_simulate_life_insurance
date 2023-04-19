package com.bbva.rbvd.lib.r301;

import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.InsuranceLifeSimulationBO;

/**
 * The  interface RBVDR301 class...
 */
public interface RBVDR301 {

	/**
	 * The execute method...
	 */

	InsuranceLifeSimulationBO executeSimulationRimacService(final InsuranceLifeSimulationBO payload, String traceId);

}
