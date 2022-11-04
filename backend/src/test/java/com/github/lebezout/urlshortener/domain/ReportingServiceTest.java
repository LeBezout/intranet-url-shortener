package com.github.lebezout.urlshortener.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Sql("classpath:/data-test-link.sql")
@Sql("classpath:/data-test-counter.sql")
@Transactional
class ReportingServiceTest {
    @Autowired
    private ReportingService service;

    @Test
    void test_getLinksReports() {
        List<LinkReportItem> aggregatedItems = service.getLinksReports();
        aggregatedItems.forEach(System.out::println);
        Assertions.assertEquals(2, aggregatedItems.size(), "Expected https://gitmoji.dev & https://github.com");
    }

    @Test
    void test_getLinksReportsByCounterCreator() {
        List<LinkReportItem> aggregatedItems = service.getLinksReportsByCreator("JUNIT");
        aggregatedItems.forEach(System.out::println);
        Assertions.assertEquals(1, aggregatedItems.size(), "Expected 1 link");
        LinkReportItem item = aggregatedItems.get(0);
        Assertions.assertEquals("AZERTY1234", item.getCounterId(), "Expected counterId=AZERTY1234");
        Assertions.assertEquals("JUNIT", item.getCounterCreator(), "Expected counterCreator=JUNIT");
    }
}
