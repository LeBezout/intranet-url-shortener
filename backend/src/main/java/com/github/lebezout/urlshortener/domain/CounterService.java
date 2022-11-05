package com.github.lebezout.urlshortener.domain;

import com.github.lebezout.urlshortener.error.CounterAlreadyExistsException;
import com.github.lebezout.urlshortener.error.CounterNotFoundException;
import com.github.lebezout.urlshortener.error.NotLinkOwnerException;
import com.github.lebezout.urlshortener.utils.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional
public class CounterService {
    private static final String ASSERTION_MESSAGE_COUNTER_ID_IS_NULL = "Counter id cannot be null";
    private static final String ASSERTION_MESSAGE_COUNTER_URL_IS_NULL = "Counter url cannot be null";

    private final CounterRepository repository;
    private final CounterSnapshotRepository snapshotRepository;
    private final IdGenerator idGenerator;

    /**
     * Get a counter from its ID
     * @param id id of the counter
     * @return counter DTO
     */
    public CounterDTO getFromID(String id) {
        Assert.notNull(id, ASSERTION_MESSAGE_COUNTER_ID_IS_NULL);
        Optional<CounterEntity> existingCounter = repository.findById(id);
        return existingCounter.map(CounterDTO::new).orElseThrow(CounterNotFoundException::new);
    }

    /**
     * Get a counter from its URL
     * @param url url of the counter
     * @return counter DTO
     */
    public CounterDTO getFromUrl(String url) {
        Assert.notNull(url, ASSERTION_MESSAGE_COUNTER_URL_IS_NULL);
        Optional<CounterEntity> existingCounter = repository.findByUrl(url);
        return existingCounter.map(CounterDTO::new).orElseThrow(CounterNotFoundException::new);
    }

    /**
     * Find all the counters of the specified creator
     * @param creator username
     * @return list of links
     */
    @Transactional(readOnly = true)
    public List<CounterDTO> findByCreator(String creator) {
        Assert.notNull(creator, "Counter creator cannot be null");
        List<CounterEntity> entities = repository.findByCreatorOrderByCreationDateDesc(creator);
        return entities.stream().map(CounterDTO::new).collect(Collectors.toList());
    }

    /**
     * Create a new counter for a website
     * @param url url of the website
     * @param creator creator of the counter (owner of the website)
     * @return counter DTO
     */
    public CounterDTO initCounter(String url, final String creator) {
        Assert.notNull(url, ASSERTION_MESSAGE_COUNTER_URL_IS_NULL);
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
        // lastVisited is null at creation
        newCounter.setUrl(url);
        LOGGER.info("Creating new counter : {}", newCounter);
        repository.save(newCounter);
        return new CounterDTO(newCounter);
    }

    /**
     * Reset a counter if owner
     * @param id id of the counter
     * @param creator creator of the counter (owner of the website)
     * @return counter
     * @throws NotLinkOwnerException if not owner of the counter
     */
    public CounterDTO resetCounter(String id, final String creator) {
        Assert.notNull(id, ASSERTION_MESSAGE_COUNTER_ID_IS_NULL);
        Optional<CounterEntity> existingCounter = repository.findById(id);
        CounterEntity entity = existingCounter.orElseThrow(CounterNotFoundException::new);
        NotLinkOwnerException.throwIfNeeded(entity.getCreator(), creator);
        entity.setVisitorCounter(0L);
        entity.setLastVisitedDate(null);
        repository.save(entity);
        return new CounterDTO(entity);
    }

    /**
     * Increment the visitor counter and set the last visited date
     * @param id id of the counter
     * @return counter new value
     */
    public long visit(String id) {
        Assert.notNull(id, ASSERTION_MESSAGE_COUNTER_ID_IS_NULL);
        Optional<CounterEntity> existingCounter = repository.findById(id);
        CounterEntity entity = existingCounter.orElseThrow(CounterNotFoundException::new);
        entity.setVisitorCounter(entity.getVisitorCounter() + 1);
        entity.setLastVisitedDate(LocalDateTime.now());
        repository.save(entity);
        return entity.getVisitorCounter();
    }

    /**
     * Take a snapshot for this counter
     * @param counterId id of the counter
     * @param claimant snapshot claimant name
     */
    public void takeSnapshot(String counterId, String claimant) {
        Assert.notNull(counterId, ASSERTION_MESSAGE_COUNTER_ID_IS_NULL);
        Assert.notNull(claimant, "Snapshot claimant cannot be null");
        CounterEntity counter = repository.findById(counterId).orElseThrow(CounterNotFoundException::new);
        CounterSnapshotEntity snapshot = new CounterSnapshotEntity();
        snapshot.setCounterId(counter.getId());
        snapshot.setClaimant(claimant);
        snapshot.setSnapshotDate(LocalDateTime.now());
        snapshot.setCounterValue(counter.getVisitorCounter());
        snapshotRepository.save(snapshot);
    }

    /**
     * Get all snapshots for a counter
     * @param counterId id of the counter
     * @return list of DTO
     */
    public List<CounterSnapshotDTO> getAllSnapshots(String counterId) {
        Assert.notNull(counterId, ASSERTION_MESSAGE_COUNTER_ID_IS_NULL);
        return snapshotRepository.findByCounterIdOrderBySnapshotDateDesc(counterId)
            .stream().map(CounterSnapshotDTO::new).collect(Collectors.toList());
    }
}
