package com.bbva.rbvd.lib.r302.impl;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.elara.library.AbstractLibrary;
import com.bbva.pisd.lib.r350.PISDR350;
import com.bbva.rbvd.lib.r301.RBVDR301;
import com.bbva.rbvd.lib.r302.RBVDR302;
// import com.bbva.rbvd.lib.r302.impl.util.MapperHelper;

/**
 * This class automatically defines the libraries and utilities that it will use.
 */
public abstract class RBVDR302Abstract extends AbstractLibrary implements RBVDR302 {

	protected ApplicationConfigurationService applicationConfigurationService;

	protected RBVDR301 rbvdR301;

	protected PISDR350 pisdR350;

	//protected MapperHelper mapperHelper;


	/**
	* @param applicationConfigurationService the this.applicationConfigurationService to set
	*/
	public void setApplicationConfigurationService(ApplicationConfigurationService applicationConfigurationService) {
		this.applicationConfigurationService = applicationConfigurationService;
	}

	/**
	* @param rbvdR301 the this.rbvdR301 to set
	*/
	public void setRbvdR301(RBVDR301 rbvdR301) {
		this.rbvdR301 = rbvdR301;
	}

	/**
	* @param pisdR350 the this.pisdR350 to set
	*/
	public void setPisdR350(PISDR350 pisdR350) {
		this.pisdR350 = pisdR350;
	}

	//public void setMapperHelper(MapperHelper mapperHelper) { this.mapperHelper = mapperHelper; }

}