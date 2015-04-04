'use strict';

angular.module('chatroomApp')
.controller('ChatRoomCtrl', function($scope, $http, $state, ChatroomService) {

  $scope.talk = function() {
	var chat = document.getElementById("talk");
	websocket.send(chat.value);
  }  

  var currentRow = 1;
  $scope.next = function(id) {
    if(!id) {
    	id = currentRow + 1;
    	currentRow = id;
    }
	$scope.retrieve(id);
  }

  $scope.prev = function(id) {
    if(!id) {
    	id = currentRow - 1;
    	currentRow = id;
    }
	$scope.retrieve(id);
  }
  
  $scope.retrieve = function(id) {
	if(!id) {
		id = currentRow;
	}
	ChatroomService.L.get({'id':id}, function(data) {
debugger;
		if(data.chatroom) {
			$scope.chatroom = data.chatroom;
		}
	}, function(error) {
	});
  }
  
  $scope.insert = function() {
	var chatroom = document.getElementById("chatroom");
  	var params = {'chatroom': chatroom.value};
  	ChatroomService.CUD.save(params, function (result) {
		if(result.code == 0) {
			$scope.chatroom[$scope.chatroom.length] = chatroom.value;
		}
	}, function(error) {
		
	});
  }
	  
  $scope.delete = function() {
  	var params = $scope.chatroom;
  	ChatroomService.CUD.delete(params, function (result) {
		if(data.rows) {
			$scope.chatroom = data.rows;
		}
	}, function(error) {
	});
  }
	  
});
