package com.github.lebezout.urlshortener.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CounterSnapshotRepository extends CrudRepository<CounterSnapshotEntity, CounterSnapshotPK> {
    /**
     * Get all the snapshot id of the counters for the specified counter
     * @param counterId id of the counter
     * @return list of snapshots
     */
    List<CounterSnapshotEntity> findByCounterIdOrderBySnapshotDateDesc(String counterId);

    /**
     * Delete all the snapshots for the specified counter
     * @param counterId id of the counter
     */
    void deleteByCounterId(String counterId);
}
