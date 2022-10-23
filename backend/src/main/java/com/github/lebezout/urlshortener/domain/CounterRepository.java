package com.github.lebezout.urlshortener.domain;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CounterRepository extends CrudRepository<CounterEntity, String> {
    /**
     * Select a counter by its url
     * @param url the site url
     * @return link
     */
    @Query("from CounterEntity l where l.url = ?1")
    Optional<CounterEntity> findByUrl(String url);
}
