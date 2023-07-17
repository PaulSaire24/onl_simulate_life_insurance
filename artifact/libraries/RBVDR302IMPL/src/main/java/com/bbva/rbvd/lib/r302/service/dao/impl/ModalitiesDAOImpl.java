package com.bbva.rbvd.lib.r302.service.dao.impl;

import com.bbva.pisd.lib.r350.PISDR350;
import com.bbva.rbvd.dto.lifeinsrc.dao.InsuranceProductModalityDAO;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDErrors;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDProperties;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDValidation;
import com.bbva.rbvd.lib.r302.service.dao.IModalitiesDAO;
import com.bbva.rbvd.lib.r302.transform.map.ProductMap;
import com.bbva.rbvd.lib.r302.util.ValidationUtil;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.springframework.util.CollectionUtils.isEmpty;

public class ModalitiesDAOImpl implements IModalitiesDAO {

    private PISDR350 pisdr350;
    public ModalitiesDAOImpl(PISDR350 pisdr350){ this.pisdr350 = pisdr350;}
    @Override
    public List<InsuranceProductModalityDAO> getModalitiesInfo(String plansPT, BigDecimal insuranceProductId, String saleChannel) {


        Map<String, Object> responseQueryModalitiesInformation =
                this.pisdr350.executeGetListASingleRow(RBVDProperties.QUERY_GET_PRODUCT_MODALITIES_INFORMATION.getValue(),
                        ProductMap.createModalitiesInformationFilters(plansPT, insuranceProductId, saleChannel));

        if(isEmpty(responseQueryModalitiesInformation)) {
            throw RBVDValidation.build(RBVDErrors.WRONG_PLAN_CODES);
        }


        List<InsuranceProductModalityDAO>  listInsuranceProductModalityDAO = ValidationUtil.
                validateQueryInsuranceProductModality(responseQueryModalitiesInformation);

        return listInsuranceProductModalityDAO;
    }
}
