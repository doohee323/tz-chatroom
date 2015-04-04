'use strict';

var wsUri = "ws://localhost:9000/room/chat/aaa"; 
var websocket = new WebSocket(wsUri); 

angular.module('concordchurchApp')
.controller('ChatCtrl', function($scope, $http, $state, ChatService) {

	var output = document.getElementById("output");  
	websocket.onopen = function(evt) { 
		onOpen(evt) 
	}

	websocket.onclose = function(evt) {
		onClose(evt) 
	}; 
	websocket.onmessage = function(evt) { 
		onMessage(evt) 
	}; 
	websocket.onerror = function(evt) { 
		onError(evt) 
	}; 
	
  $scope.join = function() {
	var username = document.getElementById("username");
	websocket.join(username.value);
  }  
	
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

function onOpen(evt) { 
	writeToScreen("CONNECTED"); 
	doSend("WebSocket rocks"); 
}  
function onClose(evt) { 
	writeToScreen("DISCONNECTED"); 
}  
function onMessage(evt) { 
	writeToScreen('<span style="color: blue;">RESPONSE: ' + evt.data+'</span>'); 
}  
function onError(evt) { 
	writeToScreen('<span style="color: red;">ERROR:</span> ' + evt.data); 
}  
function doSend(message) { 
	writeToScreen("SENT: " + message);  
	websocket.send(message); 
}  
function writeToScreen(message) { 
	var pre = document.createElement("p"); 
	pre.style.wordWrap = "break-word"; 
	pre.innerHTML = message; output.appendChild(pre); 
} 

