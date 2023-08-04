package com.bbva.rbvd.lib.r302.transform.map;

import com.bbva.rbvd.dto.lifeinsrc.dao.SimulationProductDAO;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDProperties;

import java.util.HashMap;
import java.util.Map;

public class SimulationProductMap {

    private SimulationProductMap(){}

    public static Map<String, Object> createArgumentsForSaveSimulationProduct(SimulationProductDAO simulationProductDAO) {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put(RBVDProperties.FIELD_INSURANCE_SIMULATION_ID.getValue(), simulationProductDAO.getInsuranceSimulationId());
        arguments.put(RBVDProperties.FIELD_OR_FILTER_INSURANCE_PRODUCT_ID.getValue(), simulationProductDAO.getInsuranceProductId());
        arguments.put(RBVDProperties.FIELD_CAMPAIGN_FACTOR_TYPE.getValue(), simulationProductDAO.getCampaignFactorType());
        arguments.put(RBVDProperties.FIELD_CAMPAIGN_OFFER_1_AMOUNT.getValue(), simulationProductDAO.getCampaignOffer1Amount());
        arguments.put(RBVDProperties.FIELD_CAMPAIGN_FACTOR_PER.getValue(), simulationProductDAO.getCampaignFactorPer());
        arguments.put(RBVDProperties.FIELD_SALE_CHANNEL_ID.getValue(), simulationProductDAO.getSaleChannelId());
        arguments.put(RBVDProperties.FIELD_SOURCE_BRANCH_ID.getValue(), simulationProductDAO.getSourceBranchId());
        arguments.put(RBVDProperties.FIELD_CREATION_USER_ID.getValue(), simulationProductDAO.getCreationUser());
        arguments.put(RBVDProperties.FIELD_USER_AUDIT_ID.getValue(), simulationProductDAO.getUserAudit());

        return arguments;
    }

}
