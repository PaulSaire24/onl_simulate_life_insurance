package com.bbva.rbvd.lib.r302.service.dao.impl;

import com.bbva.pisd.dto.insurance.utils.PISDProperties;
import com.bbva.pisd.lib.r350.PISDR350;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDErrors;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDProperties;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDValidation;
import com.bbva.rbvd.lib.r302.service.dao.IContractDAO;
import com.bbva.rbvd.lib.r302.transform.map.ContractMap;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static org.springframework.util.CollectionUtils.isEmpty;

public class ContractDAOImpl implements IContractDAO {

    private PISDR350 pisdR350;
    private ProductDAOImpl productDAO;

    public ContractDAOImpl(PISDR350 pisdR350) {
        this.pisdR350 = pisdR350;
    }

    public BigDecimal getInsuranceAmountDAO(BigDecimal insuranceProductId,String customerId) {

        Map<String, Object> responseQueryGetCumulus =
                this.pisdR350.executeGetListASingleRow(RBVDProperties.QUERY_GET_INSURANCE_AMOUNT.getValue(), ContractMap.mapInsuranceAmount(
                        insuranceProductId, customerId));

        if (isEmpty(responseQueryGetCumulus)) {
            throw RBVDValidation.build(RBVDErrors.WRONG_PRODUCT_CODE);
        }

        BigDecimal sumCumulus = this.getCumuls(responseQueryGetCumulus);
        return sumCumulus;
    }

    //valida la cantidad asegurada
    private BigDecimal getCumuls(Map<String, Object> responseQueryGetCumulus){

        List<Map<String, Object>> rows = (List<Map<String, Object>>) responseQueryGetCumulus.get(PISDProperties.KEY_OF_INSRC_LIST_RESPONSES.getValue());
        BigDecimal sum = BigDecimal.ZERO;
        if(!isEmpty(rows) && rows.size()!=0) {
            List<BigDecimal> listCumulus = rows.stream().map(this::createListCumulus).collect(toList());
            for (BigDecimal amt : listCumulus) {
                sum = sum.add(amt);
            }
        }
        return sum;
    }

    //crea la lista de cúmulos
    private BigDecimal createListCumulus(Map < String, Object > mapElement){
        return (BigDecimal) mapElement.get(PISDProperties.FIELD_INSURED_AMOUNT.getValue());
    }


}
