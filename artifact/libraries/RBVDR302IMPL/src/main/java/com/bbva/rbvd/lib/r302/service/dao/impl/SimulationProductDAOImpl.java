package com.bbva.rbvd.lib.r302.service.dao.impl;
import com.bbva.pisd.lib.r350.PISDR350;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDErrors;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDProperties;
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
    public void insertSimulationProduct(Map<String,Object> argument, ) {

        validationUtil.validateInsertion(this.pisdR350.executeInsertSingleRow(RBVDProperties.QUERY_INSERT_INSRNC_SIMLT_PRD.getValue(), argumentsForSaveSimulationProduct), RBVDErrors.INSERTION_ERROR_IN_SIMULATION_PRD_TABLE);

        ISimulationProductDAO insertSimulationProductDAO = new InsertSimulationProductDAOImpl(pisdR350);
        ProductSimulationDAO simulaProductDAO = productSimulationDAO.insertSimulationProductDAO();
    }
    return simulaProductDAO;
}
