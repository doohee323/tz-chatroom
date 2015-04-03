'use strict';

/**
 * @ngdoc function
 * @name concordchurchApp.controller:LogsCtrl
 * @description
 * # LogsCtrl
 * Controller of the concordchurchApp
 */
angular.module('concordchurchApp')
  .controller('ResourcesCtrl', function ($rootScope, $scope, $window, $stateParams, $state, $http, $location, $timeout, config, ResourcesService) {
		$scope.$location = $location;
		
	  $scope.retrieve = function() {
			ResourcesService.R.get({}, function(data) {
				if(data.rows) {
					$scope.resources = data.rows;
			    config.curitem = $scope.resources[0];
				}
			}, function(error) {
			});
		}
})

