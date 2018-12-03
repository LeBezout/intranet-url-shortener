package com.github.lebezout.urlshortener.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * The Link Entity.
 * @author lebezout@gmail.com
 */
@Entity
@Table(name = "link")
public class LinkEntity {
    @Id
    private String id;
    @Column(name = "target_url")
    private String target;
    @Column(name = "created_by")
    private String creator;
    @Column(name = "created_date")
    private LocalDateTime createdDate;
    @Column(name = "last_updated")
    private LocalDateTime lastUpdatedDate;
    @Column(name = "private")
    private boolean privateLink;
    @Column(name = "access_count")
    private long accessCounter;

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
}
