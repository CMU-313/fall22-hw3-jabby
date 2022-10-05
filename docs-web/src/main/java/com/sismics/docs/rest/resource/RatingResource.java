package com.sismics.docs.rest.resource;

import com.sismics.docs.core.constant.PermType;
import com.sismics.docs.core.dao.*;
import com.sismics.docs.core.dao.dto.*;
import com.sismics.docs.core.model.jpa.Document;
import com.sismics.docs.core.model.jpa.User;
import com.sismics.rest.exception.ForbiddenClientException;

import javax.json.Json;

import javax.json.JsonObjectBuilder;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/rate")
public class RatingResource extends BaseResource {

	// Returns the new average after adding newNum 
	private float getRunningAverage(int newNum, float prevAverage, int count) {
			return prevAverage + (newNum - prevAverage) / count;
	}

	/**
	 * Saves ratings for a document. 
	 * @api {put} /rate
	 * @apiName addRating
	 * @apiGroup Document
	 * @apiParam {String} id Document ID
	 * @apiParam {String} tech_rating Technical Skill Rating
	 * @apiParam {String} interpersonal_rating Interpersonal Skill Rating 
	 * @apiParam {String} fit_rating Fit Rating 
	 */
	@PUT
	public Response rate(
		@FormParam("id") String documentId,
		@FormParam("tech_rating") String techRating,
		@FormParam("interpersonal_rating") String interpersonalRating, 
		@FormParam("fit_rating") String fitRating) 
		{
		// only authenticated users should be able to rate documents
		if (!authenticate()) {
			throw new ForbiddenClientException();
		}

		DocumentDao documentDao = new DocumentDao();
		// targetId list not necessary 
		Document document = documentDao.getById(documentId);
		if (document == null) {
			throw new NotFoundException();
		}

		int previousReviews = document.getNumReviews();
		try {
			// assumes that ratings are inputted as integers 
			int convertedFitRating = Integer.parseInt(fitRating);
			int convertedTechRating = Integer.parseInt(techRating);
			int convertedInterpersonalRating = Integer.parseInt(interpersonalRating);

			document.setNumReviews(previousReviews + 1);

			float newAverageFit = getRunningAverage(convertedFitRating, 
													Float.parseFloat(document.getAvgFit()), 
													previousReviews + 1);
			document.setAvgFit(Float.toString(newAverageFit));

			float newAverageTech = getRunningAverage(convertedTechRating, 
			Float.parseFloat(document.getAvgTech()), 
			previousReviews + 1);
			document.setAvgTech(Float.toString(newAverageTech));

			float newAverageInterpersonal = getRunningAverage(convertedInterpersonalRating, 
			Float.parseFloat(document.getAvgInterpersonal()), 
			previousReviews + 1);
			document.setAvgInterpersonal(Float.toString(newAverageInterpersonal));

			// assume that logged in user is the one rating the document
			documentDao.update(document, principal.getId());
		} catch (Exception e) {
			// return error message if issue with parsing occurs
			Response.serverError().entity(e.getMessage()).build();
		}

		// return OK if successfully saved 
		JsonObjectBuilder response = Json.createObjectBuilder()
				.add("status", "ok");
		return Response.ok().entity(response.build()).build();
	}
	@GET
	public Response tech(
		@FormParam("id") String documentId
	){
		if (!authenticate()) {
			throw new ForbiddenClientException();
		}
		JsonObjectBuilder response = Json.createObjectBuilder();

		DocumentDao documentDao = new DocumentDao();
		Document document = documentDao.getById(documentId);

		if (document == null) {
			throw new NotFoundException();
		}
		String techRating = document.getAvgTech();

		response.add("avg_tech_rating", techRating);
		return Response.ok().entity(response.build()).build();
	}	
	@GET
	public Response fit(
		@FormParam("id") String documentId
	){
		if (!authenticate()) {
			throw new ForbiddenClientException();
		}
		JsonObjectBuilder response = Json.createObjectBuilder();

		DocumentDao documentDao = new DocumentDao();
		Document document = documentDao.getById(documentId);

		if (document == null) {
			throw new NotFoundException();
		}
		String fitRating = document.getAvgFit();

		response.add("avg_fit_rating", fitRating);
		return Response.ok().entity(response.build()).build();
	}	
	@GET
	public Response interpersonal(
		@FormParam("id") String documentId
	){
		if (!authenticate()) {
			throw new ForbiddenClientException();
		}
		JsonObjectBuilder response = Json.createObjectBuilder();

		DocumentDao documentDao = new DocumentDao();
		Document document = documentDao.getById(documentId);

		if (document == null) {
			throw new NotFoundException();
		}
		String interpersonalRating = document.getAvgInterpersonal();

		response.add("avg_interpersonal_rating", interpersonalRating);
		return Response.ok().entity(response.build()).build();
	}
	@GET
	public Response percentRating(
		@FormParam("id") String documentId
	){
		if (!authenticate()) {
			throw new ForbiddenClientException();
		}
		JsonObjectBuilder response = Json.createObjectBuilder();

		DocumentDao documentDao = new DocumentDao();
		Document document = documentDao.getById(documentId);

		UserDao userDao = new UserDao();

		if (document == null) {
			throw new NotFoundException();
		}

		int numReviews = document.getNumReviews();
		int totalActiveUsers = (int)userDao.getActiveUserCount();

		float percentRating = (float) numReviews / totalActiveUsers;
		response.add("percentage_rating", percentRating);

		return Response.ok().entity(response.build()).build();
	}
}
