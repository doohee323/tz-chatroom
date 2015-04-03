'use strict';

/**
 * @ngdoc function
 * @name concordchurchApp.controller:LogsCtrl
 * @description
 * # LogsCtrl
 * Controller of the concordchurchApp
 */
var bRun;
angular.module('concordchurchApp')
  .controller('LogsCtrl', function ($rootScope, $scope, $window, $stateParams, $state, $http, $location, $timeout, config, LogsService) {
		$scope.$location = $location;
		var read_at = $stateParams.read_at;
  	var prefix = "/api/bunch/v2/me/";
		var today = moment().startOf('day').utc().toDate().format('YYYY-MM-DD');
		if(!read_at) read_at = today;
		
		$scope.alert = '';
		$scope.showListBottomSheet = function($event) {
		  $scope.alert = '';
		};	
		$scope.showGridBottomSheet = function($event) {
	    $scope.alert = '';
	  };	
	
	  $scope.next = function(read_at) {
	    if(!read_at) {
	    	read_at = moment(today).startOf('day').utc().add('d', 1).toDate().format('YYYY-MM-DD');
	    	today = read_at;
	    }
			$scope.retrieve(read_at);
		}
	
	  $scope.prev = function(read_at) {
	    if(!read_at) {
	    	read_at = moment(today).startOf('day').utc().add('d', -1).toDate().format('YYYY-MM-DD');
	    	today = read_at;
	    }
			$scope.retrieve(read_at);
		}
		
	  $scope.retrieve = function(read_at) {
			if(!read_at) {
				read_at = today;
			}
			LogsService.R.get({'read_at':read_at}, function(data) {
				if(data.rows) {
					$scope.logs = data.rows;
					localStorage.setItem(prefix + read_at, JSON.stringify($scope.logs));
					//if(Android) {
					//	Android.cacheJson(JSON.stringify($scope.logs));
					//}
			    config.curitem = $scope.logs[0];
				}
			}, function(error) {
				var datast = localStorage.getItem(prefix + read_at);
				if(datast) {
					$scope.logs = JSON.parse(datast);
			    config.curitem = $scope.logs[0];
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
			return;    
    	item = JSON.parse(item);
    	$window.open(item.video, '', 'scrollbars=no,resizeable=no,toolbar=no,status=no,top=100,left=100,width=741,height=472');
    }
    
  $scope.title1 = 'Button';
  $scope.title4 = 'Warn';
  $scope.isDisabled = true;
  $scope.googleUrl = 'http://google.com';    
})

.controller('ListBottomSheetCtrl', function($scope, config) {
})
.controller('GridBottomSheetCtrl', function($scope, config) {
  $scope.items = [
    { name: 'facebook', icon: 'facebook' },
    { name: 'twitter', icon: 'twitter' },
    { name: 'google', icon: 'google' },
    { name: 'email', icon: 'email' },
    { name: 'sms', icon: 'sms' },
    { name: 'copy', icon: 'copy' }
  ];
  $scope.listItemClick = function($index) {
    var clickedItem = $scope.items[$index];
//    if(clickedItem.name == 'facebook') {
    	$scope.sendSns(clickedItem.name);
//    }
  };
  
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
})
