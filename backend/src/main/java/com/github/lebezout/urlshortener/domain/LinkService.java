package com.github.lebezout.urlshortener.domain;

import com.github.lebezout.urlshortener.error.IDAlreadyExistsException;
import com.github.lebezout.urlshortener.error.IDNotAcceptedException;
import com.github.lebezout.urlshortener.error.LinkNotFoundException;
import com.github.lebezout.urlshortener.error.NotOwnerException;
import com.github.lebezout.urlshortener.error.UrlNotAcceptedException;
import com.github.lebezout.urlshortener.utils.IdGenerator;
import com.github.lebezout.urlshortener.utils.IdValidator;
import com.github.lebezout.urlshortener.utils.TargetUrlValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequiredArgsConstructor
@Slf4j
@Service
@Transactional
public class LinkService {
    private static final String ASSERTION_MESSAGE_CREATOR_IS_NULL = "Link creator cannot be null";
    private final LinkRepository repository;
    private final IdGenerator idGenerator;
    private final IdValidator idValidator;
    private final TargetUrlValidator urlValidator;

    /**
     * Find all public links of the specified creator
     * @param creator username
     * @return list of links
     */
    @Transactional(readOnly = true)
    public List<LinkDTO> findPublicByCreator(String creator) {
        Assert.notNull(creator, ASSERTION_MESSAGE_CREATOR_IS_NULL);
        List<LinkEntity> entities = repository.findByCreatorAndPrivateLinkIsFalseOrderByLastUpdatedDateDesc(creator);
        return entities.stream().map(LinkDTO::new).collect(Collectors.toList());
    }

    /**
     * Find all the links of the specified creator
     * @param creator username
     * @return list of links
     */
    @Transactional(readOnly = true)
    public List<LinkDTO> findAllByCreator(String creator) {
        Assert.notNull(creator, ASSERTION_MESSAGE_CREATOR_IS_NULL);
        List<LinkEntity> entities = repository.findByCreatorOrderByLastUpdatedDateDesc(creator);
        return entities.stream().map(LinkDTO::new).collect(Collectors.toList());
    }

    /**
     * Find all the links corresponding to the provided criteria
     * @param creator username of the creator (optional)
     * @param start a start date (required)
     * @param end an end date (required)
     * @return list of links
     */
    @Transactional(readOnly = true)
    public List<LinkDTO> findByCriteria(String creator, LocalDateTime start, LocalDateTime end) {
        List<LinkEntity> entities = StringUtils.hasText(creator)
            ? repository.findByCreatorAndLastUpdatedDate(creator, start, end)
            : repository.findByLastUpdatedDate(start, end);
        // TODO Pageable for limiting results
        return entities.stream().map(LinkDTO::new).collect(Collectors.toList());
    }

    /**
     * Find all the links corresponding to the provided target URL
     * @param targetUrl target url
     * @return list of links
     */
    @Transactional(readOnly = true)
    public List<LinkDTO> findByTargetUrl(String targetUrl) {
        Assert.notNull(targetUrl, "Target URL cannot be null");
        return repository.findByTarget(targetUrl).stream().map(LinkDTO::new).collect(Collectors.toList());
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
     * @throws IDNotAcceptedException if the provided id is too long
     * @throws UrlNotAcceptedException if the provided target url is rejected
     */
    @CachePut(cacheNames="links") // the method is always executed and its result is placed into the cache
    public LinkDTO addNewLink(final NewLinkDTO link, final String creator) {
        Assert.notNull(link, "New link data cannot be null");
        Assert.notNull(creator, ASSERTION_MESSAGE_CREATOR_IS_NULL);
        String id = link.getId();
        if (StringUtils.hasText(id)) {
            LOGGER.info("Provided ID is {}", id);
            if (!idValidator.accept(id)) {
                throw new IDNotAcceptedException();
            }
            getLinkEntity(id).ifPresent(l -> { throw new IDAlreadyExistsException(); });
            // If id is provided, we don't check if target URL already exists
        } else {
            // Check if target URL already exists ?
            List<LinkEntity> existingTargetLinks = repository.findByTarget(link.getTarget());
            if (!existingTargetLinks.isEmpty()) {
                LinkEntity entity = existingTargetLinks.get(0);
                // increment counter
                long created = entity.getCreationCounter() + 1;
                LOGGER.info("Attempt to create an existing target link : {}, {} times", link.getTarget(), created);
                entity.setCreationCounter(created);
                repository.save(entity);
                // return data
                return new LinkDTO(entity);
            }
            // else generate ID
            id = idGenerator.generate();
            LOGGER.info("New ID generated {}", id);
        }
        // validate target URL
        if (!urlValidator.accept(link.getTarget())) {
            throw new UrlNotAcceptedException();
        }
        // create new entity
        LinkEntity entity = new LinkEntity();
        entity.setId(id);
        entity.setCreator(creator);
        entity.setCreationDate(LocalDateTime.now());
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
     * @throws NotOwnerException if the updater is not the owner of the link
     * @throws UrlNotAcceptedException if the provided target url is rejected
     */
    @CachePut(cacheNames="links")
    public void updateLink(LinkDTO link, final String updater) {
        Assert.notNull(link, "Link data cannot be null");
        Assert.notNull(updater, "Link updater cannot be null");
        Assert.hasText(link.getId(), "No ID provided");
        Optional<LinkEntity> entity = getLinkEntity(link.getId());
        LinkEntity entityToUpdate = entity.orElseThrow(LinkNotFoundException::new);
        NotOwnerException.throwIfNeeded(entityToUpdate.getCreator(), updater);
        // validate target URL
        if (!urlValidator.accept(link.getTarget())) {
            throw new UrlNotAcceptedException();
        }
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
     * @throws NotOwnerException if the updater is not the owner of the link
     */
    @CacheEvict(cacheNames="links", key = "#id")
    public void deleteLink(String id, final String updater) {
        Assert.hasText(id, "No ID provided");
        Assert.notNull(updater, ASSERTION_MESSAGE_CREATOR_IS_NULL);
        Optional<LinkEntity> entity = getLinkEntity(id);
        LinkEntity entityToDelete = entity.orElseThrow(LinkNotFoundException::new);
        NotOwnerException.throwIfNeeded(entityToDelete.getCreator(), updater);
        // ok, delete this entity
        repository.delete(entity.get()); // NOSONAR orElseThrow used above
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
