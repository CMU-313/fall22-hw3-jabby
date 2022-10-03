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
import java.util.*;

@Path("/rate")
public class RatingResource extends BaseResource {

    // Returns the new average after adding newNum 
    private float getRunningAverage(int newNum, float prevAverage, int count) {
        return prevAverage + (newNum - prevAverage) / count;
    }

	/**
	 * Saves ratings for a document. 
	 * @api {post} /rate
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
		DocumentDto documentDto = documentDao.getDocument(documentId, PermType.READ, new ArrayList<String>());
		if (documentDto == null) {
			throw new NotFoundException();
		}

		int previousReviews = documentDto.getNumReviews();

		try {
			// assumes that ratings are inputted as integers 
			int convertedFitRating = Integer.parseInt(fitRating);
			int convertedTechRating = Integer.parseInt(techRating);
			int convertedInterpersonalRating = Integer.parseInt(interpersonalRating);

			documentDto.setNumReviews(previousReviews + 1);

			float newAverageFit = getRunningAverage(convertedFitRating, 
													Float.parseFloat(documentDto.getAvgFit()), 
													previousReviews);

			documentDto.setAvgFit(Float.toString(newAverageFit));

			float newAverageTech = getRunningAverage(convertedTechRating, 
			Float.parseFloat(documentDto.getAvgTech()), 
			previousReviews);

			documentDto.setAvgTech(Float.toString(newAverageTech));


			float newAverageInterpersonal = getRunningAverage(convertedInterpersonalRating, 
			Float.parseFloat(documentDto.getAvgInterpersonal()), 
			previousReviews);

			documentDto.setAvgInterpersonal(Float.toString(newAverageInterpersonal));

			Document document = documentDao.getById(documentId);
			// assume that logged in user is the one rating the document
			documentDao.update(document, principal.getId());

		} catch (NumberFormatException e) {
			// return error message if issue with parsing occurs
			Response.serverError().entity(e.getMessage()).build();
		}

		// return OK if successfully saved 
		JsonObjectBuilder response = Json.createObjectBuilder()
				.add("status", "ok");
		return Response.ok().entity(response.build()).build();
	}
}
