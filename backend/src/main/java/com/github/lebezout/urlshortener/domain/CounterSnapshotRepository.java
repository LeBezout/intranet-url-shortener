package com.github.lebezout.urlshortener.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CounterSnapshotRepository extends CrudRepository<CounterSnapshotEntity, CounterSnapshotPK> {

    List<CounterSnapshotEntity> findByCounterIdOrderBySnapshotDateDesc(String counterId);
}
