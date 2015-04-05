'use strict';

angular.module('chatroomApp')
.controller('ChatRoomCtrl', function($scope, $http, $state, ChatroomService) {
  
  $scope.retrieve = function() {
	ChatroomService.L.get({}, function(data) {
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
		$('#chatroom').val('');
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
