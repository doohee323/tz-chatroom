'use strict';

angular.module('concordchurchApp')
.factory('ChatService', function ($resource, config) {
  return {
    R: $resource("/bbs/:id", {
    	id:"@id"
		})
	};
});
