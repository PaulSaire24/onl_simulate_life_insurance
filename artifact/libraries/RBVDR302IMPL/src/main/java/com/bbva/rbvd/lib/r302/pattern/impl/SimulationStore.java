package com.bbva.rbvd.lib.r302.pattern.impl;

import com.bbva.rbvd.lib.r302.pattern.PostSimulation;
import com.bbva.rbvd.lib.r302.service.dao.ISimulationDAO;
import com.bbva.rbvd.lib.r302.service.dao.ISimulationProductDAO;

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
		//Aquí viene la Simulación donde se llamará a ISimulationDAO
		ISimulationDAO iSimulationDAO;
		System.out.println("  saveSimuation ....");
	}

	//@Override
	public void saveSimulationProd() {
		// TODO Auto-generated method stub
		//Aquí viene la Simulación donde se llamará a ISimulationProductDAO
		ISimulationProductDAO  iSimulationProductDAO;
		System.out.println("  saveSimuationProducts >>> ....");

	}
		

}
