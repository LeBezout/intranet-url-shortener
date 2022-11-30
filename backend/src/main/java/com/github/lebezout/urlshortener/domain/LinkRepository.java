package com.github.lebezout.urlshortener.domain;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * The Link Repository.
 * @author lebezout@gmail.com
 */
@Repository
public interface LinkRepository extends CrudRepository<LinkEntity, String> {
    /**
     * Select public links by target url
     * @param target the target url
     * @return list of links
     */
    @Query("from LinkEntity l where l.target = ?1 and l.privateLink = false")
    List<LinkEntity> findByTarget(String target);

    /**
     * Select all links created by a specific user
     * @param creator the user who created the link
     * @return list of links
     */
    List<LinkEntity> findByCreatorOrderByLastUpdatedDateDesc(String creator);

    /**
     * Select only the public links created by a specific user
     * @param creator the user who created the link
     * @return list of links
     */
    List<LinkEntity> findByCreatorAndPrivateLinkIsFalseOrderByLastUpdatedDateDesc(String creator);

    /**
     * Select all links where lastUpdatedDate is between the two specified dates
     * @param startDate start of range
     * @param endDate  end of range
     * @return list of links
     */
    @Query("from LinkEntity l where l.lastUpdatedDate between ?1 and ?2 and l.privateLink = false order by l.lastUpdatedDate desc")
    List<LinkEntity> findByLastUpdatedDate(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Select all links created by a specific user where lastUpdatedDate is between the two specified dates
     * @param creator the user who created the link
     * @param startDate start of range
     * @param endDate  end of range
     * @return list of links
     */
    @Query("from LinkEntity l where l.creator = ?1 and (l.lastUpdatedDate between ?2 and ?3) and l.privateLink = false order by l.lastUpdatedDate desc")
    List<LinkEntity> findByCreatorAndLastUpdatedDate(String creator, LocalDateTime startDate, LocalDateTime endDate);
}
