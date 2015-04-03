'use strict';

angular.module('concordchurchApp')
.controller('SwipeCtrl', function($scope, $http, $state, SwipeService) {

  var prefix = "/api/bunch/v2/me/";
	
	var currentRow = 1;
  $scope.next = function(id) {
    if(!id) {
    	id = currentRow + 1;
    	currentRow = id;
    }
		$scope.retrieve(id);
	}

  $scope.prev = function(id) {
    if(!id) {
    	id = currentRow - 1;
    	currentRow = id;
    }
		$scope.retrieve(id);
	}

  $scope.change = function(id) {
	  
  }
  
  $scope.retrieve = function(id) {
		if(!id) {
			id = currentRow;
		}
		SwipeService.R.get({'id':id}, function(data) {
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

  $scope.refresh = function() {
		if(Android) {
			if(Android.refresh()) {
				document.location.reload();
			}
		}
	}	

  $scope.open = function(item) {
  	config.item = item;
  	$state.go('video');
		if(Android) {
			Android.viewVideo(JSON.stringify(item));
		}
		return;    
  	item = JSON.parse(item);
  	$window.open(item.video, '', 'scrollbars=no,resizeable=no,toolbar=no,status=no,top=100,left=100,width=741,height=472');
  }

	$scope.sendSns = function(sns) {
		config.curitem;
		var url = config.curitem.link;
		if(url.indexOf('#') > -1) url = url.substring(0, url.lastIndexOf('#'));
		var txt = '[콩코드 침례 교회] - title :' + config.curitem.title + ' - content: ' + config.curitem.bible;
		
	    var o;
	    var _url = encodeURIComponent(url);
	    var _txt = encodeURIComponent(txt);
	    var _br  = encodeURIComponent('\r\n');
	 
	    switch(sns) {
	        case 'facebook':
	            o = {
	                method:'popup',
	                url:'http://www.facebook.com/sharer/sharer.php?u=' + _url
	            };
	            break;
	        case 'twitter':
	            o = {
	                method:'popup',
	                url:'http://twitter.com/intent/tweet?text=' + _txt + '&url=' + _url
	            };
	            break;
	        case 'google':
	            o = {
	                method:'popup',
	                url:'https://plus.google.com/share?url=' + _url
	            };
	            break;
	        default:
	            alert('Not supported!');
	            return false;
	    }

	    switch(o.method) {
	        case 'popup':
	            window.open(o.url);
	            break;
	        case 'web2app':
	            if(navigator.userAgent.match(/android/i))
	            {
	                // Android
	                setTimeout(function(){ location.href = 'intent://' + o.param + '#Intent;' + o.g_proto + ';end'}, 100);
	            } else if(navigator.userAgent.match(/(iphone)|(ipod)|(ipad)/i)) {
	                // Apple
	                setTimeout(function(){ location.href = o.a_store; }, 200);          
	                setTimeout(function(){ location.href = o.a_proto + o.param }, 100);
	            } else {
	                alert('Mobile only!');
	            }
	            break;
		    }
  	}
});

// from android
var gfRefresh = function(prefix) {
	for (var key in localStorage){
		if(key.indexOf(prefix) > -1) {
			localStorage.removeItem(key);
		}
	}
}
