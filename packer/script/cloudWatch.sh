#!/bin/bash

# Update packages
sudo apt-get update -y

# Download and install the Amazon CloudWatch agent
wget https://s3.amazonaws.com/amazoncloudwatch-agent/ubuntu/amd64/latest/amazon-cloudwatch-agent.deb
sudo dpkg -i amazon-cloudwatch-agent.deb

# Create the log file if it doesn't exist
sudo touch /var/log/webapp.log
sudo chown -R csye6225:csye6225 /var/log/webapp.log
sudo chmod 664 /var/log/webapp.log

INSTANCE_ID=$(curl -s http://169.254.169.254/latest/meta-data/instance-id)

# Define the JSON content for CloudWatch agent configuration
cat <<EOF | sudo tee /opt/aws/amazon-cloudwatch-agent/etc/amazon-cloudwatch-agent.json
{
  "logs": {
    "logs_collected": {
      "files": {
        "collect_list": [
          {
            "file_path": "/tmp/webapp.log",
            "log_group_name": "csye6225",
            "log_stream_name": "webapp",
            "timezone": "UTC"
          }
        ]
      }
    }
  },
  "metrics": {
    "append_dimensions": {
      "InstanceId": "$INSTANCE_ID"
    },
    "metrics_collected": {
      "cpu": {
        "measurement": [
          {"name": "cpu_usage_idle", "unit": "Percent"}
        ],
        "metrics_collection_interval": 60
      },
      "disk": {
        "measurement": [
          {"name": "used_percent", "unit": "Percent"}
        ],
        "metrics_collection_interval": 60,
        "resources": [
          "/"
        ]
      },
      "mem": {
        "measurement": [
          {"name": "mem_used_percent", "unit": "Percent"}
        ],
        "metrics_collection_interval": 60
      }
    }
  }
}
EOF

# Start or restart the CloudWatch agent service
sudo systemctl enable amazon-cloudwatch-agent.service
sudo systemctl start amazon-cloudwatch-agent.service

echo "AWS CloudWatch Agent configured and started."