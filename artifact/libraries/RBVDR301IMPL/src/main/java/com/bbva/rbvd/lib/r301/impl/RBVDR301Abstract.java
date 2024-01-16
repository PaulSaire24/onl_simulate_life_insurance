package com.bbva.rbvd.lib.r301.impl;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.elara.library.AbstractLibrary;
import com.bbva.elara.utility.api.connector.APIConnector;
import com.bbva.elara.utility.api.connector.APIConnectorBuilder;
import com.bbva.ksmk.lib.r002.KSMKR002;
import com.bbva.pbtq.lib.r002.PBTQR002;
import com.bbva.pisd.lib.r014.PISDR014;
import com.bbva.rbvd.lib.r301.RBVDR301;
import com.bbva.rbvd.mock.MockService;

/**
 * This class automatically defines the libraries and utilities that it will use.
 */
public abstract class RBVDR301Abstract extends AbstractLibrary implements RBVDR301 {

	protected ApplicationConfigurationService applicationConfigurationService;

	protected APIConnector externalApiConnector;

	protected APIConnectorBuilder apiConnectorBuilder;

	protected APIConnector internalApiConnector;

	protected PISDR014 pisdR014;

	protected PBTQR002 pbtqR002;

	protected KSMKR002 ksmkR002;
	protected MockService mockService;


	/**
	* @param applicationConfigurationService the this.applicationConfigurationService to set
	*/
	public void setApplicationConfigurationService(ApplicationConfigurationService applicationConfigurationService) {
		this.applicationConfigurationService = applicationConfigurationService;
	}

	/**
	* @param externalApiConnector the this.externalApiConnector to set
	*/
	public void setExternalApiConnector(APIConnector externalApiConnector) {
		this.externalApiConnector = externalApiConnector;
	}

	/**
	* @param apiConnectorBuilder the this.apiConnectorBuilder to set
	*/
	public void setApiConnectorBuilder(APIConnectorBuilder apiConnectorBuilder) {
		this.apiConnectorBuilder = apiConnectorBuilder;
	}

	/**
	* @param internalApiConnector the this.internalApiConnector to set
	*/
	public void setInternalApiConnector(APIConnector internalApiConnector) {
		this.internalApiConnector = internalApiConnector;
	}

	/**
	* @param pisdR014 the this.pisdR014 to set
	*/
	public void setPisdR014(PISDR014 pisdR014) {
		this.pisdR014 = pisdR014;
	}

	/**
	* @param pbtqR002 the this.pbtqR002 to set
	*/
	public void setPbtqR002(PBTQR002 pbtqR002) {
		this.pbtqR002 = pbtqR002;
	}

	/**
	* @param ksmkR002 the this.ksmkR002 to set
	*/
	public void setKsmkR002(KSMKR002 ksmkR002) {
		this.ksmkR002 = ksmkR002;
	}

}