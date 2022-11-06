package com.github.lebezout.urlshortener.rest;

import lombok.experimental.UtilityClass;
import org.junit.jupiter.api.Assertions;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.util.stream.Stream;

/**
 * Utility class for testing the Rest controllers.
 */
@UtilityClass
public class ResourceTestUtils {
    public static void assertEmptyResponseBody(MockHttpServletResponse httpResponse) throws IOException {
        String content = httpResponse.getContentAsString();
        Assertions.assertTrue(content.isEmpty());
    }
    public static void assertValidTextPlainResponse(MockHttpServletResponse httpResponse, String expectedContend) throws IOException {
        String content = httpResponse.getContentAsString();
        Assertions.assertEquals(expectedContend, content, content + " expected");
    }
    public static void assertValidJSonObjectResponse(MockHttpServletResponse httpResponse, String extract) throws IOException {
        String jsonContent = httpResponse.getContentAsString();
        Assertions.assertAll(
            () -> Assertions.assertTrue(jsonContent.startsWith("{") && jsonContent.endsWith("}"), "Valid JSON Object expected"),
            () -> Assertions.assertTrue(jsonContent.contains(extract), extract + " expected")
        );
    }

    public static void assertValidJSonObjectResponse(MockHttpServletResponse httpResponse, String... extracts) throws IOException {
        String jsonContent = httpResponse.getContentAsString();
        Assertions.assertTrue(jsonContent.startsWith("{") && jsonContent.endsWith("}"));
        Stream.of(extracts).forEach(extract -> Assertions.assertTrue(jsonContent.contains(extract), extract + " expected"));
    }
    public static void assertValidJSonArrayResponse(MockHttpServletResponse httpResponse, String extract) throws IOException {
        String jsonContent = httpResponse.getContentAsString();
        Assertions.assertAll(
            () -> Assertions.assertTrue(jsonContent.startsWith("[") && jsonContent.endsWith("]"), "Valid JSON array expected"),
            () -> Assertions.assertTrue(jsonContent.contains(extract), extract + " expected")
        );
    }
    public static void assertValidJSonErrorResponse(MockHttpServletResponse httpResponse, String message) throws IOException {
        String jsonContent = httpResponse.getContentAsString();
        Assertions.assertAll(
            () -> Assertions.assertTrue(jsonContent.startsWith("{") && jsonContent.endsWith("}"), "Valid JSON Object expected"),
            () -> Assertions.assertTrue(jsonContent.contains("errorMessage"), "errorMessage attribut expected"),
            () -> Assertions.assertTrue(jsonContent.contains(message), message + " expected")
        );
    }
}
