package com.github.lebezout.urlshortener.rest;

import com.github.lebezout.urlshortener.error.CounterAlreadyExistsException;
import com.github.lebezout.urlshortener.error.CounterNotFoundException;
import com.github.lebezout.urlshortener.error.ErrorResponse;
import com.github.lebezout.urlshortener.error.IDAlreadyExistsException;
import com.github.lebezout.urlshortener.error.IDNotAcceptedException;
import com.github.lebezout.urlshortener.error.LinkNotFoundException;
import com.github.lebezout.urlshortener.error.NotAuthenticatedException;
import com.github.lebezout.urlshortener.error.NotLinkOwnerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;

/**
 * An exception handler to avoid the whitelabel error page and normalize error JSON responses.
 * @author lebezout@gmail.com
 */
@Slf4j
@RestControllerAdvice
public class ExceptionHandlerControllerAdvice {
    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public @ResponseBody ErrorResponse handleDefaultError(final Exception exception,
                                                          final HttpServletRequest request) {
        LOGGER.error("Unknown error occurred:", exception);
        return new ErrorResponse(ErrorResponse.ErrorType.SERVER, exception.getMessage(), request.getRequestURI());
    }
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public @ResponseBody ErrorResponse handleAuthenticationException(final Exception exception, final HttpServletRequest request) {
        LOGGER.error("AuthenticationException error occurred:", exception);
        return new ErrorResponse(ErrorResponse.ErrorType.CLIENT, exception.getMessage(), request.getRequestURI());
    }
    @ExceptionHandler(NotAuthenticatedException.class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public @ResponseBody ErrorResponse handleNotAuthenticatedException(final Exception exception,
                                                                       final HttpServletRequest request) {
        LOGGER.error("NotAuthenticatedException error occurred:", exception);
        return new ErrorResponse(ErrorResponse.ErrorType.CLIENT, exception.getMessage(), request.getRequestURI());
    }
    @ExceptionHandler(LinkNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public @ResponseBody ErrorResponse handleLinkNotFoundException(final Exception exception,
                                                                   final HttpServletRequest request) {
        LOGGER.error("LinkNotFoundException error occurred:", exception);
        return new ErrorResponse(ErrorResponse.ErrorType.CLIENT, "Expected link not found", request.getRequestURI());
    }
    @ExceptionHandler(CounterNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public @ResponseBody ErrorResponse handleCounterNotFoundException(final Exception exception,
                                                                      final HttpServletRequest request) {
        LOGGER.error("CounterNotFoundException error occurred:", exception);
        return new ErrorResponse(ErrorResponse.ErrorType.CLIENT, "Expected counter not found", request.getRequestURI());
    }
    @ExceptionHandler(CounterAlreadyExistsException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorResponse handleCounterAlreadyExistsException(final Exception exception,
                                                                           final HttpServletRequest request) {
        LOGGER.error("CounterAlreadyExistsException error occurred:", exception);
        return new ErrorResponse(ErrorResponse.ErrorType.CLIENT, "Counter already exists for this URL", request.getRequestURI());
    }
    @ExceptionHandler(IDAlreadyExistsException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorResponse handleIDAlreadyExistsException(final Exception exception,
                                                                      final HttpServletRequest request) {
        LOGGER.error("IDAlreadyExistsException error occurred:", exception);
        return new ErrorResponse(ErrorResponse.ErrorType.CLIENT, "The provided ID already exists", request.getRequestURI());
    }
    @ExceptionHandler(IDNotAcceptedException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorResponse handleIDNotAcceptedException(final Exception exception,
                                                                    final HttpServletRequest request) {
        LOGGER.error("IDNotAcceptedException error occurred:", exception);
        return new ErrorResponse(ErrorResponse.ErrorType.CLIENT, "The provided ID is rejected by our policy", request.getRequestURI());
    }
    @ExceptionHandler(NotLinkOwnerException.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public @ResponseBody ErrorResponse handleNotLinkOwnerException(final Exception exception,
                                                                   final HttpServletRequest request) {
        LOGGER.error("NotLinkOwnerException error occurred:", exception);
        return new ErrorResponse(ErrorResponse.ErrorType.CLIENT, "Only the creator of the link can update it", request.getRequestURI());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorResponse handleIllegalArgumentException(IllegalArgumentException exception, HttpServletRequest request) {
        LOGGER.error("IllegalArgumentException error occurred:", exception);
        return new ErrorResponse(ErrorResponse.ErrorType.CLIENT, exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorResponse handleMissingServletRequestParameterException(MissingServletRequestParameterException exception, HttpServletRequest request) {
        LOGGER.error("MissingServletRequestParameterException error occurred:", exception);
        return new ErrorResponse(ErrorResponse.ErrorType.CLIENT, exception.getMessage(), request.getRequestURI());
    }
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorResponse handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception, HttpServletRequest request) {
        LOGGER.error("MethodArgumentTypeMismatchException error occurred:", exception);
        return new ErrorResponse(ErrorResponse.ErrorType.CLIENT, exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorResponse handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException exception, HttpServletRequest request) {
        LOGGER.error("HttpRequestMethodNotSupportedException error occurred:", exception);
        return new ErrorResponse(ErrorResponse.ErrorType.CLIENT, exception.getMessage(), request.getRequestURI());
    }
}
