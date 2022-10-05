package com.sismics.docs.rest;

import com.google.common.io.ByteStreams;
import com.google.common.io.Resources;
import com.sismics.docs.core.util.DirectoryUtil;
import com.sismics.docs.core.constant.PermType;
import com.sismics.docs.core.dao.DocumentDao;
import com.sismics.docs.core.dao.dto.DocumentDto;
import com.sismics.util.filter.TokenBasedSecurityFilter;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.StreamDataBodyPart;
import org.joda.time.format.DateTimeFormat;
import org.junit.Assert;
import org.junit.Test;

import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;


public class TestRatingResource extends BaseJerseyTest {
	/**
	 * Tests whether a document correctly stores the ratings of a single user
	 */
	@Test
	public void testRateSingleUser() {
		// Login user
		clientUtil.createUser("user");
		String userToken = clientUtil.login("user");

		// Create a document with document1
		long create1Date = new Date().getTime();
		JsonObject json = target().path("/document").request()
		.cookie(TokenBasedSecurityFilter.COOKIE_NAME, userToken)
		.put(Entity.form(new Form()
			.param("title", "My super title document 1")
			.param("description", "My super description for document 1")
			.param("subject", "Subject document 1")
			.param("identifier", "Identifier document 1")
			.param("publisher", "Publisher document 1")
			.param("format", "Format document 1")
			.param("source", "Source document 1")
			.param("type", "Software")
			.param("coverage", "Greenland")
			.param("rights", "Public Domain")
			.param("language", "eng")
			.param("create_date", Long.toString(create1Date))), JsonObject.class);
		String document1Id = json.getString("id");

		// call put request to save ratings for document 
		json = target().path("/rate").request()
		.cookie(TokenBasedSecurityFilter.COOKIE_NAME, userToken)
		.put(Entity.form(new Form()
			.param("id", document1Id)
			.param("tech_rating", "2")
			.param("interpersonal_rating", "1")
			.param("fit_rating", "3")), JsonObject.class);
		String status = json.getString("status");

		Assert.assertEquals("ok", status);

		// Get document 1
		json = target().path("/document/" + document1Id).request()
						.cookie(TokenBasedSecurityFilter.COOKIE_NAME, userToken)
						.get(JsonObject.class);

		Assert.assertEquals("2.0", json.getString("tech_rating"));
		Assert.assertEquals("1.0", json.getString("interpersonal_rating"));
		Assert.assertEquals("3.0", json.getString("fit_rating"));
		Assert.assertEquals("1", json.getString("num_reviews"));
	}

	/**
	 * Tests whether a document correctly stores running average of ratings
	 */
	@Test
	public void testRateThreeUsers() {
		// Login user1
		clientUtil.createUser("user1");
		String userToken1 = clientUtil.login("user1");

		// Create a document with document1
		long create1Date = new Date().getTime();
		JsonObject json = target().path("/document").request()
		.cookie(TokenBasedSecurityFilter.COOKIE_NAME, userToken1)
		.put(Entity.form(new Form()
			.param("title", "My super title document 1")
			.param("description", "My super description for document 1")
			.param("subject", "Subject document 1")
			.param("identifier", "Identifier document 1")
			.param("publisher", "Publisher document 1")
			.param("format", "Format document 1")
			.param("source", "Source document 1")
			.param("type", "Software")
			.param("coverage", "Greenland")
			.param("rights", "Public Domain")
			.param("language", "eng")
			.param("create_date", Long.toString(create1Date))), JsonObject.class);
		String document1Id = json.getString("id");

		// call put request to save ratings for document 
		json = target().path("/rate").request()
		.cookie(TokenBasedSecurityFilter.COOKIE_NAME, userToken1)
		.put(Entity.form(new Form()
			.param("id", document1Id)
			.param("tech_rating", "2")
			.param("interpersonal_rating", "1")
			.param("fit_rating", "3")), JsonObject.class);
		String status = json.getString("status");

		Assert.assertEquals("ok", status);
		// logout user1
		clientUtil.logout(userToken1);

		// Login user2
		clientUtil.createUser("user2");
		String userToken2 = clientUtil.login("user2");

		// call put request to save ratings for document 
		json = target().path("/rate").request()
		.cookie(TokenBasedSecurityFilter.COOKIE_NAME, userToken2)
		.put(Entity.form(new Form()
			.param("id", document1Id)
			.param("tech_rating", "3")
			.param("interpersonal_rating", "4")
			.param("fit_rating", "1")), JsonObject.class);
		status = json.getString("status");

		Assert.assertEquals("ok", status);
		// logout user2
		clientUtil.logout(userToken2);

		// Login user3
		clientUtil.createUser("user3");
		String userToken3 = clientUtil.login("user3");

		// call put request to save ratings for document 
		json = target().path("/rate").request()
		.cookie(TokenBasedSecurityFilter.COOKIE_NAME, userToken3)
		.put(Entity.form(new Form()
			.param("id", document1Id)
			.param("tech_rating", "2")
			.param("interpersonal_rating", "3")
			.param("fit_rating", "2")), JsonObject.class);
		status = json.getString("status");

		Assert.assertEquals("ok", status);
		// logout user3
		clientUtil.logout(userToken3);

		userToken1 = clientUtil.login("user1");
		// Get document 1
		json = target().path("/document/" + document1Id).request()
		.cookie(TokenBasedSecurityFilter.COOKIE_NAME, userToken1)
		.get(JsonObject.class);

		// tech rating average = (2 + 3 + 2) / 3 = 2.33
		// interpersonal rating average = (1 + 4 + 3) / 3 = 2.667
		// fit rating average = (2 + 1 + 3) / 3 = 2.0

		Assert.assertEquals("3", json.getString("num_reviews"));
		Assert.assertTrue(Float.parseFloat(json.getString("tech_rating")) == (float)(2 + 3 + 2) / 3);
		Assert.assertTrue(Float.parseFloat(json.getString("interpersonal_rating")) == (float)(1 + 4 + 3) / 3);
		Assert.assertTrue(Float.parseFloat(json.getString("fit_rating")) == (float)(2 + 1 + 3) / 3);
	}

	@Test
	public void testRateDocumentNotFound() throws Exception {
		// Login user1
		clientUtil.createUser("user5");
		String userToken5 = clientUtil.login("user5");
		try {
			target().path("/rate").request()
			.cookie(TokenBasedSecurityFilter.COOKIE_NAME, userToken5)
			.put(Entity.form(new Form()
				.param("id", "non-existent")
				.param("tech_rating", "2")
				.param("interpersonal_rating", "1")
				.param("fit_rating", "3")), JsonObject.class);
			Assert.fail("no exception thrown when document does not exist");
		} catch (NotFoundException e) {

		}
	}

	@Test
	public void testPercentRating() throws Exception {
		clientUtil.createUser("user6");
        String userToken6 = clientUtil.login("user6");

        // Create a document with document1
		long create1Date = new Date().getTime();
		JsonObject json = target().path("/document").request()
			.cookie(TokenBasedSecurityFilter.COOKIE_NAME, userToken6)
			.put(Entity.form(new Form()
			.param("title", "My super title document 1")
			.param("description", "My super description for document 1")
			.param("subject", "Subject document 1")
			.param("identifier", "Identifier document 1")
			.param("publisher", "Publisher document 1")
			.param("format", "Format document 1")
			.param("source", "Source document 1")
			.param("type", "Software")
			.param("coverage", "Greenland")
			.param("rights", "Public Domain")
			.param("language", "eng")
			.param("create_date", Long.toString(create1Date))), JsonObject.class);
		String document1Id = json.getString("id");

		// json = target().path("/rate/" + document1Id).request()
		// .cookie(TokenBasedSecurityFilter.COOKIE_NAME, userToken6)
		// .get(JsonObject.class);

		// JsonNumber percentage = json.getJsonNumber("percentage_rating");
		// Assert.assertEquals(percentage, 0.0);

		// Login user7
		clientUtil.createUser("user7");
		String userToken7 = clientUtil.login("user7");

		// call put request to save ratings for document 
		json = target().path("/rate").request()
		.cookie(TokenBasedSecurityFilter.COOKIE_NAME, userToken7)
		.put(Entity.form(new Form()
			.param("id", document1Id)
			.param("tech_rating", "3")
			.param("interpersonal_rating", "4")
			.param("fit_rating", "1")), JsonObject.class);
		String status = json.getString("status");

		json = target().path("/rate/" + document1Id).request()
		.cookie(TokenBasedSecurityFilter.COOKIE_NAME, userToken6)
		.get(JsonObject.class);

		JsonNumber percentage1 = json.getJsonNumber("percentage_rating");
		Assert.assertEquals(percentage1, 0.25);

		Assert.assertEquals("ok", status);
		// logout user7
		clientUtil.logout(userToken7);

	}
}
