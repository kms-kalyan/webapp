#!/bin/bash

sudo mv /tmp/webapp-0.0.1-SNAPSHOT.jar /opt/
sudo mv /tmp/webapp.service /etc/systemd/system/webapp.service
sudo mv /tmp/webapp.path /etc/systemd/system/webapp.path
echo "SELINUX=permissive" | sudo tee /etc/selinux/config
sudo systemctl daemon-reload
sudo systemctl enable webapp.path
sudo systemctl enable webapp.service