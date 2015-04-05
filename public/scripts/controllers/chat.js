'use strict';

angular.module('chatroomApp')
.controller('ChatCtrl', function($scope, $http, $state, $stateParams, $timeout, config, ChatService) {

  $scope.init = function() {
	$scope.chatroom = $stateParams.chatroom;
  }  
  
  $scope.valiate = function() {
	if(!$scope.chatroom) {
		writeLog('Chat room is null!', true);
		$('#chatroom')[0].focus();
		return false;
	}
	if(!$scope.username) {
		writeLog('User name is null!', true);
		$('#username')[0].focus();
		return false;
	}
	return true;
  }
  
  var wsUri;
  $scope.join = function() {
	if(!$scope.valiate()) return;
	var param = {
		type: 'join',
		chatroom: $scope.chatroom,
		username: $scope.username
	}
	wsUri = config.ws_url + "/chatroom/chat/" + JSON.stringify(param);
	websocket = new ReconnectingWebSocket(wsUri, null, {debug: false, reconnectInterval: 3000});
	$scope.text = '';
	$('#text')[0].focus();
	
	var output = $('#output')[0];  
	var output2 = $('#output2')[0];  
	websocket.onopen = function(evt) { 
	}
	websocket.onclose = function(evt) {
		writeMsg("{passed out...}");
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
	if(!$scope.valiate()) return;
	if(!$scope.text) {
		writeLog('Text is null!', true);
		$('#text')[0].focus();
		return false;
	}
	var param = {
		type: 'talk',
		chatroom: $scope.chatroom,
		username: $scope.username,
		text: $scope.text
	};
	$scope.text = '';
	websocket.send(JSON.stringify(param));
  } 
  
  $scope.clear = function() {
	  $('#output').text('')  
	  $('#output2').text('')  
  }
  
});

function onMessage(evt) { 
	writeMsg(evt.data); 
}  
function onError(evt) { 
	writeMsg(evt.data); 
}  
function doSend(message) { 
	var param = {
		chatroom: $('#chatroom').text(),
		username: $('#username').val(),
		text: message
	};
	websocket.send(JSON.stringify(param)); 
}  
function writeMsg(input) {
	var json = JSON.parse(input);
	var msg = '<span style="color: blue;"> > ' + json.text + '</span>';
	var pre = document.createElement("p"); 
	pre.style.wordWrap = "break-word"; 
	pre.innerHTML = msg;
	$('#output')[0].appendChild(pre); 

	writeLog(JSON.stringify(json));
} 

function writeLog(input, err) {
	if(config.debug) {
		var msg;
		if(err) {
			msg = '<span style="color: red;">ERROR > ' + input + '</span><br>';
		} else {
			msg = '<span style="color: blue;"> > ' + input + '</span><br>';
		}
		var pre2 = document.createElement("p2"); 
		pre2.style.wordWrap = "break-word"; 
		pre2.innerHTML = msg;
		$('#output2')[0].appendChild(pre2); 
	}
} 

