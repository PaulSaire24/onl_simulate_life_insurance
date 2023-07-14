package com.bbva.rbvd.lib.r302.service.dao.impl;
import com.bbva.pisd.lib.r350.PISDR350;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDErrors;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDProperties;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDValidation;
import com.bbva.rbvd.lib.r302.service.dao.ISimulationProductDAO;
import com.bbva.rbvd.lib.r302.util.ValidationUtil;
import java.util.Map;

public class SimulationProductDAOImpl implements ISimulationProductDAO {
    private final PISDR350 pisdR350;
    private ValidationUtil validationUtil;
    public SimulationProductDAOImpl(PISDR350 pisdR350) {
        this.pisdR350 = pisdR350;
    }

    @Override
    public void insertSimulationProduct(Map<String,Object> argument) {

        //LOGGER.info("***** PISDR302Impl - Invoking PISDR350 QUERY_INSERT_INSRNC_SIMLT_PRD *****");
        int idInsSimulation = this.pisdR350.executeInsertSingleRow(RBVDProperties.QUERY_INSERT_INSRNC_SIMLT_PRD.getValue(),argument);
        if(idInsSimulation != 1) {
            throw RBVDValidation.build(RBVDErrors.INSERTION_ERROR_IN_SIMULATION_PRD_TABLE);
        }
    }

}
