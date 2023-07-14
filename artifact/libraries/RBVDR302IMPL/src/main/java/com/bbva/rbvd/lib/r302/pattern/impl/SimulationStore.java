package com.bbva.rbvd.lib.r302.pattern.impl;

import com.bbva.pisd.lib.r350.PISDR350;
import com.bbva.rbvd.dto.lifeinsrc.dao.SimulationDAO;
import com.bbva.rbvd.lib.r302.Transfer.PayloadStore;
import com.bbva.rbvd.lib.r302.pattern.PostSimulation;
import com.bbva.rbvd.lib.r302.service.dao.IInsuranceSimulationDAO;
import com.bbva.rbvd.lib.r302.service.dao.ISimulationProductDAO;
import com.bbva.rbvd.lib.r302.service.dao.impl.InsuranceSimulationDAOImpl;
import com.bbva.rbvd.lib.r302.service.dao.impl.SimulationProductDAOImpl;
import com.bbva.rbvd.lib.r302.transform.bean.SimulationBean;
import com.bbva.rbvd.lib.r302.transform.map.SimulationMap;
import com.bbva.rbvd.lib.r302.util.ConvertUtil;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

public class SimulationStore implements PostSimulation {

	private PISDR350 pisdR350;
	public void end(PayloadStore payloadStore) {
		this.saveSimuation(payloadStore);
		this.saveSimulationProd();

	}
	//@Override
	public void saveSimuation(PayloadStore payloadStore) {
		IInsuranceSimulationDAO insuranceSimulationDao= new InsuranceSimulationDAOImpl(pisdR350);
		//LOGGER.info("***** PISDR302Impl - Invoking PISDR350 QUERY_SELECT_INSURANCE_SIMULATION_ID *****");
		BigDecimal insuranceSimulationId = insuranceSimulationDao.getSimulationNextVal();

		String creationUser = payloadStore.getCreationUser();
		String userAudit = payloadStore.getUserAudit();
		Date maturityDate = ConvertUtil.generateDate(payloadStore.getResponseRimac().getPayload().getCotizaciones().get(0).getFechaFinVigencia());

		SimulationDAO simulationDAO = SimulationBean.createSimulationDAO(insuranceSimulationId, maturityDate, payloadStore.getResponse());
		Map<String, Object> argumentsForSaveSimulation = SimulationMap.createArgumentsForSaveSimulation(simulationDAO, creationUser, userAudit, payloadStore.getDocumentTypeId());

		insuranceSimulationDao.getInsertInsuranceSimulation(argumentsForSaveSimulation);

	}

	//@Override
	public void saveSimulationProd() {
		// TODO Auto-generated method stub

		ISimulationProductDAO iSimulationProductDAO = new SimulationProductDAOImpl(pisdR350);

		System.out.println("  saveSimuationProducts >>> ....");

	}
}
