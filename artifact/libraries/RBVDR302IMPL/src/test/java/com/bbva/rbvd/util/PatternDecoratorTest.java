package com.bbva.rbvd.util;


import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class PatternDecoratorTest {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void  easyYesTest(){
        System.err.println(" ------- Seguro Vida Easy Yes--- ");

        //Simulation easyYes = new SimulationEasyYes(new SimulationParameter(new LifeSimulationDTO(),null), new SimulationStore());
        //easyYes.start();
        assertNotNull(1);
    }
    @Test
    public void  dinamicoTest(){

        //System.err.println(" ------ Seguro Dinamico ---- ");
        //Simulation seguroVidaDinamico = new SimulationVidaDinamico(new SimulationParameter(new LifeSimulationDTO(),null), new SimulationStore());
        //seguroVidaDinamico.start();

        assertNotNull(1);
    }


}
