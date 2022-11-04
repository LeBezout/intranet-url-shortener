package com.github.lebezout.urlshortener.error;

import com.github.lebezout.urlshortener.domain.CounterEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Optional;

/**
 * A custom exception to map the 400 HTTP status if a counter already exists for the url specified.
 * @author lebezout@gmail.com
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Counter already exists for this URL")  // 400
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
