package com.github.lebezout.urlshortener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * The main app class
 * @author lebezout@gmail.com
 */
@SpringBootApplication
@EnableCaching
@EnableSwagger2
public class UrlShortenerApplication extends SpringBootServletInitializer {
    @Controller
    static class FaviconController {
        @GetMapping("favicon.ico")
        @ResponseBody
        void returnNoFavicon() {
            // disable favicon without 404
        }
    }

    @Controller
    static class CustomErrorController implements ErrorController {
        @RequestMapping("/error")
        public String handleError() {
            return "error.html";
        }
        @RequestMapping("/404")
        public String handleNotFound() {
            return "404.html";
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
