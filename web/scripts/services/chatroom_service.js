'use strict';

angular.module('chatroomApp')
.factory('ChatroomService', function ($resource, config) {
  return {
    L: $resource("/chatrooms/:page", {
    	page:"@page"
	}),
    R: $resource("/chatroom/:page", {
    	page:"@page"
	}),
    CUD: $resource("/chatroom/:page", {
		page:"@page"
		}, { 
			update: {
				method: "PUT"
			}
		})
	};
});
