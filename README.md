# Tz-ChatRoom app

http://chatroom.topzone.biz

## Features
	- Websocket.io with play!
	- angular.js
	- Redis for storage and pub/sub
	- reconnecting-websocket.min.js
	- simple free memory check (/tz-chatroom/conf/application.conf tz.maxMemory=1000)

install Tz-Chat app on vagrant

## Requirement in your pc
```	
	install vagrant
	vagrant box add trusty https://oss-binaries.phusionpassenger.com/vagrant/boxes/latest/ubuntu-14.04-amd64-vbox.box
	
	vi /etc/hosts
	192.168.82.120     chatroom.topzone.biz
	
```

## Run vagrant
```
	cd ~/tz-chatroom
	vagrant destroy -f && vagrant up
```

## Configuration
	- play!
		- redis: /tz-chatroom/conf/application.conf
		 
			redis.url="~~~"
		
	- angular
		- websocket: /tz-chatroom/web/scripts/app.js
		 
			var config = {
				debug: true,		-> UI debug console

## Build for local env.
### Install
```
	git clone https://github.com/doohee323/tz-chatroom.git
	cd tz-chatroom/web
	npm install
	bower install
	grunt build
```
### Start for local env.
```
	cd tz-chatroom
	play clean compile stage
	play start -Dhttp.port=9000
	
	cf. grunt
	cd tz-chatroom/web
	grunt serve
```

### Start for production 
```
	cd tz-chatroom
	play clean compile stage
	cd tz-chatroom
	play clean dist
	tz-chatroom/dist> unzip tz_chatroom-1.0-SNAPSHOT.zip
	tz-chatroom/dist> cd tz_chatroom-1.0-SNAPSHOT
	chmod 777 start
	./start -Xms512M -Xmx1024m &
	
# build.sh compile
# ./start -Xms512M -Xmx1024m -javaagent:/home/ubuntu/newrelic/newrelic.jar &
# ./start -Xms512M -Xmx1024m -javaagent:/home/ubuntu/newrelic/newrelic.jar -Dconfig.resource=prod.conf -Dlogger.file=/home/ubuntu/tz-chatroom/prod-logger.xml & 
```

## Etc
```
	- debugging with runscope
	https://www.runscope.com/stream/p146veqmxldc

	- server monitoring with newrelic
	https://rpm.newrelic.com/accounts/597359/applications/5584593
	
	- performance test with jmeter
	/tz-chatroom/conf/tz-chatroom.jmx
```

	
	
	

