package com.github.lebezout.urlshortener.rest;

import com.github.lebezout.urlshortener.domain.LinkReportItem;
import com.github.lebezout.urlshortener.domain.ReportingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * The reporting resource REST controller.
 * @author lebezout@gmail.com
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/report")
public class ReportingResource {
    private final ReportingService service;

    @GetMapping
    public List<LinkReportItem> getAllAggregatedLinksAndCounters(@RequestParam(name = "creator", required = false) String creatorName) {
        LOGGER.info("getAllAggregatedLinksAndCounters Creator = {}", creatorName);
        return StringUtils.hasText(creatorName)
            ? service.getLinksReportsByCreator(creatorName)
            : service.getLinksReports();
    }
}
