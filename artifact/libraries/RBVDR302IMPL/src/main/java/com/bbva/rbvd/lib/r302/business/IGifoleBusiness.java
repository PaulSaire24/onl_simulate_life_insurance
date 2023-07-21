package com.bbva.rbvd.lib.r302.business;

import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;

public interface IGifoleBusiness {


    void serviceAddGifole(LifeSimulationDTO response, CustomerListASO responseListCustomers);

}
