'use strict';

/**
 * Document modal rate controller.
 */
angular.module('docs').controller('DocumentModalRate', function ($scope, $uibModalInstance) {
  $scope.name = '';
  $scope.close = function(name) {
    $uibModalInstance.close(name);
  }
});



