package com.github.lebezout.urlshortener.utils;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * A simple validator for the provided ids
 */
@RequiredArgsConstructor
public class IdValidator {
    private final String[] forbiddenIds;
    private final int minLength;
    private final int maxLength;

    /**
     * Validate the provided id
     * @param id id
     * @return true if accepted
     */
    public boolean isValid(String id) {
        return id != null && id.length() >= minLength && id.length() <= maxLength && !isForbidden(id);
    }

    private boolean isForbidden(String value) {
        return Arrays.stream(forbiddenIds).anyMatch(e -> e.equalsIgnoreCase(value));
    }
}
