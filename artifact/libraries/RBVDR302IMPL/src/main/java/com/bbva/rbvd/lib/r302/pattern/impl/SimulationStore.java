package com.bbva.rbvd.lib.r302.pattern.impl;

import com.bbva.rbvd.lib.r302.Transfer.PayloadConfig;
import com.bbva.rbvd.lib.r302.Transfer.PayloadStore;
import com.bbva.rbvd.lib.r302.pattern.PostSimulation;

import java.util.List;

public class SimulationStore implements PostSimulation {

	@Override
	public void end(PayloadStore payloadStore) {
		this.saveSimuation(payloadStore);
		this.saveSimulationProd();
	
	}
	
	
	//@Override
	public void saveSimuation(PayloadStore payloadStore) {
		//llamar
	}

	//@Override
	public void saveSimulationProd() {
		// TODO Auto-generated method stub
		System.out.println("  saveSimuationProducts >>> ....");

	}
		

}
