
package com.bbva.rbvd.lib.r301.util;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.rbvd.lib.r301.impl.business.ExceptionBusiness;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.nio.charset.StandardCharsets;

public class RimacExceptionHandlerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(RimacExceptionHandlerTest.class);

    private final ExceptionBusiness rimacExceptionHandler = new ExceptionBusiness();

    @Test(expected = BusinessException.class)
    public void handler_HttpClientErrorException() {
        LOGGER.info("RimacExceptionHandlerTest - Executing handler_HttpClientErrorException");
        String responseBody = "{\"error\":{\"code\":\"VIDA001\",\"message\":\"ErroralValidarDatos.\",\"details\":[\"Elcúmulodelclientehasuperadoellímitemáximoparasucotización.\",\"Elcumulodelclienteerroneo\"],\"httpStatus\":400}}";
        HttpClientErrorException clientErrorException = new HttpClientErrorException(HttpStatus.BAD_REQUEST, "", responseBody.getBytes(), StandardCharsets.UTF_8);
        this.rimacExceptionHandler.handler(clientErrorException);
    }

    @Test(expected = BusinessException.class)
    public void handler_HttpClientErrorExceptionElse() {
        LOGGER.info("RimacExceptionHandlerTest - Executing handler_HttpClientErrorException");
        String responseBody = "{\"error\":{\"code\":\"VIDA001\",\"message\":\"Elplazo10solopermitelossiguientesporcentajesdedevolución:75,100\",\"httpStatus\":400}}";
        HttpClientErrorException clientErrorException = new HttpClientErrorException(HttpStatus.BAD_REQUEST, "", responseBody.getBytes(), StandardCharsets.UTF_8);
        this.rimacExceptionHandler.handler(clientErrorException);
    }


    @Test(expected = BusinessException.class)
    public void handler_HttpServerErrorException() {
        LOGGER.info("RimacExceptionHandlerTest - Executing handler_HttpServerErrorException");
        HttpServerErrorException serverErrorException = new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "");
        this.rimacExceptionHandler.handler(serverErrorException);
    }
}
