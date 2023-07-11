package com.bbva.rbvd.lib.r301;

import com.bbva.pisd.dto.insurance.aso.CustomerListASO;

import com.bbva.pisd.dto.insurance.aso.crypto.CryptoASO;
import com.bbva.pisd.dto.insurance.aso.gifole.GifoleInsuranceRequestASO;
import com.bbva.pisd.dto.insurance.aso.tier.TierASO;

import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.InsuranceLifeSimulationBO;

public interface RBVDR301 {


	InsuranceLifeSimulationBO executeSimulationRimacService(final InsuranceLifeSimulationBO payload, String traceId);

	InsuranceLifeSimulationBO executeSimulationModificationRimacService(InsuranceLifeSimulationBO payload, String quotationId, String traceId);

	CustomerListASO executeCallListCustomerResponse(String customerId);

	Integer executeGifolelifeService(GifoleInsuranceRequestASO requestBody);

	CryptoASO executeCryptoService(CryptoASO input);

	TierASO executeGetTierService(String holderId);

}
