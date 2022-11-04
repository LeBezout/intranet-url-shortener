package com.github.lebezout.urlshortener.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * A POJO that aggregate a link & a counter by URL
 */
@Entity // !! FAKE ENTITY !!
@Table(name = "v_link_item_report") // A view to pass hibernate Schema-validation
@Data
@NoArgsConstructor
public class LinkReportItem {
    @Id
    @Column(name = "url")
    private String targetUrl;
    @Column(name = "counter_id")
    private String counterId;
    @Column(name = "counter_creator")
    private String counterCreator;
    @Column(name = "counter_creation_date")
    private LocalDateTime counterCreation;
    @Column(name = "shortcut_creator")
    private String shortcutCreator;
    @Column(name = "shortcut_creation_date")
    private LocalDateTime shortcutCreation;
    @Column(name = "shortcut_ref")
    private String shortcutRef;
    @Column(name = "last_visited")
    private LocalDateTime lastVisitedDate;
    @Column(name = "visitor_counter")
    private long visitsCounterAccess;
    @Column(name = "shortcut_counter")
    private long shortcutCounterAccess;

    public LinkReportItem(CounterEntity counterE, LinkEntity linkE) {
        counterId = counterE.getId();
        counterCreator = counterE.getCreator();
        counterCreation = counterE.getCreationDate();
        visitsCounterAccess = counterE.getVisitorCounter();
        lastVisitedDate = counterE.getLastVisitedDate();

        shortcutRef = linkE.getId();
        shortcutCounterAccess = linkE.getAccessCounter();
        shortcutCreator = linkE.getCreator();
        shortcutCreation = linkE.getCreationDate();

        targetUrl = linkE.getTarget(); // expected same as counterE.getUrl()
    }
}
