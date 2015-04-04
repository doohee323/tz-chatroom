'use strict';

angular.module('chatroomApp')
.factory('ChatroomService', function ($resource, config) {
  return {
    L: $resource("/chatrooms/:id", {
    	user_id:"@id"
	}),
    R: $resource("/chatroom/:id", {
    	user_id:"@id"
	}),
    CUD: $resource("/chatroom/:id", {
		user_id:"@id"
		}, { 
			update: {
				method: "PUT"
			}
		})
	};
});
