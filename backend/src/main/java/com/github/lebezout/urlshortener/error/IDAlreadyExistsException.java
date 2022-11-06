package com.github.lebezout.urlshortener.error;

import com.github.lebezout.urlshortener.domain.LinkEntity;

import java.util.Optional;

/**
 * A custom exception to map the 400 HTTP status when a provided ID already exists when creating a new link.
 * @author lebezout@gmail.com
 */
public class IDAlreadyExistsException extends RuntimeException {

    /**
     * A convenient method to check and throw if needed this exception
     * @param entity entity attempted to be absent
     * @throws IDAlreadyExistsException if entity is present
     */
    public static void throwIfNeeded(final Optional<LinkEntity> entity) {
        if (entity.isPresent()) {
            throw new IDAlreadyExistsException();
        }
    }
}
