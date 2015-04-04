'use strict';

angular.module('chatroomApp')
.factory('ChatroomService', function ($resource, config) {
  return {
    R: $resource("/bbs/:id", {
    	id:"@id"
		})
	};
});
