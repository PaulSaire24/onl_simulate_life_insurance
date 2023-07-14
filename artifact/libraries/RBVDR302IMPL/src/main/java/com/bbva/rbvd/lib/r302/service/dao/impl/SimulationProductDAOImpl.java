package com.bbva.rbvd.lib.r302.service.dao.impl;

import com.bbva.rbvd.lib.r302.service.dao.ISimulationDAO;
import com.bbva.rbvd.lib.r302.service.dao.ISimulationProductDAO;

public class SimulationProductDAOImpl implements ISimulationProductDAO {

    @Override
    public void insertSimulationProductDAO() {
        ISimulationProductDAO insertSimulationProductDAO = new InsertSimulationProductDAOImpl(pisdR350);
        ProductSimulationDAO simulaProductDAO = productSimulationDAO.insertSimulationProductDAO();
    }
    return simulaProductDAO;
}
