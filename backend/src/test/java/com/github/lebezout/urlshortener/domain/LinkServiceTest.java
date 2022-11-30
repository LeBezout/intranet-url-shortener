package com.github.lebezout.urlshortener.domain;

import com.github.lebezout.urlshortener.error.LinkNotFoundException;
import com.github.lebezout.urlshortener.error.NotLinkOwnerException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Sql("classpath:/data-test-link.sql")
@Transactional
class LinkServiceTest {
    @Autowired
    private LinkService service;

    @Test
    void test_getByID() {
        LinkDTO link1 = service.getByID("1234156");
        LinkDTO link2 = service.getByID("1234156");
        Assertions.assertSame(link1, link2);
    }

    @Test
    void test_addNewLink() {
        NewLinkDTO dto = new NewLinkDTO();
        dto.setId("provided");
        dto.setTarget("URL");
        LinkDTO inserted = service.addNewLink(dto, "JUNIT");
        Assertions.assertEquals("JUNIT", inserted.getCreator());
        Assertions.assertEquals("provided", inserted.getId());

        LinkDTO newLink = service.getByID("provided");
        Assertions.assertEquals("JUNIT", newLink.getCreator());
        Assertions.assertEquals("provided", newLink.getId());
        //Assertions.assertSame(inserted, newLink);
    }

    @Test
    void test_addNewLink_exists_no_ID_provided() {
        // attempt to create 'https://github.com' defined in data-sql.test (author=JUNIT, creation_counter=4)
        NewLinkDTO dto = new NewLinkDTO();
        dto.setTarget("https://github.com");
        LinkDTO existing = service.addNewLink(dto, "OTHER1");
        Assertions.assertEquals("JUNIT", existing.getCreator()); // not OTHER1
        Assertions.assertEquals(4 + 1, existing.getCreationCounter());
        existing = service.addNewLink(dto, "OTHER2");
        Assertions.assertEquals("JUNIT", existing.getCreator()); // not OTHER2
        Assertions.assertEquals(4 + 2, existing.getCreationCounter());
    }
    @Test
    void test_addNewLink_exists_ID_is_provided() {
        // attempt to create 'https://github.com' defined in data-sql.test (author=JUNIT, creation_counter=4), with a specific ID
        NewLinkDTO dto = new NewLinkDTO();
        dto.setId("github");
        dto.setTarget("https://github.com");
        LinkDTO existing = service.addNewLink(dto, "OTHER1");
        Assertions.assertEquals("OTHER1", existing.getCreator()); // not JUNIT
        Assertions.assertEquals(1, existing.getCreationCounter()); // not 4+1
    }

    @Test
    void test_findByCreator() {
        List<LinkDTO> results = service.findByCreator("JUNIT");
        Assertions.assertEquals(3, results.size());
    }

    @Test
    void test_findByCriteria() {
        List<LinkDTO> results = service.findByCriteria(
            "JUNIT",
            LocalDateTime.of(2018, 11, 10, 0, 0),
            LocalDateTime.of(2018, 11, 12, 0, 0));
        Assertions.assertEquals(1, results.size());
    }

    @Test
    void test_findByCriteria_NoCreator() {
        List<LinkDTO> results = service.findByCriteria(
            "",
            LocalDateTime.of(2018, 11, 10, 0, 0),
            LocalDateTime.of(2018, 11, 12, 0, 0));
        Assertions.assertEquals(2, results.size());
    }

    @Test
    void test_findByTargetUrl() {
        List<LinkDTO> results = service.findByTargetUrl("https://notfound.com");
        Assertions.assertTrue(results.isEmpty());
        results = service.findByTargetUrl("https://github.com");
        Assertions.assertEquals(1, results.size());
    }

    @Test
    void test_updateLink() {
        LinkDTO link = service.getByID("ABCDEF");
        Assertions.assertEquals("JUNIT", link.getCreator());
        Assertions.assertEquals("https://github.com", link.getTarget());
        Assertions.assertFalse(link.isPrivateLink());
        link.setTarget("TEST");
        link.setPrivateLink(true);

        service.updateLink(link, "JUNIT");

        link = service.getByID("ABCDEF");
        Assertions.assertEquals("JUNIT", link.getCreator());
        Assertions.assertEquals("TEST", link.getTarget());
        Assertions.assertTrue(link.isPrivateLink());
    }
    @Test
    void test_updateLink_NoID() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.updateLink(null, "JUNIT"));
    }
    @Test
    void test_updateLink_NoCreator() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.updateLink(new LinkDTO(), null));
    }
    @Test
    void test_updateLink_NotOwner() {
        Assertions.assertThrows(NotLinkOwnerException.class, () -> service.updateLink(service.getByID("ABCDEF"), "TEST"));
    }

    @Test
    void test_deleteLink() {
        service.deleteLink("1234156", "TEST"); // delete ... and then try to get it
        Assertions.assertThrows(LinkNotFoundException.class, () -> service.getByID("1234156"));
    }
    @Test
    void test_deleteLink_NoID() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.deleteLink(null, "JUNIT"));
    }
    @Test
    void test_deleteLink_NoCreator() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> service.deleteLink("ID", null));
    }
    @Test
    void test_deleteLink_NotOwner() {
        try {
            service.deleteLink("ABCDEF", "TEST");
            Assertions.fail("Expected NotLinkOwnerException");
        } catch (NotLinkOwnerException e) {
            // OK
            service.getByID("ABCDEF");
        }
    }
}
