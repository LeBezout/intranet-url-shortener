package com.github.lebezout.urlshortener.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * The Link Repository.
 * @author lebezout@gmail.com
 */
public interface LinkRepository extends CrudRepository<LinkEntity, String> {

    /**
     * Select all links created by a specific user
     * @param creator the user who created the link
     * @return list of links
     */
    List<LinkEntity> findByCreatorOrderByLastUpdatedDate(String creator);

    //TODO
    //List<LinkEntity> findByCreatorOrderByLastUpdatedDate();
}
