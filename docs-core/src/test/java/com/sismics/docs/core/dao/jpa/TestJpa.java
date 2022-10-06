package com.sismics.docs.core.dao.jpa;

import com.sismics.docs.BaseTransactionalTest;
import com.sismics.docs.core.dao.DocumentDao;
import com.sismics.docs.core.dao.UserDao;
import com.sismics.docs.core.model.jpa.Document;
import com.sismics.docs.core.model.jpa.User;
import com.sismics.docs.core.util.TransactionUtil;
import com.sismics.docs.core.util.authentication.InternalAuthenticationHandler;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the persistance layer.
 * 
 * @author jtremeaux
 */
public class TestJpa extends BaseTransactionalTest {
    @Test
    public void testJpa() throws Exception {
        // Create a user
        UserDao userDao = new UserDao();
        User user = new User();
        user.setUsername("username");
        user.setPassword("12345678");
        user.setEmail("toto@docs.com");
        user.setRoleId("admin");
        user.setStorageQuota(10L);
        String id = userDao.create(user, "me");

        // Create a new document
        DocumentDao documentDao = new DocumentDao(); 
        Document document = new Document();
        document.setTitle("test1");
        document.setCreateDate(new Date());
        document.setLanguage("english");
        document.setUserId(id);
        document.setAvgTech("10");
        document.setAvgInterpersonal("9");
        document.setAvgFit("8");
        document.setNumReviews(0);
        documentDao.create(document, id);
        
        TransactionUtil.commit();

        // Search a user by his ID
        user = userDao.getById(id);
        Assert.assertNotNull(user);
        Assert.assertEquals("toto@docs.com", user.getEmail());

        // Search a document by user ID
        Assert.assertNotNull(document);
        Assert.assertEquals("10", document.getAvgTech());
        Assert.assertEquals("9", document.getAvgInterpersonal());
        Assert.assertEquals("8", document.getAvgFit());
        Assert.assertTrue(0 == document.getNumReviews());

        // Authenticate using the database
        Assert.assertNotNull(new InternalAuthenticationHandler().authenticate("username", "12345678"));
    }
}