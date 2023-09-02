package com.bbva.rbvd.lib.r302.business;

import com.bbva.pisd.dto.insurance.aso.CustomerListASO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;
import com.bbva.rbvd.lib.r044.RBVDR044;

public interface IGifoleBusiness {


    void serviceAddGifole(LifeSimulationDTO response, CustomerListASO responseListCustomers);

    void callGifoleDynamicService(LifeSimulationDTO input, CustomerListASO inputListCustomers, RBVDR044 rbvdr044);

}
