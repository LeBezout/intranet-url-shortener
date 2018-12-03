package com.github.lebezout.urlshortener.rest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Sql("classpath:/data-test.sql")
@Transactional
public class RedirectionTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedirectionTest.class);
    @Autowired
    private MockMvc mvc;

    @Test
    public void test_redirection_not_found() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(new URI("/redirect/0000"));
        MvcResult result = mvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.status().isTemporaryRedirect())
                .andExpect(MockMvcResultMatchers.header().exists(HttpHeaders.LOCATION))
                .andExpect(MockMvcResultMatchers.header().string(HttpHeaders.LOCATION, "http://localhost:8080/demo/404.html"))
                .andReturn();
        MockHttpServletResponse httpResponse = result.getResponse();
        String location = httpResponse.getHeader(HttpHeaders.LOCATION);
        LOGGER.debug(location);
        Assert.assertEquals("http://localhost:8080/demo/404.html", location);
    }

    @Test
    public void test_redirection_found() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(new URI("/redirect/AZERTY"));
        MvcResult result = mvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.status().isTemporaryRedirect())
                .andExpect(MockMvcResultMatchers.header().exists(HttpHeaders.LOCATION))
                .andExpect(MockMvcResultMatchers.header().string(HttpHeaders.LOCATION, "http://localhost:8080/api/link/AZERTY"))
                .andReturn();
        MockHttpServletResponse httpResponse = result.getResponse();
        Assert.assertNotNull(httpResponse);
    }
}