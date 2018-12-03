package com.github.lebezout.urlshortener.domain;

import java.util.StringJoiner;

/**
 * The DTO for adding a new link.
 * @author lebezout@gmail.com
 */
public class NewLinkDTO {

    private String target;
    private boolean privateLink;

    /** implicit constructor */
    public NewLinkDTO() {
        super();
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public boolean isPrivateLink() {
        return privateLink;
    }

    public void setPrivateLink(boolean privateLink) {
        this.privateLink = privateLink;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", NewLinkDTO.class.getSimpleName() + " [", "]")
                .add("target='" + target + "'")
                .add("private=" + privateLink)
                .toString();
    }
}
