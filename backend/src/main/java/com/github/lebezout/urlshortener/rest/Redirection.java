package com.github.lebezout.urlshortener.rest;

import com.github.lebezout.urlshortener.config.Params;
import com.github.lebezout.urlshortener.domain.LinkEntity;
import com.github.lebezout.urlshortener.domain.LinkRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * The redirection controller class.
 * @author lebezout@gmail.com
 */
@RestController
@RequestMapping("/redirect")
public class Redirection {
    private static final Logger LOGGER = LoggerFactory.getLogger(Redirection.class);
    private final LinkRepository repository;
    private final Params params;

    /**
     * Constructor for DI
     * @param config the autowired config
     * @param repo autowired repository instance
     */
    public Redirection(Params config, LinkRepository repo) {
        params = config;
        repository = repo;
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("CONFIG: Will redirect with status {}", params.getHttpRedirectStatus());
            LOGGER.info("CONFIG: Else will redirect default to {}", params.getNotFoundPage());
        }
    }

    /**
     * Do the expected redirection
     * @param idLink a valid ID in the database
     * @return Response
     */
    @Transactional
    @GetMapping(path = "{idLink}")
    public ResponseEntity redirectTo(@PathVariable("idLink") String idLink) {
        LOGGER.info("Redirect to {}", idLink);
        // Find the link data
        Optional<LinkEntity> link = repository.findById(idLink);
        String targetUrl;//
        if (link.isPresent()) {
            LinkEntity entity = link.get();
            targetUrl = entity.getTarget();
            // increment counter for statistics purpose
            long accessed = entity.getAccessCounter() + 1;
            LOGGER.info("Found link, redirect to {}, accessed {} times", targetUrl, accessed);
            entity.setAccessCounter(accessed);
            repository.save(entity);
        } else {
            targetUrl = params.getNotFoundPage();
            LOGGER.warn("Link not found, redirect to {}", targetUrl);
        }
        return ResponseEntity.status(params.getHttpRedirectStatus()).header(HttpHeaders.LOCATION, targetUrl).build();
    }
}
