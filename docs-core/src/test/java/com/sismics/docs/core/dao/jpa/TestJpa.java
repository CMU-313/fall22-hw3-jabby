package com.sismics.docs.core.dao.jpa;

import com.sismics.docs.BaseTransactionalTest;
import com.sismics.docs.core.dao.DocumentDao;
import com.sismics.docs.core.dao.UserDao;
import com.sismics.docs.core.model.jpa.Document;
import com.sismics.docs.core.model.jpa.User;
import com.sismics.docs.core.util.TransactionUtil;
import com.sismics.docs.core.util.authentication.InternalAuthenticationHandler;
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
        document.setAvg_tech("10");
        document.setAvg_interpersonal("9");
        document.setAvg_fit("8");
        document.setNum_reviews(0);
        
        TransactionUtil.commit();

        // Search a user by his ID
        user = userDao.getById(id);
        Assert.assertNotNull(user);
        Assert.assertEquals("toto@docs.com", user.getEmail());

        // Search a document by user ID
        document = documentDao.findByUserId(id).get(0);
        Assert.assertNotNull(document);
        Assert.assertEquals("10", document.getAvg_tech());
        Assert.assertEquals("9", document.getAvg_interpersonal());
        Assert.assertEquals("8", document.getAvg_fit());
        Assert.assertTrue(0 == document.getNum_reviews());

        // Authenticate using the database
        Assert.assertNotNull(new InternalAuthenticationHandler().authenticate("username", "12345678"));
    }
}