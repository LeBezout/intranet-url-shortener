package com.github.lebezout.urlshortener.rest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Sql("classpath:/data-test-counter.sql")
@Transactional
class CounterResourceTest {
    @Autowired
    private MockMvc mvc;

    @Test
    void test_getByID_found() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(new URI("/api/count/AZERTY1234"));

        MvcResult result = mvc.perform(builder)
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andReturn();

        MockHttpServletResponse httpResponse = result.getResponse();
        String jsonContent = httpResponse.getContentAsString();

        Assertions.assertTrue(jsonContent.startsWith("{") && jsonContent.endsWith("}"));
        Assertions.assertTrue(jsonContent.contains("JUNIT"));
    }
    @Test
    void test_getByID_not_found() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(new URI("/api/count/ZZZZZ"));

        MvcResult result = mvc.perform(builder)
            .andExpect(MockMvcResultMatchers.status().isNotFound())
            .andReturn();

        MockHttpServletResponse httpResponse = result.getResponse();
        String jsonContent = httpResponse.getContentAsString();
        assertValidJSonErrorResponse(jsonContent, "Expected counter not found");
    }

    @Test
    void test_getByURL_found() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(new URI("/api/count")).queryParam("url", "https://github.com");

        MvcResult result = mvc.perform(builder)
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andReturn();

        MockHttpServletResponse httpResponse = result.getResponse();
        String jsonContent = httpResponse.getContentAsString();

        Assertions.assertTrue(jsonContent.startsWith("{") && jsonContent.endsWith("}"));
        Assertions.assertTrue(jsonContent.contains("JUNIT"));
    }
    @Test
    void test_getByURL_not_found() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(new URI("/api/count")).queryParam("url", "https://www.website.org");

        MvcResult result = mvc.perform(builder)
            .andExpect(MockMvcResultMatchers.status().isNotFound())
            .andReturn();

        MockHttpServletResponse httpResponse = result.getResponse();
        String jsonContent = httpResponse.getContentAsString();
        assertValidJSonErrorResponse(jsonContent, "Expected counter not found");
    }

    @Test
    @WithMockUser(username = "admin", password = "admin")
    void test_createCounter() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(new URI("/api/count")).queryParam("url", "https://www.mywebsite.org");
        MvcResult result = mvc.perform(builder)
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andReturn();

        MockHttpServletResponse httpResponse = result.getResponse();
        String jsonContent = httpResponse.getContentAsString();
        Assertions.assertTrue(jsonContent.startsWith("{") && jsonContent.endsWith("}"));
        Assertions.assertTrue(jsonContent.contains("admin") && jsonContent.contains("mywebsite.org"));
    }

    @Test
    void test_visitUrl() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(new URI("/api/count/AZERTY1234/v"));
        MvcResult result = mvc.perform(builder)
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
            .andReturn();

        MockHttpServletResponse httpResponse = result.getResponse();
        String counter = httpResponse.getContentAsString();
        Assertions.assertEquals("00 000 001", counter);
    }
    @Test
    void test_visitUrl_not_found() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(new URI("/api/count/foobar/v"));
        MvcResult result = mvc.perform(builder)
            .andExpect(MockMvcResultMatchers.status().isNotFound())
            .andReturn();
    }
    @Test
    void test_visitUrl_and_get_svg() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(new URI("/api/count/AZERTY1234/svg"));
        MvcResult result = mvc.perform(builder)
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith("image/svg+xml"))
            .andReturn();
        MockHttpServletResponse httpResponse = result.getResponse();
        String svg = httpResponse.getContentAsString();
        Assertions.assertTrue(svg.contains("http://www.w3.org/2000/svg"));
    }
    @Test
    void test_visitUrl_and_get_png() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(new URI("/api/count/AZERTY1234/png"));
        MvcResult result = mvc.perform(builder)
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith("image/png"))
            .andReturn();
        MockHttpServletResponse httpResponse = result.getResponse();
        byte[] image = httpResponse.getContentAsByteArray();
        Assertions.assertTrue(image.length > 20, "Size must be > 20 bytes");
        Assertions.assertAll(
            () -> Assertions.assertEquals(137, image[0] & 0xFF, "1st byte must be 137"),
            () -> Assertions.assertEquals('P', image[1] & 0xFF, "2nd byte must be P"),
            () -> Assertions.assertEquals('N', image[2] & 0xFF, "3rd byte must be N"),
            () -> Assertions.assertEquals('G', image[3] & 0xFF, "4th byte must be G"),
            () -> Assertions.assertEquals(13, image[4] & 0xFF, "5th byte must be CR"),
            () -> Assertions.assertEquals(10, image[5] & 0xFF, "6th byte must be LF")
        );
    }

    @Test
    @WithMockUser(username = "JUNIT", password = "admin")
    void test_resetCounter_owner() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put(new URI("/api/count/AZERTY1234/reset"));
        MvcResult result = mvc.perform(builder)
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andReturn();

        MockHttpServletResponse httpResponse = result.getResponse();
        String jsonContent = httpResponse.getContentAsString();
        Assertions.assertTrue(jsonContent.startsWith("{") && jsonContent.endsWith("}"));
    }
    @Test
    @WithMockUser(username = "admin", password = "admin")
    void test_resetCounter_not_owner() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put(new URI("/api/count/FOOBAR6789/reset"));
        MvcResult result = mvc.perform(builder)
            .andExpect(MockMvcResultMatchers.status().isForbidden())
            .andReturn();

        MockHttpServletResponse httpResponse = result.getResponse();
        String jsonContent = httpResponse.getContentAsString();
        assertValidJSonErrorResponse(jsonContent, "Only the creator of the link can update it");
    }

    @Test
    @WithMockUser(username = "JUNIT", password = "admin")
    void test_takeSnapshot_OK() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(new URI("/api/count/FOOBAR6789/snapshot"));
        MvcResult result = mvc.perform(builder)
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
            .andReturn();

        MockHttpServletResponse httpResponse = result.getResponse();
        String counter = httpResponse.getContentAsString();
        Assertions.assertEquals("5", counter);
    }

    @Test
    @Sql("classpath:/data-test-countersnapshot.sql")
    void test_getCounterSnapshots() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(new URI("/api/count/AZERTY1234/snapshots"));
        MvcResult result = mvc.perform(builder)
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andReturn();

        MockHttpServletResponse httpResponse = result.getResponse();
        String jsonContent = httpResponse.getContentAsString();
        Assertions.assertTrue(jsonContent.startsWith("[") && jsonContent.endsWith("]") && jsonContent.contains("18"));
    }

    private static void assertValidJSonErrorResponse(String jsonContent, String message) {
        Assertions.assertAll(
            () -> Assertions.assertTrue(jsonContent.startsWith("{") && jsonContent.endsWith("}"), "Valid JSON Object expected"),
            () -> Assertions.assertTrue(jsonContent.contains("errorMessage"), "errorMessage attribut expected"),
            () -> Assertions.assertTrue(jsonContent.contains(message), message + " expected")
        );
    }
}
