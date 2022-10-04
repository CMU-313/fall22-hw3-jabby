'use strict';

/**
 * Document view analytics controller.
 */
angular.module('docs').controller('DocumentViewAnalytics', function ($scope, $stateParams, Restangular) {
    /**
     * Get ratings and reviewer count from server
     */
    console.log($stateParams)
    Restangular.one('file/list').get({ id: $stateParams.id }).then(function(data) {
        $scope.files = data.files;
    });

    /**
     * Style the rating scores
     * Copied from https://codepen.io/leandroamato/pen/jOWqrGe
     */
    // Find all rating items
    const ratings = document.querySelectorAll(".rating");

    // Iterate over all rating items
    ratings.forEach((rating) => {
        // Get content and get score as a float
        const ratingScore = parseFloat(rating.innerHTML, 10).toFixed(1);

        // Define if the score is good, meh, or bad according to its value
        const scoreClass = ratingScore < 4.0 ? "bad" : ratingScore < 7.0 ? "meh" : "good";

        // Add score class to the rating
        rating.classList.add(scoreClass);

        // After adding the class, get its color
        const ratingColor = window.getComputedStyle(rating).backgroundColor;

        // Define the background gradient according to the score and color
        const gradient = `background: conic-gradient(${ratingColor} ${ratingScore * 10}%, transparent 0 100%)`;

        // Set the gradient as the rating background
        rating.setAttribute("style", gradient);

        // Wrap the content in a tag to show it above the pseudo element that masks the bar
        rating.innerHTML = `<span>${ratingScore}</span>`;
    });
});