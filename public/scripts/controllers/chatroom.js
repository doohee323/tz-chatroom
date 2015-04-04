'use strict';

angular.module('concordchurchApp')
.controller('ChatRoomCtrl', function($scope, $http, $state, ChatService) {

  $scope.talk = function() {
	var chat = document.getElementById("talk");
	websocket.send(chat.value);
  }  
		
  $scope.retrieve = function(id) {
	if(!id) {
		id = currentRow;
	}
	ChatService.R.get({'id':id}, function(data) {
		if(data.rows) {
			$scope.words = data.rows;
			localStorage.setItem(prefix + id, JSON.stringify($scope.words));
			if(Android) {
				Android.cacheJson(JSON.stringify($scope.words));
			}
	    config.curitem = $scope.words[0];
		}
	}, function(error) {
		var datast = localStorage.getItem(prefix + id);
		if(datast) {
			$scope.words = JSON.parse(datast);
	    config.curitem = $scope.words[0];
	  }
	});
  }
});
