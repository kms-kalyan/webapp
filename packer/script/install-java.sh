#!/bin/bash

# Update system and install required packages
sudo apt-get update
sudo apt-get install -y openjdk-17-jdk maven unzip
sudo useradd -m -U -d /opt/tomcat -s /bin/false tomcat
sudo wget https://downloads.apache.org/tomcat/tomcat-10/v10.1.31/bin/apache-tomcat-10.1.31.tar.gz -P /tmp
sudo tar -xvf /tmp/apache-tomcat-10.1.31.tar.gz -C /opt/tomcat --strip-components=1
sudo chown -R tomcat:tomcat /opt/tomcat