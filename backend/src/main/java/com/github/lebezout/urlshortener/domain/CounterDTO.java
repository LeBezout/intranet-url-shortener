package com.github.lebezout.urlshortener.domain;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * The Counter POJO.
 * @author lebezout@gmail.com
 */
@Data
public class CounterDTO {
    private String id;
    private String url;
    private String creator;
    private LocalDateTime creationDate;
    private long counter;

    /** default constructor */
    public CounterDTO() {
        super();
    }

    /**
     * Constructor from the database entity
     * @param entity db entity
     */
    public CounterDTO(CounterEntity entity) {
        id = entity.getId();
        url = entity.getUrl();
        creator = entity.getCreator();
        creationDate = entity.getCreationDate();
        counter = entity.getVisitorCounter();
    }
}
