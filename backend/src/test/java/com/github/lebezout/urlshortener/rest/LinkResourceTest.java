package com.github.lebezout.urlshortener.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lebezout.urlshortener.domain.LinkDTO;
import com.github.lebezout.urlshortener.domain.LinkRepository;
import com.github.lebezout.urlshortener.domain.NewLinkDTO;
import com.github.lebezout.urlshortener.error.IDTooLongException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.net.URLEncoder;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Sql("classpath:/data-test-link.sql")
@Transactional
class LinkResourceTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(LinkResourceTest.class);
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private LinkRepository repository;

    @Test
    void test_findLinks() throws Exception {
        String startDate = URLEncoder.encode("2018-11-10T00:00:00.000-00:00", "UTF-8");
        String endDate = URLEncoder.encode("2018-11-12T00:00:00.000-00:00", "UTF-8");
        String uri = String.format("/api/link?creator=JUNIT&from=%s&to=%s", startDate, endDate);
        LOGGER.debug("URI to call : {}", uri);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(new URI(uri));
        MvcResult result = mvc.perform(builder)
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andReturn();

        MockHttpServletResponse httpResponse = result.getResponse();
        List<LinkDTO> dtoList = mapper.readValue(httpResponse.getContentAsByteArray(), new TypeReference<List<LinkDTO>>() {});
        LOGGER.debug(dtoList.toString());
        Assertions.assertEquals(1, dtoList.size());
    }

    @Test
    void test_findLinksByCreator() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(new URI("/api/link/createdBy/JUNIT"));
        MvcResult result = mvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        MockHttpServletResponse httpResponse = result.getResponse();
        List<LinkDTO> dtoList = mapper.readValue(httpResponse.getContentAsByteArray(), new TypeReference<List<LinkDTO>>() {});
        LOGGER.debug(dtoList.toString());
        Assertions.assertEquals(3, dtoList.size());
    }

    @Test
    void test_getByID_found() throws Exception {
        Assertions.assertTrue(repository.findById("AZERTY").isPresent());

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(new URI("/api/link/AZERTY"));

        MvcResult result = mvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        MockHttpServletResponse httpResponse = result.getResponse();
        String jsonContent = httpResponse.getContentAsString();
        LOGGER.debug(jsonContent);
        Assertions.assertTrue(jsonContent.startsWith("{") && jsonContent.endsWith("}"));
        Assertions.assertTrue(jsonContent.contains("JUNIT"));
    }

    @Test
    void test_getByID_not_found() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(new URI("/api/link/ZZZZZ"));

        MvcResult result = mvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn();

        MockHttpServletResponse httpResponse = result.getResponse();
        String jsonContent = httpResponse.getContentAsString();
        LOGGER.debug(jsonContent);
        assertValidJSonErrorResponse(jsonContent, "Expected link not found");
    }

    @Test
    void test_getTargetLink() throws Exception {
        Assertions.assertTrue(repository.findById("1234156").isPresent());

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(new URI("/api/link/1234156/target"));

        MvcResult result = mvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andReturn();

        MockHttpServletResponse httpResponse = result.getResponse();
        String target = httpResponse.getContentAsString();
        LOGGER.debug("TARGET URL = {}", target);
        Assertions.assertEquals("http://localhost:8080/api/link/1234156", target);
    }

    @Test
    @WithMockUser(username = "admin", password = "admin")
    void test_addNewLink_generated_ID() throws Exception {
        NewLinkDTO newLink = new NewLinkDTO();
        newLink.setPrivateLink(true);
        newLink.setTarget("http://github.com");

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(new URI("/api/link"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsBytes(newLink));

        MvcResult result = mvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        MockHttpServletResponse httpResponse = result.getResponse();
        String response = httpResponse.getContentAsString();
        LOGGER.debug(response);
    }
    @Test
    @WithMockUser(username = "admin", password = "admin")
    void test_addNewLink_provided_ID() throws Exception {
        NewLinkDTO newLink = new NewLinkDTO();
        newLink.setId("TEST-ID");
        newLink.setPrivateLink(true);
        newLink.setTarget("http://github.com");

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(new URI("/api/link"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsBytes(newLink));

        MvcResult result = mvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        MockHttpServletResponse httpResponse = result.getResponse();
        String response = httpResponse.getContentAsString();
        LOGGER.debug(response);
    }
    @Test
    @WithMockUser(username = "admin", password = "admin")
    void test_addNewLink_provided_ID_already_exists() throws Exception {
        NewLinkDTO newLink = new NewLinkDTO();
        newLink.setId("AZERTY");
        newLink.setPrivateLink(true);
        newLink.setTarget("https://mywebsite.com");

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(new URI("/api/link"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsBytes(newLink));

        MvcResult result = mvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        MockHttpServletResponse httpResponse = result.getResponse();
        String jsonContent = httpResponse.getContentAsString();
        assertValidJSonErrorResponse(jsonContent, "The provided ID already exists");
    }
    @Test
    @WithMockUser(username = "admin", password = "admin")
    void test_addNewLink_provided_ID_too_long() throws Exception {
        NewLinkDTO newLink = new NewLinkDTO();
        newLink.setId("AZERTYAZERTYAZERTYAZERTYAZERTYAZERTYAZERTY");
        newLink.setPrivateLink(true);
        newLink.setTarget("https://mywebsite.com");

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(new URI("/api/link"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsBytes(newLink));

        MvcResult result = mvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        MockHttpServletResponse httpResponse = result.getResponse();
        String jsonContent = httpResponse.getContentAsString();
        assertValidJSonErrorResponse(jsonContent, "The provided ID is too long (must be lower than " + IDTooLongException.ID_MAX_LENGTH + " characters)");
    }


    @Test
    @WithMockUser(username = "JUNIT", password = "admin")
    void test_updateExistingLink() throws Exception {
        Assertions.assertTrue(repository.findById("AZERTY").isPresent());

        LinkDTO existingLink = repository.findById("AZERTY").map(LinkDTO::new).orElseThrow(() -> new IllegalArgumentException("AZERTY not found"));
        existingLink.setCreator("TEST");
        existingLink.setPrivateLink(true);
        existingLink.setTarget("http://github.com");

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put(new URI("/api/link"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsBytes(existingLink));

        mvc.perform(builder).andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @WithMockUser(username = "JUNIT", password = "admin")
    void test_deleteExistingLink() throws Exception {
        Assertions.assertTrue(repository.findById("ABCDEF").isPresent());

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.delete(new URI("/api/link/ABCDEF"));

        mvc.perform(builder).andExpect(MockMvcResultMatchers.status().isNoContent());

        Assertions.assertFalse(repository.findById("ABCDEF").isPresent());
    }

    private static void assertValidJSonErrorResponse(String jsonContent, String message) {
        Assertions.assertAll(
            () -> Assertions.assertTrue(jsonContent.startsWith("{") && jsonContent.endsWith("}"), "Valid JSON Object expected"),
            () -> Assertions.assertTrue(jsonContent.contains("errorMessage"), "errorMessage attribut expected"),
            () -> Assertions.assertTrue(jsonContent.contains(message), message + " expected")
        );
    }
}
