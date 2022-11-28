package com.github.lebezout.urlshortener.domain;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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
     * Count the snapshots for the specified counter for a period
     * @param counterId id of the counter
     * @param startDate start date of the snapshots
     * @param endDate end date of the snapshots
     * @return number of snapshots for the counter
     */
    long countByCounterIdAndSnapshotDateBetween(String counterId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Get all the snapshots for the specified counter for a period
     * @param counterId id of the counter
     * @param startDate start date of the snapshots
     * @param endDate end date of the snapshots
     * @return list of snapshots
     */
    @Query("from CounterSnapshotEntity s where s.counterId = ?1 and s.snapshotDate between ?2 and ?3 order by s.snapshotDate asc")
    List<CounterSnapshotEntity> findByCounterIdBetween(String counterId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Delete all the snapshots for the specified counter
     * @param counterId id of the counter
     */
    void deleteByCounterId(String counterId);
}
