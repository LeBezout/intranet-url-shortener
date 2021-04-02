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

    @Value("${urlshortener.ldap_user_dn_patterns}") String userDnPatterns;
    @Value("${urlshortener.ldap_user_search_filter}") String userSearchFilter;
    @Value("${spring.ldap.urls}") String ldapUrl;
    @Value("${spring.ldap.base}") String ldapBase;

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        LOGGER.info("CONFIG: LDAP base search is {}, DN patterns is {}, URL is {}{}", userSearchFilter, userDnPatterns, ldapUrl, ldapBase);
        auth.ldapAuthentication()
            .userDnPatterns(userDnPatterns)
            .userSearchFilter(userSearchFilter)
            .contextSource().url(ldapUrl + ldapBase)
        ;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        final String apiURLAntMatcher = "/api/**";
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and().csrf().disable()
            .authorizeRequests()
            .antMatchers(HttpMethod.POST, apiURLAntMatcher).fullyAuthenticated()
            .antMatchers(HttpMethod.PUT, apiURLAntMatcher).fullyAuthenticated()
            .antMatchers(HttpMethod.DELETE, apiURLAntMatcher).fullyAuthenticated()
            .anyRequest().permitAll()
            .and().httpBasic()
        ;
    }
}
