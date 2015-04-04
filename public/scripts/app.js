'use strict';

/**
 * @ngdoc overview
 * @name chatroomApp
 * @description
 * # chatroomApp
 *
 * Main module of the application.
 */

var config = {
	api_url:'http://52.0.156.206:3000'
//	api_url:'http://192.168.1.17:3000'
};
 
angular
  .module('chatroomApp', [
    'ngCookies',
    'ngResource',
    'ngRoute',
    'ngSanitize',
    'ngTouch',
    'ui.router',
    'locketAdminUtils'
  ]).constant('config', config)
.config(['$stateProvider', '$urlRouterProvider', '$httpProvider', '$provide',
  function ($stateProvider, $urlRouterProvider, $httpProvider, $provide) {
  
  	$httpProvider.defaults.useXDomain = true;
  	$httpProvider.defaults.headers.common['Access-Control-Allow-Headers'] = '*';
  
	$stateProvider.state('index', {
		url: "",
		templateUrl: 'views/chatroom.html',
		controller: 'ChatRoomCtrl'
	}).state('chatRooms', {
		url: '/main',
		templateUrl: 'views/chatroom.html',
		controller: 'ChatRoomCtrl'
	}).state('chat', {
		url: '/main',
		templateUrl: 'views/chat.html',
		controller: 'ChatCtrl'
	});
	
  /**
   * @description
   * register $http interceptor factory
   */
  $httpProvider.interceptors.push('httpinterceptor');
  
}]);

angular.module('chatroomApp').factory('$exceptionHandler', function () {
  return function (exception, cause) {
  	if(exception.message == "Cannot read property 'resolve' of undefined") {
  		console.log(exception.message);
  		return;
  	} else if(exception.message && exception.message.startsWith('[$resource:badcfg]')){
			//localStorage.setItem("__SESSION_INFO", JSON.stringify({}));
  		//if(parent) {
  		//	parent.document.location = '/mpage';
  		//} else {
  		//	document.location = '/mpage';
  		//}
	    exception.message += ' (caused by "' + cause + '")';
  	} else {
	  	var errorInfo = {title: exception.message,
	        date: moment().utc().toDate().format('YYYYMMDD HH:mm:ss:SS'),
	        stack: exception.stack
	      };
	    $.ajax({
	      type: "POST",
	      url: "http://192.168.1.5:3005/loggingFromClient",
	      data: errorInfo
	    }).done(function(data) {
		  	console.log(errorInfo.stack);
	  		console.log('sent error message to the server!' + JSON.stringify(errorInfo));
	    }).fail(function(data) {
	  		//console.log('can\'t send error message to the server!' + data);
	    }).always(function(data) {
			});   			    
  	}
    //throw exception;
  };
});
