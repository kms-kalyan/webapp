#!/bin/bash

#Creating group with csye6225 name
sudo groupadd csye6225
#Creating nologin user csye6225
sudo adduser csye6225 --shell /usr/sbin/nologin -g csye6225