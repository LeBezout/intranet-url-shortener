package com.github.lebezout.urlshortener;

import com.github.lebezout.urlshortener.config.Params;
import com.github.lebezout.urlshortener.utils.IdGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.Stream;

@SpringBootTest("spring.liquibase.enabled=false")
class UrlShortenerApplicationTests {
	private static final Logger LOGGER = LoggerFactory.getLogger(UrlShortenerApplicationTests.class);

	@Autowired
	private Params params;

	@Autowired
	private IdGenerator generator;

    @Test
    void should_load_the_configuration() {
        LOGGER.debug("ID LENGTH : " + params.getIdLength());
        LOGGER.debug("REDIRECT STATUS CODE : " + params.getHttpRedirectStatus());
        LOGGER.debug("NOT FOUND PAGE : " + params.getNotFoundPage());
        LOGGER.debug("ALPHABET ID LENGTH : " + params.getIdAlphabet().length);

        Assertions.assertAll(
            () -> Assertions.assertEquals(6, params.getIdLength()),
            () -> Assertions.assertEquals(307, params.getHttpRedirectStatus()),
            () -> Assertions.assertEquals("http://localhost:8080/demo/404.html", params.getNotFoundPage()),
            () -> Assertions.assertEquals(36, params.getIdAlphabet().length)
        );

        Stream.of(params.getIdAlphabet()).forEach(c -> LOGGER.debug(String.valueOf(c)));
        String sampleId = generator.generate(params.getIdLength());
        LOGGER.debug("SAMPLE ID : " + sampleId);
        Assertions.assertEquals(6, sampleId.length());
    }
}
