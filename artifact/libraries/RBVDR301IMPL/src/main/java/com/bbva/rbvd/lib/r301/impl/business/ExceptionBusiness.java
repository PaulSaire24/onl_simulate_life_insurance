package com.bbva.rbvd.lib.r301.impl.business;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.rbvd.dto.insuranceroyal.rimac.ErrorRimacBO;
import com.bbva.rbvd.dto.insuranceroyal.utils.InsuranceRoyalErrors;
import com.bbva.rbvd.dto.insuranceroyal.utils.InsuranceRoyalValidation;
import com.bbva.rbvd.lib.r301.impl.util.Constans;
import com.bbva.rbvd.lib.r301.impl.util.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.nio.charset.StandardCharsets;

public class ExceptionBusiness {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionBusiness.class);

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
        setBusinessException(rimacError, businessException);
        throw businessException;
    }

    private static void setBusinessException(ErrorRimacBO rimacError, BusinessException businessException) {
        StringBuilder details = new StringBuilder();
        if (rimacError.getError().getDetails() != null && !rimacError.getError().getDetails().isEmpty()) {
            for (String detail : rimacError.getError().getDetails()) {
                if (details.length() > 0) {
                    details.append(" | ");
                }
                details.append(decodeMessage(detail));
            }
        }

        // E1:ERROR DE DATOS, E2:ERROR FUNCIONAL
        if(details.length() > 0){
            businessException.setAdviceCode(Constans.Error.BBVAE1 + Constans.Error.COD_008411);
            businessException.setMessage(rimacError.getError().getMessage().concat(" : ").concat(details.toString()));
        }else{
            businessException.setAdviceCode(Constans.Error.BBVAE2 + Constans.Error.COD_008411);
            businessException.setMessage(rimacError.getError().getMessage());
        }



    }

    public static String decodeMessage(String message) {
        byte[] bytes = message.getBytes(StandardCharsets.ISO_8859_1);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private ErrorRimacBO getErrorObject(String responseBody) {
        return JsonHelper.getInstance().deserialization(responseBody, ErrorRimacBO.class);
    }





}
