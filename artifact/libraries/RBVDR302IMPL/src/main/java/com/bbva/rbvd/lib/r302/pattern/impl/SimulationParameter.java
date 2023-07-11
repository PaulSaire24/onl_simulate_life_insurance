package com.bbva.rbvd.lib.r302.pattern.impl;

import com.bbva.rbvd.lib.r302.pattern.PreSimulation;

public class SimulationParameter implements PreSimulation {

	@Override
	public void Config() {
		this.getProperties();
		this.getProduct();
		this.getCumulos();
		this.getCustomer();
		this.getTier();
		
	}
	
	
	//@Override
	public void getProperties() {
		// TODO Auto-generated method stub
		System.out.println(" get Properties ....");
	}

	//@Override
	public void getProduct() {
		// TODO Auto-generated method stub
		System.out.println(" get Products >>> ....");

	}

	//@Override
	public void getCumulos() {
		// TODO Auto-generated method stub
		System.out.println(" get Cumulos () ....");

	}

	//@Override
	public void getCustomer() {
		// TODO Auto-generated method stub
		System.out.println(" get Customer :D  ....");

	}

	//@Override
		public void getTier() {
			// TODO Auto-generated method stub
			System.out.println(" get Tier :D  ....");

		}

		

}
