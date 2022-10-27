package com.github.lebezout.urlshortener.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class ReportingService {
    private final ReportingRepository repository;

    /**
     * Match the shortened links with counters by their URL and return the list.
     * @return list of POJO
     */
    public List<LinkReportItem> getLinksReports() {
        return repository.getLinksReports();
    }

    /**
     * Match the shortened links with counters by their URL of the specified creator and return the list.
     * @param creator name of the creator
     * @return list of POJO
     */
    public List<LinkReportItem> getLinksReportsByCreator(String creator) {
        return repository.getLinksReportsByCounterCreator(creator);
    }
}
