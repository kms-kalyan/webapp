#!/bin/bash

# Install Unified CloudWatch Agent if not already installed
yum install -y amazon-cloudwatch-agent

# Configure the CloudWatch agent (use Systems Manager Parameter Store or a local config file)
cat <<EOF > /opt/aws/amazon-cloudwatch-agent/etc/amazon-cloudwatch-agent.json
{
  "agent": {
    "metrics_collection_interval": 60,
    "logfile": "/var/log/amazon-cloudwatch-agent.log"
  },
  "metrics": {
    "append_dimensions": {
      "InstanceId": "\${aws:InstanceId}"
    },
    "metrics_collected": {
      "cpu": {
        "measurement": ["cpu_usage_idle"]
      }
    }
  },
  "logs": {
    "logs_collected": {
      "files": [{
        "file_path": "/var/log/messages",
        "log_group_name": "/var/log/messages"
      }]
    }
  }
}
EOF

# Restart CloudWatch Agent service
systemctl restart amazon-cloudwatch-agent.service