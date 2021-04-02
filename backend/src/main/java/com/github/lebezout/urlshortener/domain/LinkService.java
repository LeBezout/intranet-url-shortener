package com.github.lebezout.urlshortener.domain;

import com.github.lebezout.urlshortener.config.Params;
import com.github.lebezout.urlshortener.error.IDAlreadyExistsException;
import com.github.lebezout.urlshortener.error.IDTooLongException;
import com.github.lebezout.urlshortener.error.LinkNotFoundException;
import com.github.lebezout.urlshortener.error.NotLinkOwnerException;
import com.github.lebezout.urlshortener.utils.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The service layer to manage our links and to add some cache capabilities.
 * @author lebezout@gmail.com
 */
@Service
@Transactional
public class LinkService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LinkService.class);
    private final LinkRepository repository;
    private final IdGenerator idGenerator;
    private final Params params;

    /**
     * Constructor for DI
     * @param repo the autowired repository instance
     * @param generator the autowired IdGenerator
     * @param config the autowired config
     */
    public LinkService(LinkRepository repo, IdGenerator generator, Params config) {
        repository = repo;
        idGenerator = generator;
        params = config;
    }

    /**
     * Find all the links of the specified creator
     * @param creator username
     * @return list of links
     */
    //TODO @Cacheable(cacheNames="links-by-creator", key="#creator")
    @Transactional(readOnly = true)
    public List<LinkDTO> findByCreator(String creator) {
        Assert.notNull(creator, "Link creator cannot be null");
        List<LinkEntity> entities = repository.findByCreatorOrderByLastUpdatedDateDesc(creator);
        return entities.stream().map(LinkDTO::new).collect(Collectors.toList());
    }

    /**
     * Find all the links corresponding to the provided criteria
     * @param creator username of the creator
     * @param start a start date
     * @param end a end date
     * @return list of links
     */
    @Transactional(readOnly = true)
    public List<LinkDTO> findByCriteria(String creator, LocalDateTime start, LocalDateTime end) {
        List<LinkEntity> entities = repository.findByCreatorAndLastUpdatedDate(creator, start, end);
        return entities.stream().map(LinkDTO::new).collect(Collectors.toList());
    }

    /**
     * Find a link by its ID
     * @param id id of the link
     * @return link data
     * @throws LinkNotFoundException if the link is not found
     */
    @Transactional(readOnly = true)
    @Cacheable(cacheNames="links")
    public LinkDTO getByID(String id) {
        Assert.notNull(id, "Link ID cannot be null");
        Optional<LinkEntity> link = getLinkEntity(id);
        return link.map(LinkDTO::new).orElseThrow(LinkNotFoundException::new);
    }

    /**
     * Add a new link
     * @param link data of the link to create
     * @param creator username
     * @return new link data (with a new id if not provided)
     * @throws IDAlreadyExistsException if the provided id already exists
     * @throws IDTooLongException if the provided id is too long
     */
    @CachePut(cacheNames="links") // the method is always executed and its result is placed into the cache
    public LinkDTO addNewLink(final NewLinkDTO link, final String creator) {
        Assert.notNull(link, "New link data cannot be null");
        Assert.notNull(creator, "Link creator cannot be null");
        String id = link.getId();
        if (StringUtils.hasText(id)) {
            LOGGER.info("Provided ID is {}", id);
            IDTooLongException.throwIfNeeded(id);
            Optional<LinkEntity> existingLink = getLinkEntity(id);
            IDAlreadyExistsException.throwIfNeeded(existingLink);
        } else {
            id = idGenerator.generate(params.getIdLength());
            LOGGER.info("New ID generated {}", id);
        }
        // Target URL already exists ?
        Optional<LinkEntity> existingTargetLink = repository.findByTarget(link.getTarget());
        if (existingTargetLink.isPresent()) {
            LinkEntity entity = existingTargetLink.get();
            // increment counter
            long created = entity.getCreationCounter() + 1;
            LOGGER.info("Attempt to create an existing target link : {}, {} times", link.getTarget(), created);
            entity.setCreationCounter(created);
            repository.save(entity);
            // return data
            return existingTargetLink.map(LinkDTO::new).orElseThrow(LinkNotFoundException::new);
        }

        // create new entity
        LinkEntity entity = new LinkEntity();
        entity.setId(id);
        entity.setCreator(creator);
        entity.setCreatedDate(LocalDateTime.now());
        entity.setLastUpdatedDate(LocalDateTime.now());
        entity.setPrivateLink(link.isPrivateLink());
        entity.setTarget(link.getTarget());
        entity.setCreationCounter(1);
        // accessCounter defaults to 0
        entity = saveLinkEntity(entity);
        LinkDTO newLink = new LinkDTO(entity);
        LOGGER.info("New link inserted is {}", newLink);
        return newLink;
    }

    /**
     * Update a specified link (the id can obviously not be updated)
     * @param link data of the link to update
     * @param updater username
     * @throws LinkNotFoundException if the id is not found
     * @throws NotLinkOwnerException if the updater is not the owner of the link
     */
    @CachePut(cacheNames="links")
    public void updateLink(LinkDTO link, final String updater) {
        Assert.notNull(link, "Link data cannot be null");
        Assert.notNull(updater, "Link updater cannot be null");
        Assert.hasText(link.getId(), "No ID provided");
        Optional<LinkEntity> entity = getLinkEntity(link.getId());
        LinkEntity entityToUpdate = entity.orElseThrow(LinkNotFoundException::new);
        NotLinkOwnerException.throwIfNeeded(entityToUpdate.getCreator(), updater);
        // ok, update this entity
        entityToUpdate.setLastUpdatedDate(LocalDateTime.now());
        entityToUpdate.setPrivateLink(link.isPrivateLink());
        entityToUpdate.setTarget(link.getTarget());
        saveLinkEntity(entityToUpdate);
    }

    /**
     * Delete a specified link
     * @param id id of the expected link
     * @param updater username
     * @throws LinkNotFoundException in the id is not found
     * @throws NotLinkOwnerException if the updater is not the owner of the link
     */
    @CacheEvict(cacheNames="links", key = "#id")
    public void deleteLink(String id, final String updater) {
        Assert.hasText(id, "No ID provided");
        Assert.notNull(updater, "Link updater cannot be null");
        Optional<LinkEntity> entity = getLinkEntity(id);
        LinkEntity entityToDelete = entity.orElseThrow(LinkNotFoundException::new);
        NotLinkOwnerException.throwIfNeeded(entityToDelete.getCreator(), updater);
        // ok, delete this entity
        repository.delete(entity.get()); // NOSONAR orElseThrow
    }

    private Optional<LinkEntity> getLinkEntity(String id) {
        LOGGER.debug("Access the database: getLinkEntity by id {}", id);
        return repository.findById(id);
    }

    private LinkEntity saveLinkEntity(LinkEntity entity) {
        LOGGER.debug("Access the database: saveLinkEntity by id {}", entity.getId());
        return repository.save(entity);
    }
}
