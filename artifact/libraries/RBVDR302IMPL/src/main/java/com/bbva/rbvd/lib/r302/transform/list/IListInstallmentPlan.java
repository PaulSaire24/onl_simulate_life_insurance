package com.bbva.rbvd.lib.r302.transform.list;

import com.bbva.rbvd.dto.lifeinsrc.commons.InsurancePlanDTO;
import com.bbva.rbvd.dto.lifeinsrc.dao.InsuranceProductModalityDAO;
import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.InsuranceLifeSimulationBO;

import java.util.List;

public interface IListInstallmentPlan {

    List<InsurancePlanDTO> getPlansNamesAndRecommendedValuesAndInstallmentsPlans(List<InsuranceProductModalityDAO> productModalities, InsuranceLifeSimulationBO responseRimac,List<Boolean> segmentPlans);

}
