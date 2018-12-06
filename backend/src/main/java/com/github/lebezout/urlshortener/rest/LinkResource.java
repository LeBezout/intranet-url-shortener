package com.github.lebezout.urlshortener.rest;

import com.github.lebezout.urlshortener.config.Params;
import com.github.lebezout.urlshortener.domain.LinkDTO;
import com.github.lebezout.urlshortener.domain.LinkEntity;
import com.github.lebezout.urlshortener.domain.LinkRepository;
import com.github.lebezout.urlshortener.domain.NewLinkDTO;
import com.github.lebezout.urlshortener.utils.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
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
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The Link resource REST controller.
 * @author lebezout@gmail.com
 */
@RestController
@RequestMapping("/api/link")
public class LinkResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(LinkResource.class);
    private final LinkRepository repository;
    private final IdGenerator idGenerator;
    private final Params params;

    /**
     * Constructor for DI
     * @param config the autowired config
     * @param repo the autowired repository instance
     * @param generator the autowired IdGenerator
     */
    public LinkResource(Params config, LinkRepository repo, IdGenerator generator) {
        params = config;
        repository = repo;
        idGenerator = generator;
    }

    @Transactional(readOnly = true)
    @GetMapping
    public List<LinkDTO> findLinks(
        @RequestParam(name = "creator", required = false) String creator,
        @RequestParam(name = "from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
        @RequestParam(name = "to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        LOGGER.info("Find links created by {} and updated between {} & {}", creator, start, end);

        List<LinkEntity> entities = repository.findByCreatorAndLastUpdatedDate(creator, start, end);
        return entities.stream().map(LinkDTO::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @GetMapping(path = "createdBy/{creator}")
    public List<LinkDTO> findLinksByCreator(@PathVariable("creator") String creator) {
        Assert.hasText(creator, "No creator provided");
        LOGGER.info("Find links created by {}", creator);
        List<LinkEntity> entities = repository.findByCreatorOrderByLastUpdatedDateDesc(creator);
        return entities.stream().map(LinkDTO::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @GetMapping(path = "{idLink}")
    public LinkDTO getByID(@PathVariable("idLink") String idLink) {
        Assert.hasText(idLink, "No ID provided");
        LOGGER.info("Find link {}", idLink);
        Optional<LinkEntity> link = repository.findById(idLink);
        return link.map(LinkDTO::new).orElse(null);
    }

    @Transactional(readOnly = true)
    @GetMapping(path = "{idLink}/target", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getTargetLink(@PathVariable("idLink") String idLink) {
        Assert.hasText(idLink, "No ID provided");
        LOGGER.info("Find target url for {}", idLink);
        Optional<LinkEntity> link = repository.findById(idLink);
        return link.isPresent() ? link.get().getTarget() : null;
    }

    @Transactional
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LinkDTO addNewLink(@RequestBody final NewLinkDTO link, Principal principal) {
        Assert.notNull(link, "No data provided");
        Assert.hasText(link.getTarget(), "No target URL provided");
        Assert.notNull(principal, "No credentials provided");
        // create new entity
        LinkEntity entity = new LinkEntity();
        String id = link.getId();
        if (StringUtils.hasText(id)) {
            LOGGER.info("ID provided {}", id);
            IDTooLongException.throwIfNeeded(id);
            Optional<LinkEntity> existingLink = repository.findById(id);
            IDAlreadyExistsException.throwIfNeeded(existingLink);
        } else {
            id = idGenerator.generate(params.getIdLength());
            LOGGER.info("New ID generated {}", id);
        }
        entity.setId(id);
        entity.setCreator(principal.getName());
        entity.setCreatedDate(LocalDateTime.now());
        entity.setLastUpdatedDate(LocalDateTime.now());
        entity.setPrivateLink(link.isPrivateLink());
        entity.setTarget(link.getTarget());
        // acessCounter defaults to 0
        repository.save(entity);
        LinkDTO newLink = new LinkDTO(entity);
        LOGGER.info("New link inserted : {}", newLink);
        return newLink;

    }

    @Transactional
    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateExistingLink(@RequestBody final LinkDTO link, Principal principal) {
        Assert.notNull(link, "No data provided");
        Assert.hasText(link.getId(), "No ID provided");
        Assert.hasText(link.getTarget(), "No target URL provided");
        Assert.notNull(principal, "No credentials provided");
        Optional<LinkEntity> entity = repository.findById(link.getId());
        Assert.isTrue(entity.isPresent(), () -> "Link " +  link.getId() + " not found");
        if (!principal.getName().equals(entity.get().getCreator())) {
            throw new AccessDeniedException(String.format("%s is not the creator of the link", principal.getName()));
        }
        // ok, update entity
        LinkEntity entityToUpdate = entity.get();
        entityToUpdate.setLastUpdatedDate(LocalDateTime.now());
        entityToUpdate.setPrivateLink(link.isPrivateLink());
        entityToUpdate.setTarget(link.getTarget());
        repository.save(entityToUpdate);
    }

    @Transactional
    @DeleteMapping(path = "{idLink}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteExistingLink(@PathVariable("idLink") String idLink, Principal principal) {
        Assert.hasText(idLink, "No ID provided");
        LOGGER.info("Delete link {}", idLink);
        Optional<LinkEntity> entity = repository.findById(idLink);
        Assert.isTrue(entity.isPresent(), () -> "Link " +  idLink + " not found");
        if (!principal.getName().equals(entity.get().getCreator())) {
            throw new AccessDeniedException(String.format("%s is not the creator of the link", principal.getName()));
        }
        // ok, delete this entity
        repository.delete(entity.get());
    }
}
