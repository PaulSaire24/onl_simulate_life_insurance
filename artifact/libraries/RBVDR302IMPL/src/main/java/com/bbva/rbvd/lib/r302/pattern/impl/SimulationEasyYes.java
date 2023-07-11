package com.bbva.rbvd.lib.r302.pattern.impl;

import com.bbva.rbvd.lib.r302.pattern.PostSimulation;
import com.bbva.rbvd.lib.r302.pattern.PreSimulation;

import java.util.Arrays;

public class SimulationEasyYes extends SimulationDecorator{

	public SimulationEasyYes(PreSimulation preSimulation, PostSimulation postSimulation) {
		super(preSimulation, postSimulation);
	}


	@Override
	public void start() {
		// TODO Auto-generated method stub
		this.getPreSimulation().getConfig();
		
		System.err.println("Call Rimac Service");
		System.err.println("Call Gifole");
		
		this.getPostSimulation().end(Arrays.asList("Data to DB"));

	}
	


}
