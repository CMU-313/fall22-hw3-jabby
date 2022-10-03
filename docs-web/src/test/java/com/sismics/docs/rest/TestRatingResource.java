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
import javax.json.JsonObject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;


public class TestRatingResource extends BaseJerseyTest{
	/**
	 * Tests whether a document correctly stores the ratings of a single user
	 */
	@Test
	public void testRateSingleUser() {
		// Login document1
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
		System.out.println(document1Id);
		json = target().path("/rate").request()
		.cookie(TokenBasedSecurityFilter.COOKIE_NAME, userToken)
		.put(Entity.form(new Form()
			.param("id", document1Id)
			.param("tech_rating", "2")
			.param("interpersonal_rating", "1")
			.param("fit_rating", "3")), JsonObject.class);
		String status = json.getString("status");

		Assert.assertEquals("ok", status);


		DocumentDao documentDao = new DocumentDao();
		// targetId list not necessary 
		DocumentDto documentDto = documentDao.getDocument(document1Id, PermType.READ, new ArrayList<String>());

		Assert.assertEquals("2", documentDto.getAvgTech());
		Assert.assertEquals("1", documentDto.getAvgInterpersonal());
		Assert.assertEquals("3", documentDto.getAvgFit());
		Assert.assertTrue(1 == documentDto.getNumReviews());
	}
	/**
	 * Tests whether a document correctly stores running average of ratings
	 */
	@Test
	public void testRateThreeUsers() throws Exception {

	}

	/**
	 * Tests whether invalid ratings correctly return a server error
	 */
	@Test
	public void testRateInvalidRating() throws Exception {

	}

}
