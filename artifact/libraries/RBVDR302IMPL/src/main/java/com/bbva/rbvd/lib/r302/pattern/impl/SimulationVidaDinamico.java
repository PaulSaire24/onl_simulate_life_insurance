package com.bbva.rbvd.lib.r302.pattern.impl;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.InsuranceLifeSimulationBO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;
import com.bbva.rbvd.lib.r301.RBVDR301;
import com.bbva.rbvd.lib.r302.Transfer.PayloadConfig;
import com.bbva.rbvd.lib.r302.Transfer.PayloadStore;
import com.bbva.rbvd.lib.r302.business.impl.SeguroEasyYesImpl;
import com.bbva.rbvd.lib.r302.business.impl.SeguroVidaDinamico;
import com.bbva.rbvd.lib.r302.pattern.PostSimulation;
import com.bbva.rbvd.lib.r302.pattern.PreSimulation;
import com.bbva.rbvd.lib.r302.transform.list.ListInstallmentPlan;
import com.bbva.rbvd.lib.r302.transform.objects.QuotationRimac;
import com.bbva.rbvd.lib.r302.util.ValidationUtil;

import java.util.Arrays;

public class SimulationVidaDinamico extends SimulationDecorator{

	public SimulationVidaDinamico(PreSimulation preSimulation, PostSimulation postSimulation) {
		super(preSimulation, postSimulation );
	}

	@Override
	public LifeSimulationDTO start(RBVDR301 rbvdR301, ApplicationConfigurationService applicationConfigurationService) {
		LifeSimulationDTO response = new LifeSimulationDTO();
		ValidationUtil validationUtil = new ValidationUtil(rbvdR301);
		ListInstallmentPlan listInstallmentPlan = new ListInstallmentPlan();
		listInstallmentPlan.setApplicationConfigurationService(applicationConfigurationService);
		PayloadConfig payloadConfig = this.getPreSimulation().getConfig();
		SeguroVidaDinamico seguroVidaDinamico = new SeguroVidaDinamico();
		seguroVidaDinamico.setRbvdR301(rbvdR301);

		//ejecucion servicio rimac
		InsuranceLifeSimulationBO requestRimac = QuotationRimac.mapInRequestRimacLife(payloadConfig.getInput(),payloadConfig.getSumCumulus());
		requestRimac.getPayload().setProducto(payloadConfig.getProductInformation().getInsuranceBusinessName());
		InsuranceLifeSimulationBO responseRimac = seguroVidaDinamico.executeQuotationRimacService(payloadConfig.getInput(),requestRimac);

		validationUtil.validation(responseRimac);

		//construccion de respuesta trx
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
		this.getPostSimulation().end(payloadStore);

		//llamada a gifole
		seguroEasyYes.serviceAddGifole(payloadConfig.getInput(),payloadConfig.getCustomerListASO());

		//LOGGER.debug("***** RBVDR302Impl - executeGetSimulation deb ***** Response: {}", response);
		//LOGGER.info("***** RBVDR302Impl - executeGetSimulation info ***** Response: {}", response);

		//LOGGER.info("***** RBVDR302Impl - executeGetSimulation END *****");

		return response;
	}

}
