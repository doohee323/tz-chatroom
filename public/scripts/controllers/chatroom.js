'use strict';

angular.module('chatroomApp')
.controller('ChatRoomCtrl', function($scope, $http, $state, ChatroomService) {

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
	ChatroomService.L.get({'page':id}, function(data) {
		if(data.result) {
			$scope.chatroom = data.result;
		}
	}, function(error) {
	});
  }
  
  $scope.insert = function() {
	var chatroom = document.getElementById("chatroom");
  	var params = {'chatroom': chatroom.value};
  	ChatroomService.CUD.save(params, function (result) {
		if(result.code == 0) {
			$scope.chatroom[$scope.chatroom.length] = {};
			$scope.chatroom[$scope.chatroom.length - 1].name = chatroom.value;
		}
	}, function(error) {
		
	});
  }
	  
  $scope.delete = function(room, indx) {
  	var params = $scope.chatroom[indx];
  	ChatroomService.CUD.delete(params, function (result) {
		if(result.code == 0) {
			$scope.chatroom.splice(indx, 1);    
		}
	}, function(error) {
	});
  }

  $scope.join = function(chatroom) {
	$state.go('chat', {chatroom: chatroom.name});
  }  
  
});
