package com.bbva.rbvd.lib.r302.pattern;

import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;
import com.bbva.rbvd.lib.r302.transfer.PayloadConfig;

public interface PreSimulation {
	PayloadConfig getConfig(LifeSimulationDTO input);
}
