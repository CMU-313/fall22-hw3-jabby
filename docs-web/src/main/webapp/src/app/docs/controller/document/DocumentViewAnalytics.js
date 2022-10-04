'use strict';

/**
 * Document view analytics controller.
 */
angular.module('docs').controller('DocumentViewAnalytics', function ($scope, $stateParams, Restangular) {
    
    $scope.loadReviews = function () {
        /**
         * Get number of reviews and average ratings from server
         * model after DocumentView.js:8-12
         * documents from DocumentResource.java:156-286
         */
        Restangular.one('document', $stateParams.id).get().then(function (data) {
            $scope.num_reviews = data.num_reviews;
            $scope.avg_ratings = [data.tech_rating, data.interpersonal_rating, data.fit_rating];

            // Use below lines for testing:
            // $scope.num_reviews = 4
            // $scope.avg_ratings = [8.8, 5.0, 0.0]

            /**
             * Style the rating scores
             * Copied from https://codepen.io/leandroamato/pen/jOWqrGe
             */
            // Iterate over all rating items
            const ratings = document.querySelectorAll(".rating");
            ratings.forEach((rating, i) => {
                // Get content (float) and round to 1 decimal
                const ratingScore = parseFloat($scope.avg_ratings[i], 10).toFixed(1);

                // Define if the score is good, meh, or bad, then add score class
                const scoreClass = ratingScore < 1.0 ? "nan" :
                                   ratingScore < 4.0 ? "bad" :
                                   ratingScore < 7.0 ? "meh" : "good";
                rating.classList.add(scoreClass);

                // After adding the class, get its color
                const ratingColor = window.getComputedStyle(rating).backgroundColor;

                // (1) Define the background gradient, and set it as the rating background
                // (2) Wrap the content in a tag to show it above the pseudo element that masks the bar
                if (scoreClass == "nan") {
                    const gradient = `background: conic-gradient(${ratingColor} 100%, transparent 0 100%)`;
                    rating.setAttribute("style", gradient);
                    rating.innerHTML = `<span>N/A</span>`;
                }
                else {
                    const gradient = `background: conic-gradient(${ratingColor} ${ratingScore * 10}%, transparent 0 100%)`;
                    rating.setAttribute("style", gradient);
                    rating.innerHTML = `<span>${ratingScore}</span>`;
                }
            });
        }, function (response) {
            $scope.error = response;
        });
    };
    $scope.loadReviews();
    
});