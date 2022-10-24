package com.github.lebezout.urlshortener.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * A custom exception to map the 400 HTTP status when a provided ID is too long when creating a new link.
 * @author lebezout@gmail.com
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "The provided ID is too long (must be lower than " + IDTooLongException.ID_MAX_LENGTH + " characters)")  // 400
public class IDTooLongException extends RuntimeException {
    /** the maximum length allowed to store the id in the database */
    public static final int ID_MAX_LENGTH = 15;

    /**
     * A convenient method to check and throw if needed this exception
     * @param providedID ID
     * @throws IDTooLongException if the provided ID's length is greater than ID_MAX_LENGTH
     */
    public static void throwIfNeeded(final String providedID) {
        if (providedID.length() > ID_MAX_LENGTH) {
            throw new IDTooLongException();
        }
    }
}
