package com.github.lebezout.urlshortener.config;

import com.github.lebezout.urlshortener.utils.IdGenerator;
import com.github.lebezout.urlshortener.utils.IdValidator;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * The app configuration class
 * @author lebezout@gmail.com
 */
@Configuration
@ConfigurationProperties(prefix = "urlshortener")
public class Params {
    private int httpRedirectStatus;
    private int generatedIdLength;
    private Character[] idAlphabet;
    private String notFoundPage;
    private String[] forbiddenIds; // provided id
    private int providedIdMinLength;
    private int providedIdMaxLength;

    @PostConstruct
    public void setDefaultValues() {
        if (httpRedirectStatus < 300 || httpRedirectStatus > 399) {
            httpRedirectStatus = 301;
        }
        if (generatedIdLength < 2 || generatedIdLength > 15) {
            generatedIdLength = 5;
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
        if (forbiddenIds == null) {
            forbiddenIds = new String[0];
        }
        if (providedIdMinLength == 0) {
            providedIdMinLength = 2;
        }
        if (providedIdMaxLength == 0) {
            providedIdMaxLength = 15;
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

    /** @return The length of the generated id. Defaults to 5. Must in range of 2-10. */
    public int getGeneratedIdLength() {
        return generatedIdLength;
    }

    /** DO NOT USE - Only for Spring */
    public void setGeneratedIdLength(int idLength) {
        this.generatedIdLength = idLength;
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

    /** DO NOT USE - Only for Spring */
    public String[] getForbiddenIds() {
        return forbiddenIds;
    }
    /** DO NOT USE - Only for Spring */
    public void setForbiddenIds(String[] forbiddenIds) {
        this.forbiddenIds = forbiddenIds;
    }
    /** DO NOT USE - Only for Spring */
    public int getProvidedIdMinLength() {
        return providedIdMinLength;
    }
    /** DO NOT USE - Only for Spring */
    public void setProvidedIdMinLength(int providedIdMinLength) {
        this.providedIdMinLength = providedIdMinLength;
    }
    /** DO NOT USE - Only for Spring */
    public int getProvidedIdMaxLength() {
        return providedIdMaxLength;
    }
    /** DO NOT USE - Only for Spring */
    public void setProvidedIdMaxLength(int providedIdMaxLength) {
        this.providedIdMaxLength = providedIdMaxLength;
    }

    /**
     * For dependency injection of our IdGenerator
     * @return new instance
     */
    @Bean
    public IdGenerator getIdGenerator() {
        return new IdGenerator(idAlphabet);
    }

    /**
     * For dependency injection of our IdValidator
     * @return new instance
     */
    @Bean
    public IdValidator getIdValidator() {
        return new IdValidator(forbiddenIds, providedIdMinLength, providedIdMaxLength);
    }
}
