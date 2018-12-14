package com.github.lebezout.urlshortener.domain;

import com.github.lebezout.urlshortener.error.LinkNotFoundException;
import com.github.lebezout.urlshortener.error.NotLinkOwnerException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureMockMvc
@Sql("classpath:/data-test.sql")
@Transactional
public class LinkServiceTest {
    @Autowired
    private LinkService service;

    @Test
    public void test_getByID() {
        LinkDTO link1 = service.getByID("1234156");
        LinkDTO link2 = service.getByID("1234156");
        Assert.assertSame(link1, link2);
    }

    @Test
    public void test_addNewLink() {
        NewLinkDTO dto = new NewLinkDTO();
        dto.setId("provided");
        dto.setTarget("URL");
        LinkDTO inserted = service.addNewLink(dto, "JUNIT");
        Assert.assertEquals("JUNIT", inserted.getCreator());
        Assert.assertEquals("provided", inserted.getId());

        LinkDTO newLink = service.getByID("provided");
        Assert.assertEquals("JUNIT", newLink.getCreator());
        Assert.assertEquals("provided", newLink.getId());
        //Assert.assertSame(inserted, newLink);
    }

    @Test
    public void test_findByCreator() {
        List<LinkDTO> result =  service.findByCreator("JUNIT");
        Assert.assertEquals(2, result.size());
    }

    @Test
    public void test_findByCriteria() {
        List<LinkDTO> result =  service.findByCriteria(
            "JUNIT",
            LocalDateTime.of(2018, 11, 10, 0, 0),
            LocalDateTime.of(2018, 11, 12, 0, 0));
        Assert.assertEquals(1, result.size());
    }

    @Test
    public void test_updateLink() {
        LinkDTO link = service.getByID("ABCDEF");
        Assert.assertEquals("JUNIT", link.getCreator());
        Assert.assertEquals("http://github.com", link.getTarget());
        Assert.assertFalse(link.isPrivateLink());
        link.setTarget("TEST");
        link.setPrivateLink(true);

        service.updateLink(link, "JUNIT");

        link = service.getByID("ABCDEF");
        Assert.assertEquals("JUNIT", link.getCreator());
        Assert.assertEquals("TEST", link.getTarget());
        Assert.assertTrue(link.isPrivateLink());
    }
    @Test(expected = IllegalArgumentException.class)
    public void test_updateLink_NoID() {
        service.updateLink(null, "JUNIT");
    }
    @Test(expected = IllegalArgumentException.class)
    public void test_updateLink_NoCreator() {
        service.updateLink(new LinkDTO(), null);
    }
    @Test(expected = NotLinkOwnerException.class)
    public void test_updateLink_NotOwner() {
        service.updateLink(service.getByID("ABCDEF"), "TEST");
    }

    @Test(expected = LinkNotFoundException.class)
    public void test_deleteLink() {
        try {
            service.deleteLink("1234156", "TEST");
        } catch (Exception e) {
            Assert.fail(e.toString() + " not expected !");
        }

        service.getByID("1234156");
        System.out.println("--------------???");
    }
    @Test(expected = IllegalArgumentException.class)
    public void test_deleteLink_NoID() {
        service.deleteLink(null, "JUNIT");
    }
    @Test(expected = IllegalArgumentException.class)
    public void test_deleteLink_NoCreator() {
        service.deleteLink("ID", null);
    }
    @Test
    public void test_deleteLink_NotOwner() {
        try {
            service.deleteLink("ABCDEF", "TEST");
            Assert.fail("Expected NotLinkOwnerException");
        } catch (NotLinkOwnerException e) {
            // OK
            service.getByID("ABCDEF");
        }
    }
}
