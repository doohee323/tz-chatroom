'use strict';

angular.module('chatroomApp')
.factory('ChatService', function ($resource, config) {
  return {
    J: $resource("/chatroom/join/:username", {
    	username:"@username"
	}),
    R: $resource("/bbs/:id", {
    	id:"@id"
		})
	};
});
