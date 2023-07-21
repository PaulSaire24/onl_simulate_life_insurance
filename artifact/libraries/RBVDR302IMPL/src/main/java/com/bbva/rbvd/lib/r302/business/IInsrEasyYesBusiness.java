package com.bbva.rbvd.lib.r302.business;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;
import com.bbva.rbvd.lib.r302.Transfer.PayloadConfig;
import com.bbva.rbvd.lib.r302.Transfer.PayloadStore;


public interface IInsrEasyYesBusiness {

    void serviceAddGifole(LifeSimulationDTO response, CustomerListASO responseListCustomers);
     PayloadStore doEasyYes(ApplicationConfigurationService applicationConfigurationService, PayloadConfig payloadConfig);

}
