package com.bbva.rbvd.lib.r302.pattern.impl;

import com.bbva.rbvd.lib.r302.pattern.PostSimulation;
import com.bbva.rbvd.lib.r302.pattern.PreSimulation;
import com.bbva.rbvd.lib.r302.pattern.Simulation;

public abstract class SimulationDecorator implements Simulation {


	private PreSimulation preSimulation;
	private PostSimulation postSimulation;

	public SimulationDecorator(PreSimulation preSimulation, PostSimulation postSimulation) {
		this.preSimulation = preSimulation;
		this.postSimulation = postSimulation;
	}


	public PreSimulation getPreSimulation() {
		return preSimulation;
	}


	public PostSimulation getPostSimulation() {
		return postSimulation;
	}


}
