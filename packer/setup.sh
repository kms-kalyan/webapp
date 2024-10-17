#!/bin/bash

# Update system and install required packages
sudo apt-get update
sudo apt-get install -y openjdk-17-jdk maven unzip
sudo useradd -m -U -d /opt/tomcat -s /bin/false tomcat
sudo wget https://downloads.apache.org/tomcat/tomcat-10/v10.1.31/bin/apache-tomcat-10.1.31.tar.gz -P /tmp
sudo tar -xvf /tmp/apache-tomcat-10.1.31.tar.gz -C /opt/tomcat --strip-components=1
sudo chown -R tomcat:tomcat /opt/tomcat

# Setup PostgreSQL (Ensure PostgreSQL is installed)
sudo apt-get install -y postgresql postgresql-contrib
sudo systemctl start postgresql
sudo systemctl enable postgresql
sudo -u postgres psql -c "ALTER USER postgres WITH PASSWORD 'Postgres@55';"
# sudo -u postgres psql -c "CREATE DATABASE appdb;"

# Create a system user for the application
sudo groupadd csye6225
sudo useradd -r -g csye6225 -s /usr/sbin/nologin csye6225 -d /home/csye6225

# Create the user's home directory
sudo mkdir -p /home/csye6225/
sudo chown csye6225:csye6225 /home/csye6225/

# Copy and unzip webapp.zip to the appropriate location
sudo cp /tmp/webapp.zip /home/csye6225/webapp.zip
cd /home/csye6225/
sudo unzip webapp.zip -d webapp

# Change ownership of the webapp directory to the correct user
sudo chown -R csye6225:csye6225 /home/csye6225/webapp

# Build the application using Maven
cd /home/csye6225/webapp
sudo -u csye6225 mvn clean package

# Locate the generated JAR file (assuming target directory)
JAR_FILE=$(find target -name "*.jar" | head -n 1)

# Set environment variables for database credentials
echo "SPRING_DATASOURCE_USERNAME=postgres" | sudo tee -a /etc/environment
echo "SPRING_DATASOURCE_PASSWORD=Postgres@55" | sudo tee -a /etc/environment

# Reload environment variables
source /etc/environment

# Create a systemd service file for the Spring Boot application
echo "[Unit]
Description=Spring Boot Application

[Service]
User=csye6225
EnvironmentFile=/etc/environment
ExecStart=/usr/bin/java -jar /home/csye6225/webapp/$JAR_FILE

[Install]
WantedBy=multi-user.target" | sudo tee /etc/systemd/system/webapp.service

# Reload systemd to register the new service
sudo systemctl daemon-reload

# Enable the webapp service to start at boot
sudo systemctl enable webapp.service

# Start the webapp service
sudo systemctl start webapp.service

# Check the status of the webapp service
sudo systemctl status webapp.service
