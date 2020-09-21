package com.github.lebezout.urlshortener;

import com.github.lebezout.urlshortener.config.Params;
import com.github.lebezout.urlshortener.utils.IdGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.stream.Stream;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UrlShortenerApplicationTests {
	private static final Logger LOGGER = LoggerFactory.getLogger(UrlShortenerApplicationTests.class);

	@Autowired
	private Params params;

	@Autowired
	private IdGenerator generator;

	@Test
	void test_config_Params() {
		LOGGER.debug("ID LENGTH : " + params.getIdLength());
		Assertions.assertEquals(6, params.getIdLength());
		LOGGER.debug("REDIRECT STATUS CODE : " + params.getHttpRedirectStatus());
        Assertions.assertEquals(307, params.getHttpRedirectStatus());
		LOGGER.debug("NOT FOUND PAGE : " + params.getNotFoundPage());
        Assertions.assertEquals("http://localhost:8080/demo/404.html", params.getNotFoundPage());
		LOGGER.debug("ALPHABET ID LENGTH : " + params.getIdAlphabet().length);
        Assertions.assertEquals(36, params.getIdAlphabet().length);
		Stream.of(params.getIdAlphabet()).forEach(c -> LOGGER.debug(String.valueOf(c)));
		String sampleId = generator.generate(params.getIdLength());
		LOGGER.debug("SAMPLE ID : " + sampleId);
        Assertions.assertEquals(6, sampleId.length());
	}
}
