'use strict';

/**
 * Document view analytics controller.
 */
angular.module('docs').controller('DocumentViewAnalytics', function ($scope, $stateParams, Restangular) {
    /**
     * Get ratings and reviewer count from server
     */
    // console.log($stateParams)
    // Restangular.one('file/list').get({ id: $stateParams.id }).then(function(data) {
    //     $scope.num_reviews = data.???;
    //     $scope.avg_ratings = data.???;
    // });
    $scope.num_reviews = 4
    $scope.avg_ratings = [8.8, 2.3, 5.0]

    /**
     * Style the rating scores
     * Copied from https://codepen.io/leandroamato/pen/jOWqrGe
     */
    // Iterate over all rating items
    const ratings = document.querySelectorAll(".rating");
    ratings.forEach((rating, i) => {
        // Get content and get score as a float
        const ratingScore = parseFloat($scope.avg_ratings[i], 10).toFixed(1);

        // Define if the score is good, meh, or bad, then add score class
        const scoreClass = ratingScore < 4.0 ? "bad" : ratingScore < 7.0 ? "meh" : "good";
        rating.classList.add(scoreClass);

        // After adding the class, get its color to define the background gradient
        const ratingColor = window.getComputedStyle(rating).backgroundColor;
        const gradient = `background: conic-gradient(${ratingColor} ${ratingScore * 10}%, transparent 0 100%)`;

        // Set the gradient as the rating background
        rating.setAttribute("style", gradient);

        // Wrap the content in a tag to show it above the pseudo element that masks the bar
        rating.innerHTML = `<span>${ratingScore}</span>`;
    });
});