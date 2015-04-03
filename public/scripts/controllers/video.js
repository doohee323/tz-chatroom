'use strict';

var player;
var videoId;

angular.module('concordchurchApp')
.controller('VideoCtrl', function($window, $rootScope, $scope, $sce, $state, config) {

	$scope.onload = function() {
		if(config.item) {
			$scope.item = angular.fromJson(config.item);
			$scope.trustSrc = function() {
		    return $sce.trustAsResourceUrl($scope.item.video);
		  }
		  
		  window.open(
			  $scope.item.video,
			  '_blank' 
			);
			return;
			
			videoId = $scope.item.video;
			videoId = videoId.substring(videoId.lastIndexOf('/') + 1, videoId.indexOf('?'));
	    var firstScriptTag = document.getElementsByTagName('script')[0];
	    if(!config.player) {
	    	config.player = 'loaded';
		    var tag = document.createElement('script');
		    tag.src = "https://www.youtube.com/player_api";
		    firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);
	    } else {
	      player = new YT.Player('player', {
	      height: '90%',
	      width: '90%',
	      videoId: videoId,
	        events: {
		        'onReady': onPlayerReady,
		        'onStateChange': onPlayerStateChange
	        }
	      });
	    }
		}
	}
	
	$rootScope.goBack = function(){
    $window.history.back();
    return;
    
		var video = document.getElementById('video');
    var sources = video.getElementsByTagName('source');
    sources[0].src = $scope.item.video;
    video.load();
		video.addEventListener('click',function(){
		  video.play();
		},false);
		$("#player").attr("src", $scope.item.video); 
  }

});