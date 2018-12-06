package com.github.lebezout.urlshortener;

import com.github.lebezout.urlshortener.config.Params;
import com.github.lebezout.urlshortener.utils.IdGenerator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.stream.Stream;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UrlShortenerApplicationTests {
	private static final Logger LOGGER = LoggerFactory.getLogger(UrlShortenerApplicationTests.class);

	@Autowired
	private Params params;

	@Autowired
	private IdGenerator generator;

	@Test
	public void contextLoads() {
	}

	@Test
	public void test_config_Params() {
		LOGGER.debug("ID LENGTH : " + params.getIdLength());
		Assert.assertEquals(6, params.getIdLength());
		LOGGER.debug("REDIRECT STATUS CODE : " + params.getHttpRedirectStatus());
		Assert.assertEquals(307, params.getHttpRedirectStatus());
		LOGGER.debug("NOT FOUND PAGE : " + params.getNotFoundPage());
		Assert.assertEquals("http://localhost:8080/demo/404.html", params.getNotFoundPage());
		LOGGER.debug("ALPHABET ID LENGTH : " + params.getIdAlphabet().length);
		Assert.assertEquals(36, params.getIdAlphabet().length);
		Stream.of(params.getIdAlphabet()).forEach(c -> LOGGER.debug(String.valueOf(c)));
		String sampleId = generator.generate(params.getIdLength());
		LOGGER.debug("SAMPLE ID : " + sampleId);
		Assert.assertEquals(6, sampleId.length());
	}
}
