package com.github.lebezout.urlshortener.error;

/**
 * A custom exception to map the 400 HTTP status when a provided ID is too long when creating a new link.
 * @author lebezout@gmail.com
 */
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
