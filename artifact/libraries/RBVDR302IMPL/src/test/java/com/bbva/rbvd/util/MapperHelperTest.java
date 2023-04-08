package com.bbva.rbvd.util;

import com.bbva.rbvd.dto.lifeinsrc.bo.simulation.InsuranceLifeSimulationBO;
import com.bbva.rbvd.dto.lifeinsrc.mock.MockData;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;
import com.bbva.rbvd.lib.r302.impl.util.MapperHelper;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class MapperHelperTest {

    private final MapperHelper mapperHelper = new MapperHelper();

    private MockData mockDTO;

    private LifeSimulationDTO requestInput;

    private LifeSimulationDTO responseInput;

    @Before
    public void setUp() throws Exception {

        mockDTO = MockData.getInstance();

        requestInput = mockDTO.getInsuranceSimulationRequest();
        responseInput = mockDTO.getInsuranceSimulationResponse();

    }

    @Test
    public void  mapInRequestDocument_OK(){
        InsuranceLifeSimulationBO request = this.mapperHelper.mapInRequestRimacLife(requestInput);
        assertNotNull(request);
    }


}
