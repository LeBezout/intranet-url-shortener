package com.github.lebezout.urlshortener.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class OpenApiConfig {
    @Bean
    public Docket productApi() {
        Docket dk = new Docket(DocumentationType.SWAGGER_2).select()
            .apis(RequestHandlerSelectors.basePackage("com.github.lebezout.urlshortener.rest"))
            .build();
        dk.ignoredParameterTypes(org.springframework.core.io.Resource.class, java.io.InputStream.class);
        return dk;
    }
}
