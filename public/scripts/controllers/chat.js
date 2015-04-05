'use strict';

angular.module('chatroomApp')
.controller('ChatCtrl', function($scope, $http, $state, $stateParams, $timeout, config, ChatService) {

  $scope.init = function() {
	$scope.chatroom = $stateParams.chatroom;
  }  
  
  var wsUri;
  $scope.join = function() {
	var param = {
		type: 'join',
		chatroom: $scope.chatroom,
		username: $scope.username
	}
	wsUri = config.ws_url + "/chatroom/chat/" + JSON.stringify(param);
	websocket = new ReconnectingWebSocket(wsUri, null, {debug: false, reconnectInterval: 3000});
	$('#chatroom').val('');
	$('#chatroom')[0].focus();
	
	var output = $('#output')[0];  
	websocket.onopen = function(evt) { 
	}
	websocket.onclose = function(evt) {
		writeMsg("passed out...");
	}; 
	websocket.onmessage = function(evt) { 
		onMessage(evt) 
	}; 
	websocket.onerror = function(evt) { 
		onError(evt) 
	};	
  }
  
  $scope.quit = function() {
	var param = {
		type: 'quit',
		chatroom: $scope.chatroom,
		username: $scope.username
	}
	if(websocket) {
		websocket.send(JSON.stringify(param));
		websocket.close();
	}
	$state.go('chatRooms');
  }  
  
  $scope.talk = function() {
	var param = {
		type: 'talk',
		chatroom: $scope.chatroom,
		username: $scope.username,
		text: $scope.text
	};
	$('#chatroom').val('');
	websocket.send(JSON.stringify(param));
  } 
  
  $scope.clear = function() {
	  $('#output').text('')  
  }
  
});

function onMessage(evt) { 
	writeMsg('<span style="color: blue;"> >: ' + evt.data+'</span>'); 
}  
function onError(evt) { 
//	writeMsg('<span style="color: red;">ERROR:</span> ' + evt.data); 
}  
function doSend(message) { 
	var param = {
		chatroom: $('#chatroom').text(),
		username: $('#username').val(),
		text: message
	};
	websocket.send(JSON.stringify(param)); 
}  
function writeMsg(message) { 
	var pre = document.createElement("p"); 
	pre.style.wordWrap = "break-word"; 
	pre.innerHTML = message;
	$('#output')[0].appendChild(pre); 
} 

