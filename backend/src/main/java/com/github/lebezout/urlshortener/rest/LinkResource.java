package com.github.lebezout.urlshortener.rest;

import com.github.lebezout.urlshortener.config.Params;
import com.github.lebezout.urlshortener.domain.LinkDTO;
import com.github.lebezout.urlshortener.domain.LinkRepository;
import com.github.lebezout.urlshortener.domain.LinkService;
import com.github.lebezout.urlshortener.domain.NewLinkDTO;
import com.github.lebezout.urlshortener.utils.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * The Link resource REST controller.
 * @author lebezout@gmail.com
 */
@RestController
@RequestMapping("/api/link")
public class LinkResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(LinkResource.class);
    private final LinkService linkService;

    /**
     * Constructor for DI
     * @param service the autowired link service
     */
    public LinkResource(LinkService service, Params config, LinkRepository repo, IdGenerator generator) {
        linkService = service;
    }

    @GetMapping
    public List<LinkDTO> findLinks(
        @RequestParam(name = "creator", required = false) String creator,
        @RequestParam(name = "from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
        @RequestParam(name = "to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        LOGGER.info("Find links created by {} and updated between {} & {}", creator, start, end);
        return linkService.findByCriteria(creator, start, end);
    }

    @GetMapping(path = "createdBy/{creator}")
    public List<LinkDTO> findLinksByCreator(@PathVariable("creator") String creator) {
        Assert.hasText(creator, "No creator provided");
        LOGGER.info("Find links created by {}", creator);
        return linkService.findByCreator(creator);
    }

    @GetMapping(path = "{idLink}")
    public LinkDTO getByID(@PathVariable("idLink") String idLink) {
        Assert.hasText(idLink, "No ID provided");
        LOGGER.info("Find link {}", idLink);
        return linkService.getByID(idLink);
    }

    @GetMapping(path = "{idLink}/target", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getTargetLink(@PathVariable("idLink") String idLink) {
        Assert.hasText(idLink, "No ID provided");
        LOGGER.info("Find target url for {}", idLink);
        return linkService.getByID(idLink).getTarget();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LinkDTO addNewLink(@RequestBody final NewLinkDTO link, Principal principal) {
        Assert.notNull(link, "No data provided");
        Assert.hasText(link.getTarget(), "No target URL provided");
        Assert.notNull(principal, "No credentials provided");
        return linkService.addNewLink(link, principal.getName());
    }

    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateExistingLink(@RequestBody final LinkDTO link, Principal principal) {
        Assert.notNull(link, "No data provided");
        Assert.hasText(link.getId(), "No ID provided");
        Assert.hasText(link.getTarget(), "No target URL provided");
        Assert.notNull(principal, "No credentials provided");
        linkService.updateLink(link, principal.getName());
    }

    @DeleteMapping(path = "{idLink}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteExistingLink(@PathVariable("idLink") String idLink, Principal principal) {
        Assert.hasText(idLink, "No ID provided");
        LOGGER.info("Delete link {}", idLink);
        linkService.deleteLink(idLink, principal.getName());
    }
}
