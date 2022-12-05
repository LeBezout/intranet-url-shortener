package com.github.lebezout.urlshortener.utils;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * Validator for the provided target URL for the shortcuts.
 */
@RequiredArgsConstructor
public class TargetUrlValidator {
    private final String[] forbiddenTargetUrlKeywords;

    /**
     * Validate the provided shortcut target URL
     * @param url the url to validate
     * @return true if the url is accepted
     */
    public boolean accept(String url) {
        return url != null && url.length() > 7 && Arrays.stream(forbiddenTargetUrlKeywords).noneMatch(url::contains);
    }
}
