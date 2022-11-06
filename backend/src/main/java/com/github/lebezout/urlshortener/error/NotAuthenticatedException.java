package com.github.lebezout.urlshortener.error;

/**
 * A custom exception to map the 401 HTTP status when an authentication is mandatory.
 * @author lebezout@gmail.com
 */
public class NotAuthenticatedException extends RuntimeException {
    public NotAuthenticatedException() {
        super("No credentials provided");
    }
}
