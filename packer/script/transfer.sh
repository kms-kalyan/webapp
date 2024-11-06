#!/bin/bash

# Set ownership of the copied JAR file and service
sudo chown -R csye6225:csye6225 /tmp/webapp-0.0.1-SNAPSHOT.jar
sudo chown -R csye6225:csye6225 /tmp/webapp.service

sudo mkdir -p /opt/webapp/app
sudo touch /opt/webapp/app/.env

sudo chown -R csye6225:csye6225 /opt/webapp/app/.env
sudo chmod 644 /opt/webapp/app/.env