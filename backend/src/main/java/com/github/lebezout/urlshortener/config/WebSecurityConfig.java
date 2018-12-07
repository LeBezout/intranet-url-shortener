package com.github.lebezout.urlshortener.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * The security configuration for our rest api.
 * @author lebezout@gmail.com
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Value("${urlshortener.ldap_user_search_filter}") String userSearchFilter;

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.ldapAuthentication().userDnPatterns(userSearchFilter);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        // allow redirects for everyone and get REST api
        http.authorizeRequests()
                .antMatchers("/redirect/*").permitAll()
                .antMatchers("/api/link/*/target").permitAll()
            .antMatchers(HttpMethod.GET, "/api/link").permitAll()
            .antMatchers(HttpMethod.GET, "/api/link/**").permitAll();
        // other REST api calls must be authenticated
        http.authorizeRequests().anyRequest().authenticated().and().httpBasic();
    }
}
