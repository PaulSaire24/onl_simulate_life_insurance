package com.bbva.rbvd.lib.r302.pattern.impl;

import com.bbva.pisd.lib.r350.PISDR350;
import com.bbva.rbvd.dto.lifeinsrc.dao.SimulationDAO;
import com.bbva.rbvd.dto.lifeinsrc.dao.SimulationProductDAO;
import com.bbva.rbvd.lib.r302.Transfer.PayloadStore;
import com.bbva.rbvd.lib.r302.pattern.PostSimulation;
import com.bbva.rbvd.lib.r302.service.dao.IInsuranceSimulationDAO;
import com.bbva.rbvd.lib.r302.service.dao.ISimulationProductDAO;
import com.bbva.rbvd.lib.r302.service.dao.impl.InsuranceSimulationDAOImpl;
import com.bbva.rbvd.lib.r302.service.dao.impl.SimulationProductDAOImpl;
import com.bbva.rbvd.lib.r302.transform.bean.SimulationBean;
import com.bbva.rbvd.lib.r302.transform.bean.SimulationProductBean;
import com.bbva.rbvd.lib.r302.transform.map.SimulationMap;
import com.bbva.rbvd.lib.r302.transform.map.SimulationProductMap;
import com.bbva.rbvd.lib.r302.util.ConvertUtil;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

public class SimulationStore implements PostSimulation {

	private PISDR350 pisdR350;
	public void end(PayloadStore payloadStore) {
		BigDecimal nextId = this.getInsuranceSimulationId();
		this.saveSimulation(payloadStore, nextId);
		this.saveSimulationProd(payloadStore,nextId);
	}
	public BigDecimal getInsuranceSimulationId(){
		IInsuranceSimulationDAO insuranceSimulationDao= new InsuranceSimulationDAOImpl(pisdR350);
		return insuranceSimulationDao.getSimulationNextVal();
	}
	//@Override
	public void saveSimulation(PayloadStore payloadStore, BigDecimal insuranceSimulationId) {

		//LOGGER.info("***** PISDR302Impl - Invoking PISDR350 QUERY_SELECT_INSURANCE_SIMULATION_ID *****");
		IInsuranceSimulationDAO insuranceSimulationDao= new InsuranceSimulationDAOImpl(pisdR350);

		Date maturityDate = ConvertUtil.generateDate(payloadStore.getResponseRimac().getPayload().getCotizaciones().get(0).getFechaFinVigencia());

		SimulationDAO simulationDAO = SimulationBean.createSimulationDAO(insuranceSimulationId, maturityDate, payloadStore.getResponse());
		Map<String, Object> argumentsForSaveSimulation = SimulationMap.createArgumentsForSaveSimulation(simulationDAO, payloadStore.getCreationUser(), payloadStore.getUserAudit(), payloadStore.getDocumentTypeId());

		insuranceSimulationDao.getInsertInsuranceSimulation(argumentsForSaveSimulation);

	}

	//@Override
	public void saveSimulationProd(PayloadStore payloadStore,BigDecimal insuranceSimulationId) {

		SimulationProductDAO simulationProductDAO = SimulationProductBean.createSimulationProductDAO(
				insuranceSimulationId,
				payloadStore.getProductInformation().getInsuranceProductId(),
				payloadStore.getCreationUser(),
				payloadStore.getUserAudit(),
				payloadStore.getResponse()
		);

		Map<String, Object> argumentsForSaveSimulationProduct = SimulationProductMap.createArgumentsForSaveSimulationProduct(simulationProductDAO);

        //LOGGER.info("***** PISDR302Impl - Invoking PISDR350 QUERY_INSERT_INSRNC_SIMLT_PRD *****");
		ISimulationProductDAO iSimulationProductDAO = new SimulationProductDAOImpl(pisdR350);
		iSimulationProductDAO.insertSimulationProduct(argumentsForSaveSimulationProduct);
	}
}
