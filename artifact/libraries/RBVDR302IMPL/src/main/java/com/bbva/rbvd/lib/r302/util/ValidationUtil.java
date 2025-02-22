package com.bbva.rbvd.lib.r302.util;

import com.bbva.pisd.dto.insurance.aso.gifole.DocumentTypeASO;
import com.bbva.pisd.dto.insurance.aso.gifole.HolderASO;
import com.bbva.pisd.dto.insurance.aso.gifole.IdentityDocumentASO;
import com.bbva.pisd.dto.insurance.bo.IdentityDocumentsBO;
import com.bbva.rbvd.dto.connectionapi.aso.common.GenericTypeASO;
import com.bbva.rbvd.dto.lifeinsrc.dao.InsuranceProductModalityDAO;
import com.bbva.rbvd.dto.lifeinsrc.simulation.LifeSimulationDTO;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDErrors;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDProperties;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDValidation;
import com.bbva.rbvd.lib.r302.transfer.PayloadConfig;
import com.bbva.rbvd.lib.r302.transform.bean.InsuranceProductModalityBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Arrays;

import static java.util.stream.Collectors.toList;
import static org.springframework.util.CollectionUtils.isEmpty;

public class ValidationUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationUtil.class);

    public static List<InsuranceProductModalityDAO> validateQueryInsuranceProductModality(Map<String, Object> responseQueryInsuranceProductModality) {
        List<Map<String, Object>> rows =
                (List<Map<String, Object>>) responseQueryInsuranceProductModality.get(RBVDProperties.KEY_OF_INSRC_LIST_RESPONSES.getValue());
        if (isEmpty(rows)) {
            throw RBVDValidation.build(RBVDErrors.WRONG_PLAN_CODES);
        }
        return rows.stream().map(InsuranceProductModalityBean::createInsuranceProductModalityDAO).collect(toList());
    }

    public static void validateParticipant(LifeSimulationDTO input, PayloadConfig payloadConfig){

        if(!CollectionUtils.isEmpty(input.getParticipants())){
            LOGGER.info("***** SimulationParameter: participan is not null *****");
            payloadConfig.setParticipant(true);
        }
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


    public void docValidationForGifoleDynamic(String documentNumber, String documentType, com.bbva.rbvd.dto.connectionapi.aso.common.HolderASO holder, LifeSimulationDTO response){
        com.bbva.rbvd.dto.connectionapi.aso.common.IdentityDocumentASO identityDocument = new com.bbva.rbvd.dto.connectionapi.aso.common.IdentityDocumentASO();
        GenericTypeASO documentTypeAso = new GenericTypeASO();
        documentTypeAso.setId(documentType);
        identityDocument.setDocumentType(documentTypeAso);
        identityDocument.setDocumentNumber(documentNumber);

        if (Objects.nonNull(response.getHolder()) && Objects.nonNull(response.getHolder().getIdentityDocument())) {
            identityDocument.setDocumentNumber(response.getHolder().getIdentityDocument().getDocumentNumber());
            identityDocument.getDocumentType().setId(response.getHolder().getIdentityDocument().getDocumentType().getId());
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

    public static boolean isFirstCalled(String externalSimulationId) {
        return Objects.isNull(externalSimulationId);
    }


}
