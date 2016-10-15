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
sudo sh -c "echo 'export PATH=$PATH:.' >> $HOME_DIR/.bashrc"
sudo sh -c "echo 'export HOME_DIR='$HOME_DIR >> $HOME_DIR/.bashrc"
sudo sh -c "echo 'export SRC_DIR='$SRC_DIR >> $HOME_DIR/.bashrc"
sudo sh -c "echo 'export NODE_ENV=development >> $HOME_DIR/.bashrc"
sudo sh -c "echo 'export PORT=3000 >> $HOME_DIR/.bashrc"
source $HOME_DIR/.bashrc

sudo sh -c "echo '' >> /etc/hosts"
sudo sh -c "echo '127.0.0.1  www.topzone.biz' >> /etc/hosts"
sudo sh -c "echo '127.0.0.1  admin.topzone.biz' >> /etc/hosts"

sudo apt-get install software-properties-common -y
sudo add-apt-repository ppa:ondrej/php -y

sudo apt-get update

sudo apt-get install npm -y
sudo apt-get install git -y
sudo npm install bower -g

sudo locale-gen UTF-8

### [install nginx] ############################################################################################################
sudo apt-get install nginx -y --force-yes

sudo cp -rf $SRC_DIR/nginx/nginx.conf /etc/nginx/nginx.conf
# tz-chatroom
sudo rm -rf /etc/nginx/sites-available/default
sudo cp -Rf $SRC_DIR/nginx/default /etc/nginx/sites-available/default
sudo ln -s /etc/nginx/sites-available/default /etc/nginx/sites-enabled/default
# tz-chatroom-admin
sudo rm -rf /etc/nginx/sites-available/admin
sudo cp -Rf $SRC_DIR/nginx/admin /etc/nginx/sites-available/admin
sudo ln -s /etc/nginx/sites-available/admin /etc/nginx/sites-enabled/admin
# adminlte
sudo rm -rf /etc/nginx/sites-available/adminlte
sudo cp -Rf $SRC_DIR/nginx/adminlte /etc/nginx/sites-available/adminlte
sudo ln -s /etc/nginx/sites-available/adminlte /etc/nginx/sites-enabled/adminlte

if [ "$SENV" = "aws" ]; then
	sudo sed -i "s/\/vagrant\/web_apps/\/home\/ubuntu\/tz-chatroom\/web_apps/g" /etc/nginx/sites-available/default
	sudo sed -i "s/\/vagrant\/web_apps/\/home\/ubuntu\/tz-chatroom-admin\/web_apps/g" /etc/nginx/sites-available/admin
fi

# curl http://127.0.0.1:80
sudo service nginx stop

### [install redis] ############################################################################################################

### [install php] ############################################################################################################
sudo apt-get install php7.0-fpm -y
sudo apt-get install php7.0-redis -y
sudo apt-get install php-xdebug -y
sudo apt-get install php-curl -y
sudo service php7.0-fpm stop 
sudo cp -rf $SRC_DIR/php70/php.ini /etc/php/7.0/fpm/php.ini

sudo cp -rf $SRC_DIR/php70/admin.conf /etc/php/7.0/fpm/pool.d/admin.conf
sudo cp -rf $SRC_DIR/php70/adminlte.conf /etc/php/7.0/fpm/pool.d/adminlte.conf

### [install socket.io] ############################################################################################################


### [open firewalls] ############################################################################################################
ufw allow "Nginx Full"
sudo iptables -I INPUT -p tcp --dport 21 -j ACCEPT
sudo iptables -I INPUT -p tcp --dport 22 -j ACCEPT
sudo iptables -I INPUT -p tcp --dport 80 -j ACCEPT
sudo iptables -I INPUT -p tcp --dport 443 -j ACCEPT
sudo iptables -I INPUT -p tcp --dport 3002 -j ACCEPT
sudo iptables -I INPUT -p tcp --dport 3306 -j ACCEPT
sudo iptables -I INPUT -p tcp --dport 9000 -j ACCEPT
sudo iptables -I INPUT -p tcp --dport 9005 -j ACCEPT
sudo iptables -I INPUT -p tcp --dport 8090 -j ACCEPT
sudo service iptables save
sudo service iptables restart

### [start services] ############################################################################################################
sudo mkdir -p /tmp/uploads
sudo ln -s /tmp/uploads /vagrant/web_apps/uploads
sudo chown -Rf vagrant:vagrant /tmp/uploads
sudo chmod -Rf 777 /tmp/uploads

#redis -h localhost -P 3306 -u root -p
sudo /etc/init.d/redis restart  
sudo service php7.0-fpm restart
sudo service nginx start

#curl http://192.168.82.120

# change to utc in redis
#https://andromedarabbit.net/wp/redis%EC%9D%98-%EC%8B%9C%EA%B0%84%EB%8C%80-%EB%B0%94%EA%BE%B8%EA%B8%B0/
#shell> redis_tzinfo_to_sql /usr/share/zoneinfo | redis -u root -p redis

exit 0
