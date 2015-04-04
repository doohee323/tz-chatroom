'use strict';

angular.module('chatroomApp')
.factory('ChatService', function ($resource, config) {
  return {
    R: $resource("/bbs/:id", {
    	id:"@id"
		})
	};
});
