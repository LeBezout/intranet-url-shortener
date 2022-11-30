package com.github.lebezout.urlshortener.domain;

import com.github.lebezout.urlshortener.error.CounterAlreadyExistsException;
import com.github.lebezout.urlshortener.error.CounterNotFoundException;
import com.github.lebezout.urlshortener.error.NotLinkOwnerException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Sql("classpath:/data-test-counter.sql")
@Transactional
class CounterServiceTest {
    @Autowired
    private CounterService service;

    @Test
    void test_getFromID() {
        Assertions.assertEquals("JUNIT", service.getFromID("AZERTY1234").getCreator());
        Assertions.assertEquals("USER", service.getFromID("FOOBAR6789").getCreator());
    }
    @Test
    void test_getFromID_CounterNotFoundException() {
        Assertions.assertThrows(CounterNotFoundException.class, () -> service.getFromID("foobar"));
    }

    @Test
    void test_getFromUrl() {
        Assertions.assertEquals("JUNIT", service.getFromUrl("https://github.com").getCreator());
        Assertions.assertEquals("USER", service.getFromUrl("https://gitmoji.dev").getCreator());
    }
    @Test
    void test_getFromUrl_CounterNotFoundException() {
        Assertions.assertThrows(CounterNotFoundException.class, () -> service.getFromUrl("http://www.foobar.com"));
    }

    @Test
    void test_findByCreator() {
        List<CounterDTO> counters = service.findByCreator("JUNIT");
        Assertions.assertTrue(counters.size() > 1);
        counters.forEach(c -> {
            if (!c.getCreator().equals("JUNIT")) {
                Assertions.fail("Expected JUNIT creator only");
            }
        });
    }

    @Test
    void test_initCounter() {
        CounterDTO counter = service.initCounter("https://website.org", "junit");
        Assertions.assertNotNull(counter);
        Assertions.assertAll(
            () -> Assertions.assertEquals("junit", counter.getCreator()),
            () -> Assertions.assertEquals("https://website.org", counter.getUrl()),
            () -> Assertions.assertTrue(StringUtils.hasText(counter.getId())),
            () -> Assertions.assertNull(counter.getLastVisitedDate())
        );
    }
    @Test
    void test_initCounter_CounterAlreadyExistsException() {
        Assertions.assertThrows(CounterAlreadyExistsException.class, () -> service.initCounter("https://github.com", "junit"));
    }

    @Test
    void test_visit() {
        long initialCount = service.getFromID("AZERTY1234").getCounter();
        long count1 = service.visit("AZERTY1234");
        long count2 = service.visit("AZERTY1234");
        Assertions.assertAll(
            () -> Assertions.assertNotEquals(count1, initialCount),
            () -> Assertions.assertNotEquals(count1, count2),
            () -> Assertions.assertEquals(initialCount + 2 , count2),
            () -> Assertions.assertNotNull(service.getFromID("AZERTY1234").getLastVisitedDate())
        );
    }

    @Test
    void test_resetCounter_owner() {
        CounterDTO counter = service.resetCounter("FOOBAR6789", "USER");
        Assertions.assertAll(
            () -> Assertions.assertEquals(0, counter.getCounter()),
            () -> Assertions.assertNull(counter.getLastVisitedDate())
        );
    }
    @Test
    void test_resetCounter_not_owner() {
        Assertions.assertThrows(NotLinkOwnerException.class, () -> service.resetCounter("FOOBAR6789", "OTHER"));
    }

    @Test
    void test_deleteCounter_owner() {
        service.deleteCounter("DELETEME", "JUNIT");
        Assertions.assertThrows(CounterNotFoundException.class, () -> service.getFromID("DELETEME"));
        Assertions.assertTrue(service.getAllSnapshots("DELETEME").isEmpty());
    }
    @Test
    void test_deleteCounter_not_owner() {
        Assertions.assertThrows(NotLinkOwnerException.class, () -> service.deleteCounter("FOOBAR6789", "OTHER"));
    }

    @Test
    void test_takeSnapshot_CounterNotFoundException() {
        Assertions.assertThrows(CounterNotFoundException.class, () -> service.takeSnapshot("invalidid", "JUNIT"));
    }
    @Test
    void test_takeSnapshot_OK() {
        long currentCounterValue = service.takeSnapshot("DEMO$123@6", "JUNIT");
        Assertions.assertTrue(currentCounterValue >= 66);
    }

    @Test
    void test_getAllSnapshots() throws InterruptedException {
        String counterId = "AZERTY1234";
        service.takeSnapshot(counterId, "JUNIT1");
        Thread.sleep(350);
        service.takeSnapshot(counterId, "JUNIT2");
        List<CounterSnapshotDTO> dtos = service.getAllSnapshots(counterId);
        Assertions.assertTrue(dtos.size() >= 2);
    }

    @Test
    @Sql("classpath:/data-test-countersnapshot.sql")
    void test_getAllSnapshotsBetween() {
        String counterId = "AZERTY1234";
        LocalDate start = LocalDate.of(2018, 1, 1);
        LocalDate end = LocalDate.of(2019, 12, 31);
        List<CounterSnapshotDTO> dtos = service.getAllSnapshotsBetween(counterId, start, end);
        Assertions.assertTrue(dtos.size() >= 6);
    }
}
