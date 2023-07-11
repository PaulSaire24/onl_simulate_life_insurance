package com.bbva.rbvd.lib.r302.business.util;

import com.bbva.pisd.dto.insurance.aso.crypto.CryptoASO;
import com.bbva.pisd.dto.insurance.aso.tier.TierASO;
import com.bbva.pisd.dto.insurance.utils.PISDProperties;
import com.bbva.rbvd.dto.lifeinsrc.dao.InsuranceProductModalityDAO;
import com.bbva.rbvd.dto.lifeinsrc.dao.ProductInformationDAO;
import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.InsuranceLifeSimulationBO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDErrors;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDProperties;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDValidation;
import com.bbva.rbvd.lib.r301.RBVDR301;
import com.bbva.rbvd.lib.r302.transform.bean.InsuranceProductModalityBean;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.toList;
import static org.springframework.util.CollectionUtils.isEmpty;

public class ValidationUtil {

    private static final String RIMAC_PRODUCT_NAME = "PRODUCT_SHORT_DESC";

    private RBVDR301 rbvdR301;

    public ValidationUtil(RBVDR301 rbvdR301) {
        this.rbvdR301 = rbvdR301;
    }

    //valida la cantidad asegurada
    public BigDecimal validateQueryGetInsuranceAmount(Map<String, Object> responseQueryGetCumulus){

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

    //realiza una validación
    public void validation(InsuranceLifeSimulationBO responseRimac){
        if(Objects.isNull(responseRimac)){
            throw RBVDValidation.build(RBVDErrors.ERROR_FROM_RIMAC);
        }
    }

    //Valida la información del producto
    public ProductInformationDAO validateQueryGetProductInformation(Map<String, Object> responseQueryGetProductInformation) {
        if(isEmpty(responseQueryGetProductInformation)) {
            throw RBVDValidation.build(RBVDErrors.WRONG_PRODUCT_CODE);
        }
        ProductInformationDAO productInformationDAO = new ProductInformationDAO();
        productInformationDAO.setInsuranceProductId((BigDecimal) responseQueryGetProductInformation.get(RBVDProperties.FIELD_OR_FILTER_INSURANCE_PRODUCT_ID.getValue()));
        productInformationDAO.setInsuranceProductDescription((String) responseQueryGetProductInformation.get(RBVDProperties.FIELD_INSURANCE_PRODUCT_DESC.getValue()));
        productInformationDAO.setInsuranceBusinessName((String) responseQueryGetProductInformation.get(RIMAC_PRODUCT_NAME));
        return productInformationDAO;
    }

    //valida la modalidad del producto asegurado
    public List<InsuranceProductModalityDAO> validateQueryInsuranceProductModality(Map<String, Object> responseQueryInsuranceProductModality) {
        List<Map<String, Object>> rows = (List<Map<String, Object>>) responseQueryInsuranceProductModality.get(RBVDProperties.KEY_OF_INSRC_LIST_RESPONSES.getValue());
        if (isEmpty(rows)) {
            throw RBVDValidation.build(RBVDErrors.WRONG_PLAN_CODES);
        }
        return rows.stream().map(productModality -> InsuranceProductModalityBean.createInsuranceProductModalityDAO(productModality)).collect(toList());
    }

    //valida la inserción de filas
    public void validateInsertion(int insertedRows, RBVDErrors error) {
        //LOGGER.info("***** VALOR inSERrow {} ", insertedRows);
        if(insertedRows != 1) {
            throw RBVDValidation.build(error);
        }
    }

    public TierASO validateTier (LifeSimulationDTO input){
        //LOGGER.info("***** RBVDR302Impl - validateTier START *****");
        TierASO responseTierASO = null;
        if (Objects.isNull(input.getTier())) {
            //LOGGER.info("Invoking Service ASO Tier");
            CryptoASO crypto = rbvdR301.executeCryptoService(new CryptoASO(input.getHolder().getId()));
            responseTierASO = rbvdR301.executeGetTierService(crypto.getData().getDocument());
        }
        //LOGGER.info("***** RBVDR302Impl - validateTier ***** Response: {}", responseTierASO);
        //LOGGER.info("***** RBVDR302Impl - validateTier END *****");
        return responseTierASO;
    }

}
