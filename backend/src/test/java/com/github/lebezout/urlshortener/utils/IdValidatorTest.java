package com.github.lebezout.urlshortener.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class IdValidatorTest {

    @Test
    void test_not_isValid() {
        IdValidator validator = new IdValidator(new String[] { "admin", "foo", "bar" }, 3, 8);
        Assertions.assertAll(
            () -> Assertions.assertFalse(validator.isValid(null)),
            () -> Assertions.assertFalse(validator.isValid("")),
            () -> Assertions.assertFalse(validator.isValid("qw")), // < minLength
            () -> Assertions.assertFalse(validator.isValid("123456789")), // > maxLength
            () -> Assertions.assertFalse(validator.isValid("admin")),
            () -> Assertions.assertFalse(validator.isValid("foo")),
            () -> Assertions.assertFalse(validator.isValid("bar"))
        );
    }

    @Test
    void test_isValid() {
        IdValidator validator = new IdValidator(new String[] { "admin", "foo", "bar" }, 3, 5);
        Assertions.assertAll(
            () -> Assertions.assertTrue(validator.isValid("123")),
            () -> Assertions.assertTrue(validator.isValid("1234")),
            () -> Assertions.assertTrue(validator.isValid("12345")),
            () -> Assertions.assertTrue(validator.isValid("myid"))
        );
    }
}
