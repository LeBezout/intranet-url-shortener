package com.github.lebezout.urlshortener.rest;

import com.github.lebezout.urlshortener.domain.LinkEntity;
import com.github.lebezout.urlshortener.domain.LinkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Controller
@RequestMapping("/check")
public class LinkCheck {
    private final LinkRepository repository;

    @GetMapping(value = "{idLink}")
    public String checkLink(@PathVariable("idLink") String idLink, Model model) {
        LOGGER.info("Find link {}", idLink);
        // Load link data
        Optional<LinkEntity> link = repository.findById(idLink);
        if (link.isPresent()) {
            LinkEntity entity = link.get();
            model.addAttribute("LINK_ID", entity.getId());
            model.addAttribute("LINK_TARGET", entity.getTarget());
            model.addAttribute("LINK_OWNER", entity.getCreator());
            model.addAttribute("LINK_ACCESS_COUNTER", entity.getAccessCounter());
            model.addAttribute("LINK_CREATION_DATE", DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").format(entity.getCreationDate()));
            return "link_checked";
        } else {
            model.addAttribute("LINK_ID", idLink);
            return "link_notfound";
        }
    }
}
