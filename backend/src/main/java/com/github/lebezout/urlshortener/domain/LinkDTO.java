package com.github.lebezout.urlshortener.domain;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * The Link POJO.
 * @author lebezout@gmail.com
 */
@Data
public class LinkDTO {

    private String id;
    private String target;
    private String creator;
    private LocalDateTime creationDate;
    private LocalDateTime lastUpdatedDate;
    private boolean privateLink;
    private long accessCounter;
    private long creationCounter;

    /** default constructor */
    public LinkDTO() {
        super();
    }

    /**
     * Constructor from the database entity
     * @param entity db entity
     */
    public LinkDTO(LinkEntity entity) {
        id = entity.getId();
        target = entity.getTarget();
        creator = entity.getCreator();
        creationDate = entity.getCreationDate();
        lastUpdatedDate = entity.getLastUpdatedDate();
        privateLink = entity.isPrivateLink();
        accessCounter = entity.getAccessCounter();
        creationCounter = entity.getCreationCounter();
    }

}
