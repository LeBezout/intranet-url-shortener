package com.github.lebezout.urlshortener.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Optional;

/**
 * A custom exception to map the 400 HTTP status when a provided ID already exists when creating a new link.
 * @author lebezout@gmail.com
 */
@ResponseStatus(value= HttpStatus.BAD_REQUEST, reason="The provided ID already exists")  // 400
public class IDAlreadyExistsException extends RuntimeException {

    /**
     * A convenient method to check and throw if needed this exception
     * @param entity entity attempted to be absent
     * @throws IDAlreadyExistsException if entity is present
     */
    public static void throwIfNeeded(final Optional<?> entity) {
        if (entity.isPresent()) {
            throw new IDAlreadyExistsException();
        }
    }
}
