package com.github.lebezout.urlshortener.domain;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportingRepository extends org.springframework.data.repository.Repository<LinkReportItem, String> {

    @Query("select new com.github.lebezout.urlshortener.domain.LinkReportItem(counter, link) from CounterEntity counter, LinkEntity link where counter.url=link.target order by counter.lastVisitedDate desc")
    List<LinkReportItem> getLinksReports();

    @Query("select new com.github.lebezout.urlshortener.domain.LinkReportItem(counter, link) from CounterEntity counter, LinkEntity link where counter.url=link.target and counter.creator=?1 order by counter.lastVisitedDate desc")
    List<LinkReportItem> getLinksReportsByCounterCreator(String creator);
}
