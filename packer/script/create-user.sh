#!/bin/bash

# #Creating group with csye6225 name
# sudo groupadd csye6225
# #Creating nologin user csye6225
# sudo adduser csye6225 --shell /usr/sbin/nologin -g csye6225

# Create a system user for the application
sudo groupadd csye6225
sudo useradd -r -g csye6225 -s /usr/sbin/nologin csye6225 -d /home/csye6225

# Create the user's home directory
sudo mkdir -p /home/csye6225/
sudo chown csye6225:csye6225 /home/csye6225/