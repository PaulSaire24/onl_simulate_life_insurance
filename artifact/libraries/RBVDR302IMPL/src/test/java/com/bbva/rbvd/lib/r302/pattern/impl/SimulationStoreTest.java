package com.bbva.rbvd.lib.r302.pattern.impl;

import com.bbva.pisd.lib.r350.PISDR350;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDProperties;
import com.bbva.rbvd.lib.r301.RBVDR301;
import com.bbva.rbvd.lib.r302.Transfer.PayloadStore;
import com.bbva.rbvd.lib.r302.pattern.PostSimulation;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SimulationStoreTest extends TestCase {

    private PayloadStore payloadStore;
    private PostSimulation simulation;
    private PISDR350 pisdR350;
    @Before
    public void setUp() throws Exception {
        payloadStore = mock(PayloadStore.class);
        simulation = mock(SimulationStore.class);
        pisdR350 = mock(PISDR350.class);
        /*
        rbvdR302.setPisdR350(pisdR350);

        rbvdr301 = mock(RBVDR301.class);
        rbvdR302.setRbvdR301(rbvdr301);
        */
    }
    @Test
    public void endTest() {

        //given
        Map<String,Object> map = new HashMap<>();
        map.put("PISD.SELECT_INSURANCE_SIMULATION_ID_LIFE",new HashMap<>());
        when(this.pisdR350.executeGetASingleRow(RBVDProperties.QUERY_SELECT_INSURANCE_SIMULATION_ID.getValue(),new HashMap<>())).thenReturn(map);

        //simulation = new SimulationStore(pisdR350);
        //BigDecimal resul = simulation.getInsuranceSimulationId();

        //when
        simulation.end(payloadStore);

        //then
        Mockito.verify(pisdR350, Mockito.atLeastOnce()).executeGetASingleRow(Mockito.anyString(), Mockito.anyMap());


    }

}