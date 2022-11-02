package com.github.lebezout.urlshortener.rest;

import com.github.lebezout.urlshortener.error.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * A default exception handler to avoid the whitelabel error page.
 * @author lebezout@gmail.com
 */
@Slf4j
@RestControllerAdvice
public class ExceptionHandlerControllerAdvice {
    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public @ResponseBody ErrorResponse handleDefaultError(final Exception exception,
                                                          final HttpServletRequest request) {
        LOGGER.error("Error occurred : ", exception);
        return new ErrorResponse(exception.getMessage(), request.getRequestURI());
    }
}
