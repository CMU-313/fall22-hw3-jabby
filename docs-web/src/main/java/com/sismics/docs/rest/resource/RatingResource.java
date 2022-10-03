package com.sismics.docs.rest.resource;

import com.sismics.docs.core.constant.PermType;
import com.sismics.docs.core.dao.*;
import com.sismics.docs.core.dao.dto.*;
import com.sismics.docs.core.model.jpa.Document;

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
		System.out.println("got num reviews");
		try {
			// assumes that ratings are inputted as integers 
			int convertedFitRating = Integer.parseInt(fitRating);
			int convertedTechRating = Integer.parseInt(techRating);
			int convertedInterpersonalRating = Integer.parseInt(interpersonalRating);
			System.out.println("parsed inputs");
			document.setNumReviews(previousReviews + 1);
			System.out.println("set new num reviews");
			float newAverageFit = getRunningAverage(convertedFitRating, 
													Float.parseFloat(document.getAvgFit()), 
													previousReviews + 1);
			System.out.println("got new average fit");
			System.out.println(newAverageFit);
			document.setAvgFit(Float.toString(newAverageFit));
			System.out.println("set new avg fit");
			float newAverageTech = getRunningAverage(convertedTechRating, 
			Float.parseFloat(document.getAvgTech()), 
			previousReviews + 1);
			System.out.println("got new avg tech");
			System.out.println(newAverageTech);
			document.setAvgTech(Float.toString(newAverageTech));
			System.out.println("set new avg tech");

			float newAverageInterpersonal = getRunningAverage(convertedInterpersonalRating, 
			Float.parseFloat(document.getAvgInterpersonal()), 
			previousReviews + 1);
			System.out.println("got new avg inter");
			document.setAvgInterpersonal(Float.toString(newAverageInterpersonal));
			System.out.println("set new avg inter");
			// assume that logged in user is the one rating the document
			documentDao.update(document, principal.getId());
			System.out.println("updated document");
		} catch (NumberFormatException e) {
			// return error message if issue with parsing occurs
			Response.serverError().entity(e.getMessage()).build();
		}
		System.out.println("finished try");
		// return OK if successfully saved 
		JsonObjectBuilder response = Json.createObjectBuilder()
				.add("status", "ok");
		return Response.ok().entity(response.build()).build();
	}
}
