package com.bbva.rbvd.lib.r302.util;

import com.bbva.pisd.dto.insurance.aso.crypto.CryptoASO;
import com.bbva.pisd.dto.insurance.aso.gifole.DocumentTypeASO;
import com.bbva.pisd.dto.insurance.aso.gifole.HolderASO;
import com.bbva.pisd.dto.insurance.aso.gifole.IdentityDocumentASO;
import com.bbva.pisd.dto.insurance.aso.tier.TierASO;
import com.bbva.pisd.dto.insurance.bo.IdentityDocumentsBO;
import com.bbva.pisd.dto.insurance.utils.PISDProperties;
import com.bbva.rbvd.dto.lifeinsrc.dao.InsuranceProductModalityDAO;
import com.bbva.rbvd.dto.lifeinsrc.dao.ProductInformationDAO;
import com.bbva.rbvd.dto.lifeinsrc.rimac.simulation.InsuranceLifeSimulationBO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDErrors;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDProperties;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDValidation;
import com.bbva.rbvd.lib.r301.RBVDR301;
import com.bbva.rbvd.lib.r302.service.api.ConsumerInternalService;
import com.bbva.rbvd.lib.r302.transform.bean.InsuranceProductModalityBean;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.toList;
import static org.springframework.util.CollectionUtils.isEmpty;

public class ValidationUtil {

    private static final String RIMAC_PRODUCT_NAME = "PRODUCT_SHORT_DESC";

    private RBVDR301 rbvdR301;

    private ConsumerInternalService consumerInternalService;



    public ValidationUtil(RBVDR301 rbvdR301) {
        this.rbvdR301 = rbvdR301;
        this.consumerInternalService = new ConsumerInternalService(rbvdR301);

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
    public static List<InsuranceProductModalityDAO> validateQueryInsuranceProductModality(Map<String, Object> responseQueryInsuranceProductModality) {
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
            CryptoASO crypto = consumerInternalService.callCryptoService(input.getHolder().getId());
            responseTierASO = consumerInternalService.callGetTierService(crypto.getData().getDocument());
        }
        //LOGGER.info("***** RBVDR302Impl - validateTier ***** Response: {}", responseTierASO);
        //LOGGER.info("***** RBVDR302Impl - validateTier END *****");
        return responseTierASO;
    }

    public String validateSN(String name) {
        if(Objects.isNull(name) || "null".equals(name) || " ".equals(name)){
            return "N/A";
        }else{
            name = name.replace("#","Ñ");
            return name;
        }
    }

    public void docValidationForGifole(IdentityDocumentsBO customerInfo, HolderASO holder, LifeSimulationDTO response){
        IdentityDocumentASO identityDocument = new IdentityDocumentASO();
        DocumentTypeASO documentType = new DocumentTypeASO();
        String docNumber = customerInfo.getDocumentNumber();
        documentType.setId(customerInfo.getDocumentType().getId());
        identityDocument.setDocumentType(documentType);

        identityDocument.setDocumentNumber(response.getHolder().getIdentityDocument().getDocumentNumber());
        if (Objects.isNull(response.getHolder().getIdentityDocument().getDocumentNumber())) {
            identityDocument.setDocumentNumber(docNumber);
        } else {
            identityDocument.setDocumentNumber(
                    response.getHolder().getIdentityDocument().getDocumentNumber());
        }
        holder.setIdentityDocument(identityDocument);
    }

    public Boolean selectValuePlansDescription(String segmentoPlan, LifeSimulationDTO input){
        boolean valuePlus= false;
        String[] lifeArray = segmentoPlan.split(",");
        List<String> listSegment = Arrays.stream(lifeArray).collect(toList());
        String valueRetail = null;
        valueRetail = listSegment.stream().filter(retail -> retail.equals(input.getId())).findFirst().orElse(null);
        if(null!=valueRetail){
            valuePlus=true;
        }
        return valuePlus;
    }



}
