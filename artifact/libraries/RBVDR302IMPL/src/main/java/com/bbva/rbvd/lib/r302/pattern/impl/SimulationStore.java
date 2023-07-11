package com.bbva.rbvd.lib.r302.pattern.impl;

import com.bbva.rbvd.lib.r302.pattern.PostSimulation;

import java.util.List;

public class SimulationStore implements PostSimulation {

	@Override
	public void end(List<String> data) {
		this.saveSimuation();
		this.saveSimulationProd();
	
	}
	
	
	//@Override
	public void saveSimuation() {
		// TODO Auto-generated method stub
		System.out.println("  saveSimuation ....");
	}

	//@Override
	public void saveSimulationProd() {
		// TODO Auto-generated method stub
		System.out.println("  saveSimuationProducts >>> ....");

	}
		

}
