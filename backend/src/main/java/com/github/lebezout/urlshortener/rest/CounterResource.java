package com.github.lebezout.urlshortener.rest;

import com.github.lebezout.urlshortener.domain.CounterDTO;
import com.github.lebezout.urlshortener.domain.CounterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
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

    @PutMapping(path = "{id}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String visitUrl(@PathVariable("id") String counterId) {
        assertIdIsProvided(counterId);
        LOGGER.info("Update counter from id {}", counterId);
        long counterValue = service.visit(counterId);
        return Long.toString(counterValue);
    }

    @PutMapping(path = "{id}/svg", produces = "image/svg+xml")
    public byte[] visitAndGetSvg(@PathVariable("id") String counterId) {
        assertIdIsProvided(counterId);
        LOGGER.info("Update counter from id {}", counterId);
        long counterValue = service.visit(counterId);
        String svgTemplate = "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"120\" height=\"20\">\n" +
            "    <linearGradient id=\"a\" x2=\"0\" y2=\"100%%\">\n" +
            "        <stop offset=\"0\" stop-color=\"#bbb\" stop-opacity=\".1\"/>\n" +
            "        <stop offset=\"1\" stop-opacity=\".1\"/>\n" +
            "    </linearGradient>\n" +
            "    <rect rx=\"3\" width=\"110\" height=\"20\" fill=\"#555\" />\n" +
            "    <rect rx=\"3\" x=\"37\" width=\"75\" height=\"20\" fill=\"#9f9f9f\" />\n" +
            "    <path fill=\"#9f9f9f\" d=\"M37 0h4v20h-4z\"/>\n" +
            "    <g fill=\"#fff\" text-anchor=\"middle\" font-family=\"DejaVu Sans,Verdana,Geneva,sans-serif\" font-size=\"11\">\n" +
            "        <text x=\"19\" y=\"15\" fill=\"#010101\" fill-opacity=\".3\">visit</text>\n" +
            "        <text x=\"19\" y=\"14\">visit</text>\n" +
            "        <text x=\"75\" y=\"15\" fill=\"#010101\" fill-opacity=\".3\">%s</text>\n" +
            "        <text x=\"75\" y=\"14\">%s</text>\n" +
            "    </g>\n" +
            "</svg>";
        String strCounterValue = Long.toString(counterValue);
        return String.format(svgTemplate, strCounterValue, strCounterValue).getBytes(StandardCharsets.UTF_8);
    }

    // TODO visitAndGetPng

    @PutMapping(path = "{id}/reset")
    public CounterDTO resetCounter(@PathVariable("id") String counterId, Principal principal) {
        assertIdIsProvided(counterId);
        Assert.notNull(principal, "No credentials provided");
        assertIdIsProvided(counterId);
        return service.resetCounter(counterId, principal.getName());
    }

    // TODO delete ?

    private static void assertIdIsProvided(String id) {
        Assert.hasText(id, "No ID provided");
    }
}