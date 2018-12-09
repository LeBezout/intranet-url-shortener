package com.github.lebezout.urlshortener.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * A custom exception to map the 403 HTTP status when a link is attempt to be updated by another person than the creator.
 * @author lebezout@gmail.com
 */
@ResponseStatus(value= HttpStatus.FORBIDDEN, reason="Only the creator of the link can update it")
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
