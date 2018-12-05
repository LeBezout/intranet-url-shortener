package com.github.lebezout.urlshortener.domain;

import java.util.StringJoiner;

/**
 * The DTO for adding a new link.
 * @author lebezout@gmail.com
 */
public class NewLinkDTO {

    private String id;
    private String target;
    private boolean privateLink;

    /** implicit constructor */
    public NewLinkDTO() {
        super();
    }

    /**
     * @return the OPTIONAL id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the OPTIONAL id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the MANDATORY target url
     */
    public String getTarget() {
        return target;
    }

    /**
     * @param target the MANDATORY target url
     */
    public void setTarget(String target) {
        this.target = target;
    }

    /**
     * @return the OPTIONAL private flag
     */
    public boolean isPrivateLink() {
        return privateLink;
    }

    /**
     * @param privateLink the OPTIONAL private flag
     */
    public void setPrivateLink(boolean privateLink) {
        this.privateLink = privateLink;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", NewLinkDTO.class.getSimpleName() + " [", "]")
                .add("id='" + id + "'")
                .add("target='" + target + "'")
                .add("private=" + privateLink)
                .toString();
    }
}
