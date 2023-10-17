package com.bbva.rbvd.lib.r302.pattern.impl;

import com.bbva.pisd.lib.r350.PISDR350;
import com.bbva.rbvd.dto.lifeinsrc.dao.SimulationDAO;
import com.bbva.rbvd.dto.lifeinsrc.dao.CommonsLifeDAO;
import com.bbva.rbvd.dto.lifeinsrc.dao.SimulationProductDAO;
import com.bbva.rbvd.lib.r302.transfer.PayloadStore;
import com.bbva.rbvd.lib.r302.pattern.PostSimulation;
import com.bbva.rbvd.lib.r302.service.dao.IInsuranceSimulationDAO;
import com.bbva.rbvd.lib.r302.service.dao.ISimulationProductDAO;
import com.bbva.rbvd.lib.r302.service.dao.impl.InsuranceSimulationDAOImpl;
import com.bbva.rbvd.lib.r302.service.dao.impl.SimulationProductDAOImpl;
import com.bbva.rbvd.lib.r302.transform.bean.SimulationBean;
import com.bbva.rbvd.lib.r302.transform.bean.SimulationParticipanBean;
import com.bbva.rbvd.lib.r302.transform.bean.SimulationProductBean;
import com.bbva.rbvd.lib.r302.transform.map.SimulationMap;
import com.bbva.rbvd.lib.r302.transform.map.SimulationParticipantMap;
import com.bbva.rbvd.lib.r302.transform.map.SimulationProductMap;
import com.bbva.rbvd.lib.r302.util.ConvertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

public class SimulationStore implements PostSimulation {

	private static final Logger LOGGER = LoggerFactory.getLogger(SimulationStore.class);
	private PISDR350 pisdR350;

	public SimulationStore(PISDR350 pisdR350) {
		this.pisdR350=pisdR350;
	}

	@Override
	public void end(PayloadStore payloadStore) {
		BigDecimal nextId = this.getInsuranceSimulationId();
		this.saveSimulation(payloadStore, nextId);
		this.saveSimulationProd(payloadStore,nextId);
		this.saveParticipantInformation(payloadStore,nextId);
	}


	public BigDecimal getInsuranceSimulationId(){
		LOGGER.info("***** SimulationStore - getInsuranceSimulationId START *****");

		IInsuranceSimulationDAO insuranceSimulationDao= new InsuranceSimulationDAOImpl(pisdR350);
		BigDecimal simulationNextValue = insuranceSimulationDao.getSimulationNextVal();

		LOGGER.info("***** SimulationStore - getInsuranceSimulationId | simulationNextValue: {} *****",simulationNextValue);
		return simulationNextValue;
	}

	public void saveSimulation(PayloadStore payloadStore, BigDecimal insuranceSimulationId) {

		LOGGER.info("***** SimulationStore - saveSimulation START - arguments: payloadStore {} *****",payloadStore);
		IInsuranceSimulationDAO insuranceSimulationDao= new InsuranceSimulationDAOImpl(pisdR350);

		Date maturityDate = ConvertUtil.generateDate(payloadStore.getResponseRimac().getPayload().getCotizaciones().get(0).getFechaFinVigencia());

		SimulationDAO simulationDAO = SimulationBean.createSimulationDAO(insuranceSimulationId, maturityDate, payloadStore.getResponse());
		LOGGER.info("***** SimulationStore - saveSimulation - simulationDAO {} *****",simulationDAO);

		Map<String, Object> argumentsForSaveSimulation = SimulationMap.createArgumentsForSaveSimulation(simulationDAO, payloadStore.getCreationUser(), payloadStore.getUserAudit(), payloadStore.getDocumentTypeId());
		LOGGER.info("***** SimulationStore - saveSimulation - argumentsForSaveSimulation {} *****",argumentsForSaveSimulation);

		insuranceSimulationDao.insertInsuranceSimulation(argumentsForSaveSimulation);

	}
	public void saveSimulationProd(PayloadStore payloadStore,BigDecimal insuranceSimulationId) {

		LOGGER.info("***** SimulationStore - saveSimulationProd START - arguments: payloadStore {} *****",payloadStore);

		SimulationProductDAO simulationProductDAO = SimulationProductBean.createSimulationProductDAO(
				insuranceSimulationId,
				payloadStore.getProductInformation().getInsuranceProductId(),
				payloadStore.getCreationUser(),
				payloadStore.getUserAudit(),
				payloadStore.getResponse()
		);
		LOGGER.info("***** SimulationStore - saveSimulationProd - simulationProductDAO {} *****",simulationProductDAO);

		Map<String, Object> argumentsForSaveSimulationProduct = SimulationProductMap.createArgumentsForSaveSimulationProduct(simulationProductDAO);
		LOGGER.info("***** SimulationStore - saveSimulationProd - argumentsForSaveSimulationProduct {} *****",argumentsForSaveSimulationProduct);

		ISimulationProductDAO iSimulationProductDAO = new SimulationProductDAOImpl(pisdR350);
		iSimulationProductDAO.insertSimulationProduct(argumentsForSaveSimulationProduct);
	}

	public void saveParticipantInformation(PayloadStore payloadStore,BigDecimal insuranceSimulationId){
		LOGGER.info("***** SimulationStore - saveParticipantInformation START - arguments: payloadStore {} *****",payloadStore);
		CommonsLifeDAO commonsLife  = SimulationParticipanBean.createSimulationParticipant(insuranceSimulationId,payloadStore.getResponse(),
									payloadStore.getCreationUser(),payloadStore.getUserAudit(),payloadStore.getProductInformation().getInsuranceProductId(),payloadStore.getCustomer());
		LOGGER.info("***** SimulationStore - saveParticipantInformation - SimulationParticipantDAO {} *****",commonsLife);
		Map<String, Object> argumentForSaveParticipant = SimulationParticipantMap.createArgumentsForSaveParticipant(commonsLife);
		LOGGER.info("***** SimulationStore - saveParticipantInformation - argumentForSaveParticipant {} *****",argumentForSaveParticipant);
		IInsuranceSimulationDAO insuranceSimulationDao= new InsuranceSimulationDAOImpl(pisdR350);
		insuranceSimulationDao.insertSimulationParticipant(argumentForSaveParticipant);
	}
}
