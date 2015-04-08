'use strict';

/**
 * @ngdoc object
 * @name locketAdminApp.service:httpinterceptor
 * 
 * @description
 * This is service factory 
 ```
  usage) in locketAdminApp.config function
  
  $httpProvider.interceptors.push('httpinterceptor');
 ```
 */

angular.module('httpIntercepter',[])
  .factory('httpinterceptor', ['$q', '$rootScope', 'msgconst', 'config', function ($q, $rootScope, msgconst, config) {

    var started = function(req) {
      if(req) {
        $rootScope.running = true;
      }
    };
    
    var ended = function(req) {
      if(req) {
        $rootScope.running = false;
      }
    };
    
    return {
      'request': function(req) {
        started(req);
				if(req.url.trim().endsWith('html')){
				} else {
					req.headers = req.headers || {};
				}      
        return req || $q.when(req);
      },
      'requestError': function(request) {
        ended(request.req);
        return $q.reject(request);
      },
      'response': function(response) {
        ended(response.req);
        return response || $q.when(response);
      },
      'responseError': function(response) {
        ended(response.req);
        
        var status = response.status;
        var msg = '';
        var title = '';
        if(status == 400 || status == 401 || status == 403 || status == 404 || status == 500) {
          title += msgconst['httpStatus_'+status].code;
          msg += msgconst['httpStatus_'+status].message;
          if(status == 500) {
          	msg += response.data;
          	alert(msg);
          }
        } else {
          title += 'Server Error';
          if(response.req) {
	          msg += "HTTP: " + response.req.method + " on " + response.req.url + " failed.";
          } else {
	          msg += "HTTP: " + response.config.method + " failed.";
          }
        }
        console.error('[' + title + ', HTTP:' + status + '], message=' + msg);
        
        return $q.reject(response);
      }
    }
    
  }]);
