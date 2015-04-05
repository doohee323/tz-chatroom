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
//	api_url:'http://localhost:9000',
//	ws_url:'ws://localhost:9000'
	api_url:'http://54.163.92.94:3000',
	WS_URL:'WS://54.163.92.94:3000'
};
 
angular
  .module('chatroomApp', [
    'ngCookies',
    'ngResource',
    'ngRoute',
    'ngSanitize',
    'ngTouch',
    'ui.router',
    'httpIntercepter'
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
		url: '/main/:chatroom',
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
	  	console.log(errorInfo.stack);
  		console.log('sent error message to the server!' + JSON.stringify(errorInfo));
  	}
    //throw exception;
  };
});
