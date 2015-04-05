#!/bin/bash

export PLAY=/usr/local/sbin/play

# for build server
export JENKINS_PROJECT=tz.chatroom
export BUILD_BASE='/home/ubuntu/tz.chatroom'

# for staging server
export RUN_BASE='/home/ubuntu/tz.chatroom'

echo '1nd args : '$1
echo '$PLAY : '$PLAY
echo '$RUN_BASE : '$RUN_BASE
 
if [ $1 == 'compile' ]
then
	echo '1 : cd '$BUILD_BASE
	cd $BUILD_BASE
	git pull origin master
	echo '1 : compile '$PLAY $1
	#$PLAY clean compile stage
	$PLAY compile stage
elif [ $1 == 'stop' ]
then
	echo '2 : cd '$RUN_BASE
	cd $RUN_BASE
	$PLAY stop
elif [ $1 == 'start' ]
then
	echo '3 : cd '$RUN_BASE
	cd $RUN_BASE
	$PLAY start -Dhttp.port=9000 & 
	#.$RUN_BASE/target/start -Dconfig.resource=stage.conf -Dhttp.port=9000 &
else
	echo 'else'
fi