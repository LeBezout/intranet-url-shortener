package com.github.lebezout.urlshortener.domain;

import com.github.lebezout.urlshortener.error.CounterAlreadyExistsException;
import com.github.lebezout.urlshortener.error.CounterNotFoundException;
import com.github.lebezout.urlshortener.utils.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional
public class CounterService {
    private final CounterRepository repository;
    private final IdGenerator idGenerator;

    /**
     * Get a counter from its ID
     * @param id id of the counter
     * @return counter DTO
     */
    public CounterDTO getFromID(String id) {
        Assert.notNull(id, "Counter id cannot be null");
        Optional<CounterEntity> existingCounter = repository.findById(id);
        return existingCounter.map(CounterDTO::new).orElseThrow(CounterNotFoundException::new);
    }

    /**
     * Get a counter from its URL
     * @param url url of the counter
     * @return counter DTO
     */
    public CounterDTO getFromUrl(String url) {
        Assert.notNull(url, "Counter url cannot be null");
        Optional<CounterEntity> existingCounter = repository.findByUrl(url);
        return existingCounter.map(CounterDTO::new).orElseThrow(CounterNotFoundException::new);
    }

    /**
     * Create a new counter for a website
     * @param url url of the website
     * @param creator creator of the counter (owner of the website)
     * @return counter DTO
     */
    public CounterDTO initCounter(String url, final String creator) {
        Assert.notNull(url, "Counter url cannot be null");
        Assert.notNull(creator, "Counter creator cannot be null");
        // Counter for this url already exists ?
        Optional<CounterEntity> existingCounter = repository.findByUrl(url);
        CounterAlreadyExistsException.throwIfNeeded(existingCounter);
        // OK create new one
        CounterEntity newCounter = new CounterEntity();
        String id = idGenerator.generate(10);
        newCounter.setId(id);
        newCounter.setCreator(creator);
        newCounter.setCreationDate(LocalDateTime.now());
        newCounter.setUrl(url);
        LOGGER.info("Creating new counter : {}", newCounter);
        repository.save(newCounter);
        return new CounterDTO(newCounter);
    }

    /**
     * Increment the visitor counter
     * @param id id of the counter
     * @return counter new value
     */
    public long visit(String id) {
        Assert.notNull(id, "Counter id cannot be null");
        Optional<CounterEntity> existingCounter = repository.findById(id);
        CounterEntity entity = existingCounter.orElseThrow(CounterNotFoundException::new);
        entity.setVisitorCounter(entity.getVisitorCounter() + 1);
        repository.save(entity);
        return entity.getVisitorCounter();
    }
}
