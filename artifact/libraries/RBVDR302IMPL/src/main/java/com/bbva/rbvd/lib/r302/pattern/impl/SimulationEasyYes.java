package com.bbva.rbvd.lib.r302.pattern.impl;

import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.InsuranceLifeSimulationBO;
import com.bbva.rbvd.lib.r302.Transfer.PayloadConfig;
import com.bbva.rbvd.lib.r302.pattern.PostSimulation;
import com.bbva.rbvd.lib.r302.pattern.PreSimulation;
import com.bbva.rbvd.lib.r302.transform.objects.QuotationRimac;

import java.util.Arrays;

public class SimulationEasyYes extends SimulationDecorator{

	public SimulationEasyYes(PreSimulation preSimulation, PostSimulation postSimulation) {
		super(preSimulation, postSimulation);
	}


	@Override
	public void start() {
		// TODO Auto-generated method stub
		PayloadConfig payloadConfig = this.getPreSimulation().getConfig();



		System.err.println("Call Gifole");
		
		this.getPostSimulation().end(null);

	}
	


}
