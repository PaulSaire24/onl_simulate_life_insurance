package com.bbva.rbvd.lib.r301.impl.util;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.rbvd.dto.insuranceroyal.utils.InsuranceRoyalErrors;
import com.bbva.rbvd.dto.insuranceroyal.utils.InsuranceRoyalValidation;
import com.bbva.rbvd.dto.insuranceroyal.rimac.ErrorRimacBO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

public class RimacExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(RimacExceptionHandler.class);

    public void handler(RestClientException exception) {
        if(exception instanceof HttpClientErrorException) {
            LOGGER.info("RimacExceptionHandler - HttpClientErrorException");
            this.clientExceptionHandler((HttpClientErrorException) exception);
        } else {
            LOGGER.info("RimacExceptionHandler - HttpServerErrorException");
            throw InsuranceRoyalValidation.build(InsuranceRoyalErrors.SERVER_ERROR);
        }
    }

    private void clientExceptionHandler(HttpClientErrorException exception) {
        String responseBody = exception.getResponseBodyAsString();
        LOGGER.info("HttpClientErrorException - Response body: {}", responseBody);
        ErrorRimacBO rimacError = this.getErrorObject(responseBody);
        LOGGER.info("HttpClientErrorException - rimacError details: {}", rimacError.getError().getDetails());
        this.throwingBusinessException(rimacError);
    }

    private void throwingBusinessException(ErrorRimacBO rimacError) {
        BusinessException businessException = InsuranceRoyalValidation.build(InsuranceRoyalErrors.ERROR_FROM_RIMAC);

        StringBuilder details = new StringBuilder();
        for (String detail : rimacError.getError().getDetails()) {
            if (details.length() > 0) {
                details.append(" | ");
            }
            details.append(detail);
        }

        // E1 ERROR DE DATOS, E2 ERROR FUNCIONAL
        if(details.length() > 0){
            businessException.setAdviceCode("BBVAE1"+ "008411");
            businessException.setMessage(rimacError.getError().getMessage().concat(" : ").concat(details.toString()));
        }else{
            businessException.setAdviceCode("BBVAE2"+ "008411");
            businessException.setMessage(rimacError.getError().getMessage());
        }

        throw businessException;
    }


    private ErrorRimacBO getErrorObject(String responseBody) {
        return JsonHelper.getInstance().deserialization(responseBody, ErrorRimacBO.class);
    }

}
