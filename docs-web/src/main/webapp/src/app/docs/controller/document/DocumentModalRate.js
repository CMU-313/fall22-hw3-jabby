'use strict';

/**
 * Document modal rate controller.
 */
angular.module('docs').controller('DocumentModalRate', function ($scope, $uibModalInstance) {
  $scope.tech_rating = 0;
  $scope.interpersonal_rating=0;
  $scope.fit_rating=0;
  $scope.close = function(tech_rating, interpersonal_rating, fit_rating) {
    const ratings = [tech_rating, interpersonal_rating, fit_rating];
    $uibModalInstance.close(ratings);
  }
});



