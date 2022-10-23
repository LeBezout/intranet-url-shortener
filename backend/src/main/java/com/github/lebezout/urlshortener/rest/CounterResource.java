package com.github.lebezout.urlshortener.rest;

import com.github.lebezout.urlshortener.domain.CounterDTO;
import com.github.lebezout.urlshortener.domain.CounterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * The Counter resource REST controller.
 * @author lebezout@gmail.com
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/count")
public class CounterResource {
    private final CounterService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CounterDTO createCounter(@RequestParam("url") String url, Principal principal) {
        Assert.hasText(url, "No URL provided");
        Assert.notNull(principal, "No credentials provided");
        return service.initCounter(url, principal.getName());
    }

    @GetMapping(path = "{id}")
    public CounterDTO getByID(@PathVariable("id") String counterId) {
        assertIdIsProvided(counterId);
        LOGGER.info("Find counter from id {}", counterId);
        return service.getFromID(counterId);
    }

    @GetMapping
    public CounterDTO getByURL(@RequestParam("url") String url) {
        Assert.hasText(url, "No URL provided");
        LOGGER.info("Find counter from url {}", url);
        return service.getFromUrl(url);
    }

    @PutMapping
    public long visitUrl(String counterId) {
        assertIdIsProvided(counterId);
        LOGGER.info("Update counter from id {}", counterId);
        return service.visit(counterId);
    }

    // TODO visitAndGetSvg
    // TODO visitAndGetPng

    // TODO reset ?

    // TODO delete ?

    private static void assertIdIsProvided(String idLink) {
        Assert.hasText(idLink, "No ID provided");
    }
}
