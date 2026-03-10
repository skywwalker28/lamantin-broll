package com.wsky.lomantin.exception;

import com.wsky.lomantin.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpClientErrorException.Unauthorized.class)
    public ErrorResponse handlerUnauthorized(HttpClientErrorException.Unauthorized ex) {
        return new ErrorResponse(
            "PEXELS_UNAUTHORIZED",
            "Invalid Api key for Pexels API",
            HttpStatus.UNAUTHORIZED.value()
        );
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ErrorResponse handlerHttpClientError(HttpClientErrorException ex) {
        return new ErrorResponse(
                "PEXELS_API_RESPONSE",
                ex.getMessage(),
                ex.getStatusCode().value()
        );
    }

    @ExceptionHandler(Exception.class)
    public ErrorResponse handlerGeneric(Exception ex) {
        return new ErrorResponse(
                "INTERNAL_ERROR",
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
    }
}
