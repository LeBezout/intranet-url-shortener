package com.github.lebezout.urlshortener.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * The user resource REST controller.
 * @author lebezout@gmail.com
 */
@RestController
@RequestMapping("/api/user")
public class UserResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserResource.class);

    @PostMapping(path = "login")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void login(Principal principal) {
        LOGGER.info("{} successfully logged in", principal.getName());
    }
}
