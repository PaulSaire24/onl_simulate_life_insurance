package com.bbva.rbvd.lib.r302.pattern.product;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.rbvd.dto.lifeinsrc.commons.InsurancePlanDTO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;
import com.bbva.rbvd.lib.r044.RBVDR044;
import com.bbva.rbvd.lib.r301.RBVDR301;
import com.bbva.rbvd.lib.r302.business.IGifoleBusiness;
import com.bbva.rbvd.lib.r302.business.IInsrDynamicLifeBusiness;
import com.bbva.rbvd.lib.r302.business.impl.GifoleBusinessImpl;
import com.bbva.rbvd.lib.r302.business.impl.InsrVidaDinamicoBusinessImpl;
import com.bbva.rbvd.lib.r302.pattern.PostSimulation;
import com.bbva.rbvd.lib.r302.pattern.PreSimulation;
import com.bbva.rbvd.lib.r302.pattern.impl.SimulationDecorator;
import com.bbva.rbvd.lib.r302.transfer.PayloadConfig;
import com.bbva.rbvd.lib.r302.transfer.PayloadStore;
import com.bbva.rbvd.lib.r302.util.ConstantsUtil;
import com.bbva.rbvd.lib.r302.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SimulationVidaDinamico extends SimulationDecorator {
	private static final Logger LOGGER = LoggerFactory.getLogger(SimulationVidaDinamico.class);

	private RBVDR044 rbvdr044;

	public SimulationVidaDinamico(PreSimulation preSimulation, PostSimulation postSimulation, RBVDR044 rbvdr044) {
		super(preSimulation, postSimulation);
		this.rbvdr044 = rbvdr044;
	}

	@Override
	public LifeSimulationDTO start(LifeSimulationDTO input, RBVDR301 rbvdR301, ApplicationConfigurationService applicationConfigurationService) {
		LOGGER.info("***** SimulationVidaDinamico - start START | input {} *****",input);

		validatePlanWithRefundPercentage(input);

		PayloadConfig payloadConfig = this.getPreSimulation().getConfig(input);

		IInsrDynamicLifeBusiness seguroVidaDinamico = new InsrVidaDinamicoBusinessImpl(rbvdR301, applicationConfigurationService);

		String simulationId = input.getExternalSimulationId();
		//ejecucion servicio rimac
		PayloadStore payloadStore = seguroVidaDinamico.doDynamicLife(payloadConfig);
		LOGGER.info("***** SimulationVidaDinamico - start | payloadStore {} *****",payloadStore);

		if(ValidationUtil.isFirstCalled(simulationId)){
			this.getPostSimulation().end(payloadStore);
		}

		//Actualizacion tipo documento en salida trx
		payloadStore.getResponse().getHolder().getIdentityDocument().getDocumentType().setId(payloadConfig.getProperties().getDocumentTypeIdAsText());
		LOGGER.info("***** RBVDR302Impl - SimulationVidaDinamico.start()  ***** Response: {}", payloadStore.getResponse());


		//LLAMADA A GIFOLE!
		IGifoleBusiness iGifoleBusiness = new GifoleBusinessImpl(rbvdR301, applicationConfigurationService);
		iGifoleBusiness.callGifoleDynamicService(payloadStore.getResponse(), payloadConfig.getCustomerListASO(), this.rbvdr044);

		LOGGER.info("***** RBVDR302Impl - SimulationVidaDinamico.start() END *****");

		return payloadStore.getResponse();
	}

	private static void validatePlanWithRefundPercentage(LifeSimulationDTO input) {
		if(input.getListRefunds() != null && CollectionUtils.isEmpty(input.getProduct().getPlans())){
			BigDecimal percentage = input.getListRefunds().stream()
					.filter(refundsDTO -> refundsDTO.getUnit().getUnitType().equals(ConstantsUtil.REFUND_UNIT_PERCENTAGE))
					.map(refundsDTO -> refundsDTO.getUnit().getPercentage()).collect(Collectors.toList()).get(0);

			if(percentage.compareTo(BigDecimal.ZERO) == 0){
				InsurancePlanDTO plan01 = new InsurancePlanDTO();
				plan01.setId(ConstantsUtil.PLANUNO);
				List<InsurancePlanDTO> plans = new ArrayList<>();
				plans.add(plan01);
				input.getProduct().setPlans(plans);
			}else{
				InsurancePlanDTO plan02 = new InsurancePlanDTO();
				plan02.setId(ConstantsUtil.PLANDOS);
				List<InsurancePlanDTO> plans = new ArrayList<>();
				plans.add(plan02);
				input.getProduct().setPlans(plans);
			}
		}
	}


}
