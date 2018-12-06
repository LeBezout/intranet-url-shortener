package com.github.lebezout.urlshortener.domain;

import java.time.LocalDateTime;
import java.util.StringJoiner;

/**
 * The Link POJO.
 * @author lebezout@gmail.com
 */
public class LinkDTO {

    private String id;
    private String target;
    private String creator;
    private LocalDateTime createdDate;
    private LocalDateTime lastUpdatedDate;
    private boolean privateLink;
    private long accessCounter;

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
        createdDate = entity.getCreatedDate();
        lastUpdatedDate = entity.getLastUpdatedDate();
        privateLink = entity.isPrivateLink();
        accessCounter = entity.getAccessCounter();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(LocalDateTime lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public boolean isPrivateLink() {
        return privateLink;
    }

    public void setPrivateLink(boolean privateLink) {
        this.privateLink = privateLink;
    }

    public long getAccessCounter() {
        return accessCounter;
    }

    public void setAccessCounter(long accessCounter) {
        this.accessCounter = accessCounter;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", LinkDTO.class.getSimpleName() + " [", "]")
                .add("id='" + id + "'")
                .add("target='" + target + "'")
                .add("createdBy='" + creator + "'")
                .add("createdOn=" + createdDate)
                .add("lastUpdatedOn=" + lastUpdatedDate)
                .add("isPrivate=" + privateLink)
                .add("accessed=" + accessCounter)
                .toString();
    }
}
