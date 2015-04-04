'use strict';

angular.module('concordchurchApp')
.factory('ChatroomService', function ($resource, config) {
  return {
    R: $resource("/bbs/:id", {
    	id:"@id"
		})
	};
});
