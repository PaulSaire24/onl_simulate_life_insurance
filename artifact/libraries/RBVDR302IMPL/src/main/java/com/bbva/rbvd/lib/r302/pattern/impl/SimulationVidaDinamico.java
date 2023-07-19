package com.bbva.rbvd.lib.r302.pattern.impl;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.InsuranceLifeSimulationBO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;
import com.bbva.rbvd.lib.r301.RBVDR301;
import com.bbva.rbvd.lib.r302.Transfer.PayloadConfig;
import com.bbva.rbvd.lib.r302.Transfer.PayloadStore;
import com.bbva.rbvd.lib.r302.business.impl.SeguroVidaDinamico;
import com.bbva.rbvd.lib.r302.pattern.PostSimulation;
import com.bbva.rbvd.lib.r302.pattern.PreSimulation;
import com.bbva.rbvd.lib.r302.transform.list.ListInstallmentPlan;

import java.util.Objects;

public class SimulationVidaDinamico extends SimulationDecorator{

	public SimulationVidaDinamico(PreSimulation preSimulation, PostSimulation postSimulation) {
		super(preSimulation, postSimulation );
	}

	@Override
	public LifeSimulationDTO start(RBVDR301 rbvdR301, ApplicationConfigurationService applicationConfigurationService) {
		LifeSimulationDTO response = new LifeSimulationDTO();

		PayloadConfig payloadConfig = this.getPreSimulation().getConfig();
		SeguroVidaDinamico seguroVidaDinamico = new SeguroVidaDinamico(rbvdR301);

		//ejecucion servicio rimac
		InsuranceLifeSimulationBO responseRimac = null;

		if(payloadConfig.getInput().getExternalSimulationId() != null) {
			responseRimac = seguroVidaDinamico.executeModifyQuotationRimacService(
					payloadConfig.getInput(),
					payloadConfig.getCustomerListASO(),
					payloadConfig.getSumCumulus(),
					applicationConfigurationService
			);
		}else{
			responseRimac = seguroVidaDinamico.executeQuotationRimacService(
					payloadConfig.getInput(),
					payloadConfig.getProductInformation().getInsuranceBusinessName(),
					payloadConfig.getCustomerListASO(),
					payloadConfig.getSumCumulus(),
					applicationConfigurationService);

		}

		//construccion de respuesta trx
		ListInstallmentPlan listInstallmentPlan = new ListInstallmentPlan();
		listInstallmentPlan.setApplicationConfigurationService(applicationConfigurationService);
		response = payloadConfig.getInput();
		response.getProduct().setName(responseRimac.getPayload().getProducto());
		response.setExternalSimulationId(responseRimac.getPayload().getCotizaciones().get(0).getCotizacion());
		response.getProduct().setPlans(listInstallmentPlan.getPlansNamesAndRecommendedValuesAndInstallmentsPlans(
				payloadConfig.getListInsuranceProductModalityDAO(),
				responseRimac,
				payloadConfig.getProperties().getSegmentLifePlans().get(0),
				payloadConfig.getProperties().getSegmentLifePlans().get(1),
				payloadConfig.getProperties().getSegmentLifePlans().get(2)));
		//Revisar si es necesario esta línea:
		//response.getProduct().setId(payloadConfig.getInput().getProduct().getId());
		response.getHolder().getIdentityDocument().getDocumentType().setId(payloadConfig.getProperties().getDocumentTypeIdAsText());


		//guardar en bd
		PayloadStore payloadStore = new PayloadStore(
				payloadConfig.getInput().getCreationUser(),
				payloadConfig.getInput().getUserAudit(),
				responseRimac,
				response,
				payloadConfig.getInput().getHolder().getIdentityDocument().getDocumentType().getId(),
				payloadConfig.getProductInformation()
		);

		if(Objects.isNull(payloadConfig.getInput().getExternalSimulationId())){
			this.getPostSimulation().end(payloadStore);
		}

		//LOGGER.debug("***** RBVDR302Impl - executeGetSimulation deb ***** Response: {}", response);
		//LOGGER.info("***** RBVDR302Impl - executeGetSimulation info ***** Response: {}", response);

		//LOGGER.info("***** RBVDR302Impl - executeGetSimulation END *****");

		return response;
	}

}
