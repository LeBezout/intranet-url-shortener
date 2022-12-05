package com.github.lebezout.urlshortener.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TargetUrlValidatorTest {

    @Test
    void test_is_accepted() {
        TargetUrlValidator validator = new TargetUrlValidator(new String[] { "bit.ly", "cutt.ly", "shorturl.at" });
        Assertions.assertAll(
            () -> Assertions.assertTrue(validator.accept("https://www.gitlab.com")),
            () -> Assertions.assertTrue(validator.accept("https://github.com")),
            () -> Assertions.assertTrue(validator.accept("https://stackoverflow.com"))
        );
    }

    @Test
    void test_is_not_accepted() {
        TargetUrlValidator validator = new TargetUrlValidator(new String[] { "bit.ly", "cutt.ly", "shorturl.at" });
        Assertions.assertAll(
            () -> Assertions.assertFalse(validator.accept(null)),
            () -> Assertions.assertFalse(validator.accept("")),
            () -> Assertions.assertFalse(validator.accept("google")),
            () -> Assertions.assertFalse(validator.accept("http://")),
            () -> Assertions.assertFalse(validator.accept("http://bit.ly/link")),
            () -> Assertions.assertFalse(validator.accept("https://cutt.ly/link")),
            () -> Assertions.assertFalse(validator.accept("https://shorturl.at/link"))
        );
    }
}
