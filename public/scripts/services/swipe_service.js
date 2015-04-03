'use strict';

angular.module('concordchurchApp')
.factory('SwipeService', function ($resource, config) {
  return {
    R: $resource("/bbs/:id", {
    	id:"@id"
		})
	};
});
