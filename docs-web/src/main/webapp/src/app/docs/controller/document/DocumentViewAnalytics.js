'use strict';

/**
 * Document view analytics controller.
 */
 angular.module('docs').controller('DocumentViewAnalytics', function ($scope, $stateParams, Restangular) {

    /**
     * Style the percentage score (1) and rating scores (3)
     * Copied from https://codepen.io/leandroamato/pen/jOWqrGe
     */
    $scope.styleReviews = function () {
        // Iterate over all rating items
        const ratings = document.querySelectorAll(".rating");
        ratings.forEach((rating, i) => {
            // Get content (float) and round to 1 decimal
            const ratingScore = parseFloat($scope.ratings[i], 10).toFixed(1);

            // Define if the score is good, meh, or bad, then add score class
            var scoreClass = (i==0) ? getScoreClass(ratingScore) : getScoreClass(ratingScore * 10);
            rating.classList.add(scoreClass);

            // After adding the class, get its color
            const ratingColor = window.getComputedStyle(rating).backgroundColor;

            // (1) Define the background gradient, and set it as the rating background
            // (2) Wrap the content in a tag to show it above the pseudo element that masks the bar
            if (i == 0 && scoreClass == "nan") {
                rating.setAttribute("style", getGradient(ratingColor, 100));
                rating.innerHTML = `<span>${Math.round(ratingScore)}%</span>`;
            }
            else if (i == 0 && scoreClass != "nan"){
                rating.setAttribute("style", getGradient(ratingColor, ratingScore));
                rating.innerHTML = `<span>${Math.round(ratingScore)}%</span>`;
            }
            else if (i != 0 && scoreClass == "nan"){
                rating.setAttribute("style", getGradient(ratingColor, 100));
                rating.innerHTML = `<span>N/A</span>`;
            }
            else {
                rating.setAttribute("style", getGradient(ratingColor, ratingScore * 10));
                rating.innerHTML = `<span>${ratingScore}</span>`;
            }
        });
    }
    function getScoreClass(score) {
        return score ==  0.0 ? "nan" :
               score <  40.0 ? "bad" :
               score <  70.0 ? "meh" : "good";
    }
    function getGradient(color, score) {
        return `background: conic-gradient(${color} ${score}%, transparent 0 100%)`;
    }

    /**
     * Get percentage of reviews and average ratings from server
     */
    $scope.loadReviews = function () {
        Restangular.one('document', $stateParams.id).get().then(function (data) {
            $scope.ratings = [data.num_reviews, data.tech_rating, data.interpersonal_rating, data.fit_rating];
            // $scope.ratings = [4/7 * 100, 8.8, 2.0, 0.0]; // purely for testing
            $scope.styleReviews();
        }, function (response) {
            $scope.error = response;
        });
    };
    $scope.loadReviews();
    
});