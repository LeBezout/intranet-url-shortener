package com.github.lebezout.urlshortener.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * A custom exception to map the 404 HTTP status when a link is not found.
 * @author lebezout@gmail.com
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Expected link not found")
public class LinkNotFoundException extends RuntimeException {
}
