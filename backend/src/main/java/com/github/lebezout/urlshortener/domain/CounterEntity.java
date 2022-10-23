package com.github.lebezout.urlshortener.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * The Counter Entity.
 * @author lebezout@gmail.com
 */
@Getter
@Setter
@Entity
@Table(name = "counter")
public class CounterEntity {
    @Id
    private String id;
    @Column(name = "url")
    private String url;
    @Column(name = "created_by")
    private String creator;
    @Column(name = "creation_date")
    private LocalDateTime creationDate;
    @Column(name = "visitor_counter")
    private long visitorCounter;

}
