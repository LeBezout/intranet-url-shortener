package com.github.lebezout.urlshortener.error;

/**
 * A custom exception to map the 403 HTTP status when a link is attempt to be updated by another person than the creator.
 * @author lebezout@gmail.com
 */
public class NotLinkOwnerException extends RuntimeException {

    /**
     * A convenient method to check and throw if needed this exception
     * @param creator creator of the link
     * @param updater updater of the link
     * @throws NotLinkOwnerException if creator not equals updater
     */
    public static void throwIfNeeded(String creator, String updater) {
        if (!creator.equals(updater)) {
            throw new NotLinkOwnerException();
        }
    }
}
