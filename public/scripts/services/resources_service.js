'use strict';

angular.module('concordchurchApp')
.factory('ResourcesService', function ($resource, config) {
	try {
		if(Android != null && Android) {
			var factory = {}; 
			factory.R = {};
	    factory.R.get = function(param, callback) {
				var rslt = {};
				rslt.rows = JSON.parse(Android.getResourceStatus(JSON.stringify({})));
				callback(rslt);
			}
	    return factory;
		}
	} catch (e) {
	  return null;
	}
});
