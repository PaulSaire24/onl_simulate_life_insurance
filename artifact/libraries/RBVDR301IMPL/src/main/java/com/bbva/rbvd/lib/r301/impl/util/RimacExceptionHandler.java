package com.bbva.rbvd.lib.r301.impl.util;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.rbvd.dto.lifeinsrc.bo.ErrorRimacBO;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDErrors;
import com.bbva.rbvd.dto.lifeinsrc.utils.RBVDValidation;
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
            throw RBVDValidation.build(RBVDErrors.SERVER_ERROR);
        }
    }

    private void clientExceptionHandler(HttpClientErrorException exception) {
        String responseBody = exception.getResponseBodyAsString();
        LOGGER.info("HttpClientErrorException - Response body: {}", responseBody);
        ErrorRimacBO rimacError = this.getErrorObject(responseBody);
        this.throwingBusinessException(rimacError);
    }

    private void throwingBusinessException(ErrorRimacBO rimacError) {
        BusinessException businessException = RBVDValidation.build(RBVDErrors.SERVICE_CONNECT_DOCUMENT_PROCESSES);
        businessException.setMessage(rimacError.getError().getHttpStatus() + " - " + rimacError.getError().getMessage());
        throw businessException;
    }

    private ErrorRimacBO getErrorObject(String responseBody) {
        return JsonHelper.getInstance().deserialization(responseBody, ErrorRimacBO.class);
    }

}
