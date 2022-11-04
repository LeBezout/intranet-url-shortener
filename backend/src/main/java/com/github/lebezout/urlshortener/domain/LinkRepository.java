package com.github.lebezout.urlshortener.domain;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * The Link Repository.
 * @author lebezout@gmail.com
 */
@Repository
public interface LinkRepository extends CrudRepository<LinkEntity, String> {

    /**
     * Select a link by its target
     * @param target the target url
     * @return link
     */
    @Query("from LinkEntity l where l.target = ?1 and l.privateLink = false")
    Optional<LinkEntity> findByTarget(String target);

    /**
     * Select all links created by a specific user
     * @param creator the user who created the link
     * @return list of links
     */
    List<LinkEntity> findByCreatorOrderByLastUpdatedDateDesc(String creator);

    /**
     * Select all links created by a specific user where lastUpdatedDate is between the two specified dates
     * @param creator the user who created the link
     * @param startDate start of range
     * @param endDate  end of range
     * @return list of links
     */
    @Query("from LinkEntity l where l.creator = ?1 and (l.lastUpdatedDate between ?2 and ?3) order by l.lastUpdatedDate desc")
    List<LinkEntity> findByCreatorAndLastUpdatedDate(String creator, LocalDateTime startDate, LocalDateTime endDate);
}
