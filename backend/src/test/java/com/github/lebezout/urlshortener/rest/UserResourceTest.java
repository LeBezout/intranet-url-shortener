package com.github.lebezout.urlshortener.rest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.net.URI;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class UserResourceTest {
    @Autowired
    private MockMvc mvc;

    @Test
    @WithMockUser(username = "admin", password = "admin")
    void test_login_OK_1() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(new URI("/api/user/login"));
        mvc.perform(builder).andExpect(MockMvcResultMatchers.status().isNoContent()).andReturn();
    }

    @Test
    @WithMockUser(username = "JUNIT", password = "admin")
    void test_login_OK_2() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(new URI("/api/user/login"));
        mvc.perform(builder).andExpect(MockMvcResultMatchers.status().isNoContent()).andReturn();
    }

    @Test
    void test_login_failed_no_user() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(new URI("/api/user/login"));
        mvc.perform(builder).andExpect(MockMvcResultMatchers.status().isUnauthorized()).andReturn();
    }

//    @Test
//    @WithMockUser(username = "other", password = "other")
//    void test_login_failed_bad_user() throws Exception {
//        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(new URI("/api/user/login"));
//        mvc.perform(builder).andExpect(MockMvcResultMatchers.status().isUnauthorized()).andReturn();
//    }
}
