package com.github.lebezout.urlshortener.rest;

import com.github.lebezout.urlshortener.error.CounterAlreadyExistsException;
import com.github.lebezout.urlshortener.error.CounterNotFoundException;
import com.github.lebezout.urlshortener.error.ErrorResponse;
import com.github.lebezout.urlshortener.error.IDAlreadyExistsException;
import com.github.lebezout.urlshortener.error.IDTooLongException;
import com.github.lebezout.urlshortener.error.LinkNotFoundException;
import com.github.lebezout.urlshortener.error.NotLinkOwnerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
        LOGGER.error("Error occurred : ", exception);
        return new ErrorResponse(ErrorResponse.ErrorType.SERVER, exception.getMessage(), request.getRequestURI());
    }
    @ExceptionHandler(LinkNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Expected link not found")
    public @ResponseBody ErrorResponse handleLinkNotFoundException(final Exception exception,
                                                                   final HttpServletRequest request) {
        LOGGER.error("LinkNotFoundException error occurred : ", exception);
        return new ErrorResponse(ErrorResponse.ErrorType.CLIENT, exception.getMessage(), request.getRequestURI());
    }
    @ExceptionHandler(CounterNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Expected counter not found")
    public @ResponseBody ErrorResponse handleCounterNotFoundException(final Exception exception,
                                                                      final HttpServletRequest request) {
        LOGGER.error("CounterNotFoundException error occurred : ", exception);
        return new ErrorResponse(ErrorResponse.ErrorType.CLIENT, exception.getMessage(), request.getRequestURI());
    }
    @ExceptionHandler(CounterAlreadyExistsException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Counter already exists for this URL")
    public @ResponseBody ErrorResponse handleCounterAlreadyExistsException(final Exception exception,
                                                                           final HttpServletRequest request) {
        LOGGER.error("CounterAlreadyExistsException error occurred : ", exception);
        return new ErrorResponse(ErrorResponse.ErrorType.CLIENT, exception.getMessage(), request.getRequestURI());
    }
    @ExceptionHandler(IDAlreadyExistsException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "The provided ID already exists")
    public @ResponseBody ErrorResponse handleIDAlreadyExistsException(final Exception exception,
                                                                      final HttpServletRequest request) {
        LOGGER.error("IDAlreadyExistsException error occurred : ", exception);
        return new ErrorResponse(ErrorResponse.ErrorType.CLIENT, exception.getMessage(), request.getRequestURI());
    }
    @ExceptionHandler(IDTooLongException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "The provided ID is too long (must be lower than " + IDTooLongException.ID_MAX_LENGTH + " characters)")
    public @ResponseBody ErrorResponse handleIDTooLongException(final Exception exception,
                                                                final HttpServletRequest request) {
        LOGGER.error("IDTooLongException error occurred : ", exception);
        return new ErrorResponse(ErrorResponse.ErrorType.CLIENT, exception.getMessage(), request.getRequestURI());
    }
    @ExceptionHandler(NotLinkOwnerException.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Only the creator of the link can update it")
    public @ResponseBody ErrorResponse handleNotLinkOwnerException(final Exception exception,
                                                                   final HttpServletRequest request) {
        LOGGER.error("NotLinkOwnerException error occurred : ", exception);
        return new ErrorResponse(ErrorResponse.ErrorType.CLIENT, exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorResponse handleMissingServletRequestParameterException(MissingServletRequestParameterException exception, HttpServletRequest request) {
        LOGGER.error("MissingServletRequestParameterException error occurred : ", exception);
        return new ErrorResponse(ErrorResponse.ErrorType.CLIENT, exception.getMessage(), request.getRequestURI());
    }
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorResponse handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception, HttpServletRequest request) {
        LOGGER.error("MethodArgumentTypeMismatchException error occurred : ", exception);
        return new ErrorResponse(ErrorResponse.ErrorType.CLIENT, exception.getMessage(), request.getRequestURI());
    }
}
