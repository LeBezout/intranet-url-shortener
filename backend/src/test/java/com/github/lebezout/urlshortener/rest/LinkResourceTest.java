package com.github.lebezout.urlshortener.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lebezout.urlshortener.domain.LinkDTO;
import com.github.lebezout.urlshortener.domain.LinkRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Sql("classpath:/data-test.sql")
@Transactional
public class LinkResourceTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(LinkResourceTest.class);
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private LinkRepository repository;

    @Test
    public void test_findLinks() throws Exception {
    }

    @Test
    public void test_findLinksByCreator() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(new URI("/api/link/createdBy/JUNIT"));
        MvcResult result = mvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        MockHttpServletResponse httpResponse = result.getResponse();
        List<LinkDTO> dtoList =  mapper.readValue(httpResponse.getContentAsByteArray(), new TypeReference<List<LinkDTO>>() {});
        LOGGER.debug(dtoList.toString());
        Assert.assertEquals(2, dtoList.size());
    }

    @Test
    public void test_getByID_found() throws Exception {
        Assert.assertTrue(repository.findById("AZERTY").isPresent());

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(new URI("/api/link/AZERTY"));

        MvcResult result = mvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        MockHttpServletResponse httpResponse = result.getResponse();
        String jsonContent = httpResponse.getContentAsString();
        LOGGER.debug(jsonContent);
        Assert.assertTrue(jsonContent.startsWith("{") && jsonContent.endsWith("}"));
        Assert.assertTrue(jsonContent.contains("JUNIT"));
    }

    @Test
    public void test_getByID_not_found() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(new URI("/api/link/ZZZZZ"));

        MvcResult result = mvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        MockHttpServletResponse httpResponse = result.getResponse();
        String jsonContent = httpResponse.getContentAsString();
        LOGGER.debug(jsonContent);
    }


    @Test
    public void test_getTargetLink() throws Exception {
        Assert.assertTrue(repository.findById("1234156").isPresent());

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(new URI("/api/link/1234156/target"));

        MvcResult result = mvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andReturn();

        MockHttpServletResponse httpResponse = result.getResponse();
        String target = httpResponse.getContentAsString();
        LOGGER.debug("TARGET URL = {}", target);
        Assert.assertEquals("http://localhost:8080/api/link/1234156", target);
    }

    @Test
    @WithMockUser(username = "admin", password = "admin")
    public void test_addNewLink() throws Exception {
        LinkDTO newLink = new LinkDTO();
        newLink.setCreator("TEST");
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
    public void test_updateExistingLink() throws Exception {
        Assert.assertTrue(repository.findById("AZERTY").isPresent());

        LinkDTO existingLink = repository.findById("AZERTY").map(LinkDTO::new).get();
        existingLink.setCreator("TEST");
        existingLink.setPrivateLink(true);
        existingLink.setTarget("http://github.com");

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put(new URI("/api/link"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsBytes(existingLink));

        mvc.perform(builder).andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin")
    public void test_deleteExistingLink() throws Exception {
        Assert.assertTrue(repository.findById("ABCDEF").isPresent());

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.delete(new URI("/api/link/ABCDEF"));

        mvc.perform(builder).andExpect(MockMvcResultMatchers.status().isNoContent());

        Assert.assertFalse(repository.findById("ABCDEF").isPresent());
    }
}