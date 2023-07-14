package com.bbva.rbvd.lib.r302.service.dao.impl;

import com.bbva.rbvd.lib.r302.service.dao.ISimulationDAO;

public class SimulationDAOImpl implements ISimulationDAO {


    @Override
    public void insertSimulationDAO() {
        ISimulationDAO insertSimulationDAO = new InsertSimulationDAOImpl(pisdR350);
        ProductSimulationDAO simulaDAO = simulationDAO.inserSimulationDAO();
    }

    return simulaDAO;
}
