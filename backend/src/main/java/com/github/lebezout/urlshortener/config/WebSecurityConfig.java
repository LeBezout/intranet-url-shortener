package com.github.lebezout.urlshortener.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

/**
 * The security configuration for our rest api.
 * @author lebezout@gmail.com
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSecurityConfig.class);

    @Value("${urlshortener.ldap_user_search_filter}") String userSearchFilter;

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        super.configure(auth);
        LOGGER.info("CONFIG: LDAP base search is {}", userSearchFilter);
        auth.ldapAuthentication().userDnPatterns(userSearchFilter);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.csrf().disable();
        // allow redirects for everyone and get REST api
        http.authorizeRequests()
            .mvcMatchers("/favicon.ico").permitAll()
            .mvcMatchers("/manage/health").permitAll()
            .mvcMatchers("/manage/info").permitAll()
            .antMatchers("/redirect/*").permitAll()
            .antMatchers("/api/link/*/target").permitAll()
            .antMatchers(HttpMethod.GET, "/api/link").permitAll()
            .antMatchers(HttpMethod.GET, "/api/link/**").permitAll();
        // other REST api calls must be authenticated
        http.authorizeRequests().anyRequest().authenticated().and().httpBasic();
    }
}
