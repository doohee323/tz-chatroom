#!/usr/bin/env bash

set -x

export SENV=$1

export USER=vagrant  # for vagrant
export PROJ_NAME=tz-chatroom
export PROJ_DIR=/vagrant
export NODE_ENV=development
export SRC_DIR=/vagrant/resources  # for vagrant

if [ "$SENV" = "aws" ]; then
	USER=ubuntu
	SRC_DIR=/home/ubuntu/tz-chatroom/resources
fi

export HOME_DIR=/home/$USER

sudo sh -c "echo '' >> $HOME_DIR/.bashrc"
sudo sh -c "echo 'export PATH=$PATH:.:~/play-2.1.3' >> $HOME_DIR/.bashrc"
sudo sh -c "echo 'export HOME_DIR='$HOME_DIR >> $HOME_DIR/.bashrc"
sudo sh -c "echo 'export PROJ_DIR='$PROJ_DIR >> $HOME_DIR/.bashrc"
sudo sh -c "echo 'export SRC_DIR='$SRC_DIR >> $HOME_DIR/.bashrc"
source $HOME_DIR/.bashrc

sudo sh -c "echo '' >> /etc/hosts"
sudo sh -c "echo '127.0.0.1  chatroom.topzone.biz' >> /etc/hosts"

#sudo apt-get update

sudo apt-get install openjdk-7-jdk curl -y
sudo apt-get install npm -y
sudo apt-get install git -y
sudo apt-get install unzip
sudo npm install bower -g

sudo locale-gen UTF-8

### [install nginx] ############################################################################################################
sudo apt-get install nginx -y --force-yes

sudo cp -rf $SRC_DIR/nginx/nginx.conf /etc/nginx/nginx.conf
# tz-chatroom
sudo rm -rf /etc/nginx/sites-available/tz-chatroom.conf
sudo rm -rf /etc/nginx/sites-enabled/tz-chatroom.conf
sudo cp -Rf $SRC_DIR/nginx/tz-chatroom.conf /etc/nginx/sites-available/tz-chatroom.conf
sudo ln -s /etc/nginx/sites-available/tz-chatroom.conf /etc/nginx/sites-enabled/tz-chatroom.conf

if [ "$SENV" = "aws" ]; then
	sudo sed -i "s/\/vagrant\/web_apps/\/home\/ubuntu\/tz-chatroom\/web_apps/g" /etc/nginx/sites-available/default
	sudo sed -i "s/\/vagrant\/web_apps/\/home\/ubuntu\/tz-chatroom-admin\/web_apps/g" /etc/nginx/sites-available/admin
fi

# curl http://127.0.0.1:80
sudo service nginx stop

### [install redis] ############################################################################################################
cd $HOME_DIR
wget http://download.redis.io/redis-stable.tar.gz
tar xvzf redis-stable.tar.gz
cd redis-stable
make
sudo cp src/redis-server /usr/local/bin/
sudo cp src/redis-cli /usr/local/bin/
redis-server &

### [install play!] ############################################################################################################
cd $HOME_DIR
wget https://downloads.typesafe.com/play/2.1.3/play-2.1.3.zip
unzip play-2.1.3.zip
cd $PROJ_DIR
bash build.sh compile

### [make web env] ############################################################################################################
cd $PROJ_DIR/web
npm install
bower install

### [open firewalls] ############################################################################################################
ufw allow "Nginx Full"
sudo iptables -I INPUT -p tcp --dport 21 -j ACCEPT
sudo iptables -I INPUT -p tcp --dport 22 -j ACCEPT
sudo iptables -I INPUT -p tcp --dport 80 -j ACCEPT
sudo iptables -I INPUT -p tcp --dport 443 -j ACCEPT
sudo iptables -I INPUT -p tcp --dport 9000 -j ACCEPT
sudo service iptables save
sudo service iptables restart

### [start services] ############################################################################################################
cd $PROJ_DIR
sudo /etc/init.d/redis restart  
sudo service nginx start
bash build.sh start

#curl http://192.168.82.120

# change to utc in redis
#https://andromedarabbit.net/wp/redis%EC%9D%98-%EC%8B%9C%EA%B0%84%EB%8C%80-%EB%B0%94%EA%BE%B8%EA%B8%B0/
#shell> redis_tzinfo_to_sql /usr/share/zoneinfo | redis -u root -p redis

exit 0
