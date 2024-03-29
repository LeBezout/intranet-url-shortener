package com.github.lebezout.urlshortener.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.servlet.HandlerExceptionResolver;

/**
 * The security configuration for our rest api.
 * @author lebezout@gmail.com
 */
@Slf4j
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Value("${urlshortener.ldap_user_search_filter}") String userSearchFilter;
    @Value("${spring.ldap.urls}") String ldapUrl;
    @Value("${spring.ldap.username}") String ldapUser;
    @Value("${spring.ldap.password}") String ldapPwd;
    @Value("${spring.ldap.base}") String ldapBase;

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver handlerExceptionResolver;

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        LOGGER.info("CONFIG: LDAP URL is {}, Base is {}, User is {}, search filter is {}", ldapUrl, ldapBase, ldapUser, userSearchFilter);
        auth.ldapAuthentication()
            .userSearchBase(ldapBase)
            .userSearchFilter(userSearchFilter)
            .groupSearchBase(ldapBase)
            .contextSource()
                .url(ldapUrl)
                .managerDn(ldapUser)
                .managerPassword(ldapPwd);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        LOGGER.info("CONFIG: HandlerExceptionResolver is {}", handlerExceptionResolver);
        final String apiURLAntMatcher = "/api/**";
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and().csrf().disable()
            .authorizeRequests()
            .antMatchers(HttpMethod.GET, "/api/links/owned").fullyAuthenticated()
            .antMatchers(HttpMethod.POST, apiURLAntMatcher).fullyAuthenticated()
            .antMatchers(HttpMethod.PUT, apiURLAntMatcher).fullyAuthenticated()
            .antMatchers(HttpMethod.DELETE, apiURLAntMatcher).fullyAuthenticated()
            .anyRequest().permitAll()
            .and().httpBasic().authenticationEntryPoint((request, response, exception) -> {
                LOGGER.info("authenticationEntryPoint - handle {}", exception.getClass());
                handlerExceptionResolver.resolveException(request, response, null, exception);
            })
        ;
    }
}
