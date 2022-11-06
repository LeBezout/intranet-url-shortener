package com.github.lebezout.urlshortener.error;

import com.github.lebezout.urlshortener.domain.CounterEntity;

import java.util.Optional;

/**
 * A custom exception to map the 400 HTTP status if a counter already exists for the url specified.
 * @author lebezout@gmail.com
 */
public class CounterAlreadyExistsException extends RuntimeException {

    /**
     * A convenient method to check and throw if needed this exception
     * @param entity entity attempted to be absent
     * @throws CounterAlreadyExistsException if entity is present
     */
    public static void throwIfNeeded(final Optional<CounterEntity> entity) {
        if (entity.isPresent()) {
            throw new CounterAlreadyExistsException();
        }
    }
}
