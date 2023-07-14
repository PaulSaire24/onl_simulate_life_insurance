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
		BigDecimal nextId = this.getInsuranceSimulationId();
		this.saveSimulation(payloadStore, nextId);
		this.saveSimulationProd();

	}
	public BigDecimal getInsuranceSimulationId(){
		IInsuranceSimulationDAO insuranceSimulationDao= new InsuranceSimulationDAOImpl(pisdR350);

		return insuranceSimulationDao.getSimulationNextVal();
	}
	//@Override
	public void saveSimulation(PayloadStore payloadStore, BigDecimal insuranceSimulationId) {

		//LOGGER.info("***** PISDR302Impl - Invoking PISDR350 QUERY_SELECT_INSURANCE_SIMULATION_ID *****");
		IInsuranceSimulationDAO insuranceSimulationDao= new InsuranceSimulationDAOImpl(pisdR350);

		//reationUser(payloadStore.getCreationUser());
		//payloadStore.setUserAudit(payloadStore.getUserAudit());
		Date maturityDate = ConvertUtil.generateDate(payloadStore.getResponseRimac().getPayload().getCotizaciones().get(0).getFechaFinVigencia());

		SimulationDAO simulationDAO = SimulationBean.createSimulationDAO(insuranceSimulationId, maturityDate, payloadStore.getResponse());
		Map<String, Object> argumentsForSaveSimulation = SimulationMap.createArgumentsForSaveSimulation(simulationDAO, payloadStore.getCreationUser(), payloadStore.getUserAudit(), payloadStore.getDocumentTypeId());

		insuranceSimulationDao.getInsertInsuranceSimulation(argumentsForSaveSimulation);

	}

	//@Override
	public void saveSimulationProd() {
		// TODO Auto-generated method stub

		ISimulationProductDAO iSimulationProductDAO = new SimulationProductDAOImpl(pisdR350);

		System.out.println("  saveSimuationProducts >>> ....");

	}
}
