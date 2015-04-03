#!/bin/bash

export PLAY_HOME=/data3/play-2.2.0
export PATH=$PATH:$PLAY_HOME
export PLAY=$PLAY_HOME/play

# for build server
export JENKINS_PROJECT=tz.chatroom
export BUILD_BASE='/data3/workspace/tz.chatroom'

# for staging server
export RUN_BASE='/data3/workspace/tz.chatroom'

echo '1nd args : '$1

if [ $1 == 'compile' ]
then
 echo '1 : cd $BUILD_BASE'
 cd $BUILD_BASE
 echo '1 : compile $PLAY $1'
 $PLAY $1
elif [ $1 == 'stop' ]
then
 echo '2 : $PLAY stop'
 cd $RUN_BASE
 $PLAY stop
elif [ $1 == 'start' ]
then
 echo '/data3/play-2.2.0/play run &'
 echo 'cd $RUN_BASE'
 echo '$PLAY run'
 cd $RUN_BASE
 $PLAY start
else
	echo 'else'
fi