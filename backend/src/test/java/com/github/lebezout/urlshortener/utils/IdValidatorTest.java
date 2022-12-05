package com.github.lebezout.urlshortener.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class IdValidatorTest {

    @Test
    void test_not_isValid() {
        IdValidator validator = new IdValidator(new String[] { "admin", "foo", "bar" }, 3, 8);
        Assertions.assertAll(
            () -> Assertions.assertFalse(validator.accept(null)),
            () -> Assertions.assertFalse(validator.accept("")),
            () -> Assertions.assertFalse(validator.accept("qw")), // < minLength
            () -> Assertions.assertFalse(validator.accept("123456789")), // > maxLength
            () -> Assertions.assertFalse(validator.accept("admin")),
            () -> Assertions.assertFalse(validator.accept("foo")),
            () -> Assertions.assertFalse(validator.accept("bar"))
        );
    }

    @Test
    void test_isValid() {
        IdValidator validator = new IdValidator(new String[] { "admin", "foo", "bar" }, 3, 5);
        Assertions.assertAll(
            () -> Assertions.assertTrue(validator.accept("123")),
            () -> Assertions.assertTrue(validator.accept("1234")),
            () -> Assertions.assertTrue(validator.accept("12345")),
            () -> Assertions.assertTrue(validator.accept("myid"))
        );
    }
}
