package com.bbva.rbvd.lib.r301.impl;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.elara.library.AbstractLibrary;
import com.bbva.elara.utility.api.connector.APIConnector;
import com.bbva.elara.utility.api.connector.APIConnectorBuilder;
import com.bbva.pisd.lib.r014.PISDR014;
import com.bbva.rbvd.lib.r301.RBVDR301;
import com.bbva.rbvd.mock.MockService;


public abstract class RBVDR301Abstract extends AbstractLibrary implements RBVDR301 {

	protected ApplicationConfigurationService applicationConfigurationService;

	protected APIConnector externalApiConnector;

	protected APIConnectorBuilder apiConnectorBuilder;

	protected APIConnector internalApiConnector;

	protected PISDR014 pisdR014;

	protected MockService mockService;


	public void setApplicationConfigurationService(ApplicationConfigurationService applicationConfigurationService) {
		this.applicationConfigurationService = applicationConfigurationService;
	}


	public void setExternalApiConnector(APIConnector externalApiConnector) {
		this.externalApiConnector = externalApiConnector;
	}

	public void setApiConnectorBuilder(APIConnectorBuilder apiConnectorBuilder) {
		this.apiConnectorBuilder = apiConnectorBuilder;
	}


	public void setInternalApiConnector(APIConnector internalApiConnector) {
		this.internalApiConnector = internalApiConnector;
	}


	public void setPisdR014(PISDR014 pisdR014) {
		this.pisdR014 = pisdR014;
	}

	public void setMockService(MockService mockService) { this.mockService = mockService; }

}