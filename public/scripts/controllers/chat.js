'use strict';

angular.module('chatroomApp')
.controller('ChatCtrl', function($scope, $http, $state, $stateParams, config, ChatService) {

  $scope.init = function() {
	$scope.chatroom = $stateParams.chatroom;
  }  
  
  $scope.join = function() {
	var param = {
		type: 'join',
		chatroom: $scope.chatroom,
		username: $scope.username
	}
	var wsUri = config.ws_url + "/chatroom/chat/" + JSON.stringify(param);
	websocket = new WebSocket(wsUri);
	
	var output = $('#output')[0];  
	websocket.onopen = function(evt) { 
	}
	websocket.onclose = function(evt) {
		$scope.rejoin(param);
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
	websocket.send(JSON.stringify(param));
	websocket.close();
  }  
  
  $scope.talk = function() {
	var param = {
		type: 'talk',
		chatroom: $scope.chatroom,
		username: $scope.username,
		text: $scope.text
	};
	$scope.rejoin(param);
	websocket.send(JSON.stringify(param));
  } 
  
  $scope.clear = function() {
	  $('#output').text('')  
  }
  
  $scope.rejoin = function(param) {
	if (websocket.readyState != 1) {
		writeMsg("try to rejoin!");
		param.type = 'join';
		var wsUri = config.ws_url + "/chatroom/chat/" + JSON.stringify(param);
		websocket = new WebSocket(wsUri);
	}
  }    
});

function onMessage(evt) { 
	writeMsg('<span style="color: blue;"> >: ' + evt.data+'</span>'); 
}  
function onError(evt) { 
	writeMsg('<span style="color: red;">ERROR:</span> ' + evt.data); 
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

