package com.bbva.rbvd.lib.r302.pattern.impl;

import com.bbva.rbvd.lib.r302.pattern.PostSimulation;
import com.bbva.rbvd.lib.r302.pattern.PreSimulation;

import java.util.Arrays;

public class SimulationVidaDinamico extends SimulationDecorator{

	public SimulationVidaDinamico(PreSimulation preSimulation, PostSimulation postSimulation) {
		super(preSimulation, postSimulation );
	}

	@Override
	public void start() {
		this.getPreSimulation().Config();
		System.out.println("Call Rimac");
		this.getPostSimulation().end(Arrays.asList("Data to DB"));
	}

}
