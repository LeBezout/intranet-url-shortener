package com.github.lebezout.urlshortener.domain;

import com.github.lebezout.urlshortener.error.CounterAlreadyExistsException;
import com.github.lebezout.urlshortener.error.CounterNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureMockMvc
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
        Assertions.assertEquals("JUNIT", service.getFromUrl("https://www.github.com").getCreator());
        Assertions.assertEquals("USER", service.getFromUrl("https://gitmoji.dev").getCreator());
    }
    @Test
    void test_getFromUrl_CounterNotFoundException() {
        Assertions.assertThrows(CounterNotFoundException.class, () -> service.getFromUrl("http://www.foobar.com"));
    }

    @Test
    void test_initCounter() {
        CounterDTO counter = service.initCounter("https://website.org", "junit");
        Assertions.assertNotNull(counter);
        Assertions.assertAll(
            () -> Assertions.assertEquals("junit", counter.getCreator()),
            () -> Assertions.assertEquals("https://website.org", counter.getUrl()),
            () -> Assertions.assertTrue(StringUtils.hasText(counter.getId()))
        );
    }
    @Test
    void test_initCounter_CounterAlreadyExistsException() {
        Assertions.assertThrows(CounterAlreadyExistsException.class, () -> service.initCounter("https://www.github.com", "junit"));
    }

    @Test
    void test_visit() {
        long initialCount = service.getFromID("AZERTY1234").getCounter();
        long count = service.visit("AZERTY1234");
        count = service.visit("AZERTY1234");
        Assertions.assertEquals(initialCount + 2 , count);
    }
}
