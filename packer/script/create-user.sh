#!/bin/bash

# Create a system user for the application
sudo groupadd csye6225
sudo useradd -r -g csye6225 -s /usr/sbin/nologin csye6225 -d /home/csye6225

# Create the user's home directory
sudo mkdir -p /home/csye6225/
sudo chown csye6225:csye6225 /home/csye6225/