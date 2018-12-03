package com.github.lebezout.urlshortener.config;

import com.github.lebezout.urlshortener.domain.LinkEntity;
import com.github.lebezout.urlshortener.utils.IdGenerator;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * The app configuration class
 * @author lebezout@gmail.com
 */
@Configuration
@ConfigurationProperties(prefix = "urlshortener")
@EnableConfigurationProperties
public class Params {
    private int httpRedirectStatus;
    private int idLength;
    private Character[] idAlphabet;
    private String notFoundPage;

    @PostConstruct
    public void setDefaultValues() {
        if (httpRedirectStatus < 300 || httpRedirectStatus > 399) {
            httpRedirectStatus = 301;
        }
        if (idLength < 2 || idLength > 10) {
            idLength = 5;
        }
        if (idAlphabet == null) {
            idAlphabet = new Character[] {
                    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                    'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M','N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
                    'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
            };
        }
        if (notFoundPage == null || notFoundPage.isEmpty()) {
            notFoundPage = "/not_found.html";
        }
    }

    /** @return The HTTP status to use for the redirection. Defaults to 301. Must in range of 300-399. */
    public int getHttpRedirectStatus() {
        return httpRedirectStatus;
    }

    /** DO NOT USE - Only for Spring */
    public void setHttpRedirectStatus(int httpRedirectStatus) {
        this.httpRedirectStatus = httpRedirectStatus;
    }

    /** @return The length of the genrated id. Defaults to 5. Must in range of 2-10. */
    public int getIdLength() {
        return idLength;
    }

    /** DO NOT USE - Only for Spring */
    public void setIdLength(int idLength) {
        this.idLength = idLength;
    }

    /** @return The alphabet to use for the ID generation. Defaults to digits+lower+upper. */
    public Character[] getIdAlphabet() {
        return idAlphabet;
    }

    /** DO NOT USE - Only for Spring */
    public void setIdAlphabet(Character[] idAlphabet) {
        this.idAlphabet = idAlphabet;
    }

    /** @return The page to use in case of bad id. Defaults to the static/not_found.html page */
    public String getNotFoundPage() {
        return notFoundPage;
    }

    /** DO NOT USE - Only for Spring */
    public void setNotFoundPage(String notFoundPage) {
        this.notFoundPage = notFoundPage;
    }

    /**
     * For dependency injection of our IdGenerator
     * @return new instance
     */
    @Bean
    public IdGenerator getIdGenerator() {
        return new IdGenerator(idAlphabet);
    }
}
