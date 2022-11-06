package com.github.lebezout.urlshortener.rest;

import com.github.lebezout.urlshortener.domain.CounterDTO;
import com.github.lebezout.urlshortener.domain.CounterService;
import com.github.lebezout.urlshortener.domain.CounterSnapshotDTO;
import com.github.lebezout.urlshortener.utils.CounterFormater;
import com.github.lebezout.urlshortener.utils.ImageUtils;
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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

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

    @PostMapping(path = "{id}/snapshot", produces = MediaType.TEXT_PLAIN_VALUE)
    public String takeSnapshot(@PathVariable("id") String counterId, Principal principal) {
        assertIdIsProvided(counterId);
        Assert.notNull(principal, "No credentials provided");
        LOGGER.info("Take snapshot for counter {} claimed by {}", counterId, principal.getName());
        long counterValue = service.takeSnapshot(counterId, principal.getName());
        return Long.toString(counterValue);
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

    @GetMapping(path = "createdBy/{creator}")
    public List<CounterDTO> findByCreator(@PathVariable("creator") String creator) {
        Assert.hasText(creator, "No creator provided");
        LOGGER.info("Find counter created by {}", creator);
        return service.findByCreator(creator);
    }

    @GetMapping(path = "{id}/snapshots")
    public List<CounterSnapshotDTO> getCounterSnapshots(@PathVariable("id") String counterId) {
        assertIdIsProvided(counterId);
        LOGGER.info("Find snapshots for counter {}", counterId);
        return service.getAllSnapshots(counterId);
    }

    @GetMapping(path = "{id}/v", produces = MediaType.TEXT_PLAIN_VALUE)
    public String visitUrl(@PathVariable("id") String counterId) {
        assertIdIsProvided(counterId);
        return incrementAndGetCounterAsString(counterId);
    }

    @GetMapping(path = { "{id}/px/{color}", "{id}/px" }, produces = "image/png")
    public byte[] visitAndGetPixel(@PathVariable("id") String counterId, @PathVariable(name = "color", required = false) String pxColor) {
        assertIdIsProvided(counterId);
        incrementAndGetCounterAsString(counterId);
        try {
            return ImageUtils.pixel(pxColor);
        } catch (IOException e) {
            LOGGER.error("Can't generate pixel image (color=" + pxColor + ")", e);
            return new byte[0];
        }
    }

    @GetMapping(path = "{id}/svg", produces = "image/svg+xml")
    public byte[] visitAndGetSvg(@PathVariable("id") String counterId, @RequestParam(name = "label") Optional<String> label) {
        assertIdIsProvided(counterId);
        String strCounterValue = incrementAndGetCounterAsString(counterId);
        String caption = label.orElse("visits");
        return ImageUtils.svgBadge(caption, strCounterValue);
    }

    @GetMapping(path = "{id}/png", produces = "image/png")
    public byte[] visitAndGetPng(@PathVariable("id") String counterId) {
        assertIdIsProvided(counterId);
        String strCounterValue = incrementAndGetCounterAsString(counterId);
        try {
            return ImageUtils.fromText(strCounterValue, 20, true, "#2D3362", "#FFFFFF");
        } catch (IOException e) {
            LOGGER.error("Can't generate  image", e);
            return new byte[0];
        }
    }

    @PutMapping(path = "{id}/reset")
    public CounterDTO resetCounter(@PathVariable("id") String counterId, Principal principal) {
        assertIdIsProvided(counterId);
        Assert.notNull(principal, "No credentials provided");
        assertIdIsProvided(counterId);
        return service.resetCounter(counterId, principal.getName());
    }

    // TODO delete or disable counter ?

    private String incrementAndGetCounterAsString(String counterId) {
        LOGGER.info("Update counter from id {}", counterId);
        long counterValue = service.visit(counterId);
        return CounterFormater.format(counterValue);
    }

    private static void assertIdIsProvided(String id) {
        Assert.hasText(id, "No ID provided");
    }
}
