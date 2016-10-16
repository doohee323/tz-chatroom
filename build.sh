#!/bin/bash

#export PLAY=/usr/local/bin/play

# for build server
export BUILD_BASE='/tz-chatroom'

# for staging server
export RUN_BASE='/tz-chatroom'

echo '1nd args : '$1
echo '$RUN_BASE : '$RUN_BASE
 
if [ $1 == 'compile' ]
then
	echo '1 : cd '$BUILD_BASE
	cd $BUILD_BASE
	git pull origin master
	echo '1 : compile 'play $1
	#play clean compile stage
	play compile stage
	play clean dist
	cd dist
	unzip tz_chatroom-1.0-SNAPSHOT.zip
	cd tz_chatroom-1.0-SNAPSHOT
	chmod 777 start
elif [ $1 == 'stop' ]
then
	echo '2 : cd '$RUN_BASE
	cd $RUN_BASE
	play stop
elif [ $1 == 'start' ]
then
	echo '3 : cd '$RUN_BASE
	cd $RUN_BASE
	rm -rf /tz-chatroom/./dist/tz_chatroom-1.0-SNAPSHOT/RUNNING_PID 
	play start -Dhttp.port=9000 &
	#./start -Xms512M -Xmx1024m -javaagent:/home/ubuntu/newrelic/newrelic.jar &
	#./dist/tz_chatroom-1.0-SNAPSHOT/start -Dconfig.resource=application.conf -Dhttp.port=9000 &
else
	echo 'else'
fi