'use strict';

angular.module('chatroomApp')
.controller('ChatRoomCtrl', function($scope, $http, $state, ChatroomService) {
  
  $scope.retrieve = function() {
	ChatroomService.L.get({}, function(data) {
		if(data.result) {
			$scope.chatrooms = data.result;
		}
	}, function(error) {
	});
  }
  
  $scope.insert = function() {
	if(!$scope.chatroom) {
		writeLog('Chat room is null!', true);
		$('#chatroom')[0].focus();
		return false;
	}
  	var params = {'chatroom': $scope.chatroom};
  	ChatroomService.CUD.save(params, function (result) {
		if(result.code == 0) {
			$scope.chatrooms[$scope.chatrooms.length] = {};
			$scope.chatrooms[$scope.chatrooms.length - 1].name = $scope.chatroom;
		}
		$('#chatroom').val('');
	}, function(error) {
		
	});
  }
	  
  $scope.delete = function(room, indx) {
  	var params = $scope.chatrooms[indx];
  	ChatroomService.CUD.delete(params, function (result) {
		if(result.code == 0) {
			$scope.chatrooms.splice(indx, 1);    
		}
	}, function(error) {
	});
  }

  $scope.join = function(chatroom) {
	$state.go('chat', {chatroom: chatroom.name});
  }  
  
});
