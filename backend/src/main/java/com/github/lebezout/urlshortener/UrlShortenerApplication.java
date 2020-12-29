package com.github.lebezout.urlshortener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * The main app class
 * @author lebezout@gmail.com
 */
@SpringBootApplication
@EnableCaching
public class UrlShortenerApplication extends SpringBootServletInitializer {
    @Controller
    static class FaviconController {
        @GetMapping("favicon.ico")
        @ResponseBody
        void returnNoFavicon() {
            // disable favicon without 404
        }
    }

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(UrlShortenerApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(UrlShortenerApplication.class, args);
	}
}
