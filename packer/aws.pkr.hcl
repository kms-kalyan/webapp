packer {
  required_plugins {
    amazon = {
      version = ">= 1.0.0, <2.0.0"
      source  = "github.com/hashicorp/amazon"
    }
  }
}

variable "aws_region" {
  type    = string
  default = "us-east-1"
}

variable "source_ami" {
  type    = string
  default = "ami-0866a3c8686eaeeba"  # Ubuntu AMI ID
}

variable "ssh_username" {
  type    = string
  default = "ubuntu"
}

variable "subnet_id" {
  type    = string
  default = "subnet-06c25e1fc37c5c81d"
}

source "amazon-ebs" "ubuntu-ami" {
  region          = var.aws_region
  source_ami      = var.source_ami
  instance_type   = "t2.small"
  ssh_username    = var.ssh_username
  ami_name        = "custom-ubuntu-{{timestamp}}"
  vpc_id          = "vpc-0e87ef6bfc8964ea0"
  subnet_id       = var.subnet_id
  ami_description = "AMI for CSYE6225"
  
  ami_regions = [
    "us-east-1",
  ]

  aws_polling {
    delay_seconds = 120
    max_attempts  = 50
  }

  launch_block_device_mappings {
    delete_on_termination = true
    device_name           = "/dev/sda1"
    volume_size           = 8
    volume_type           = "gp2"
  }
}

build {
  sources = [
    "source.amazon-ebs.ubuntu-ami"
  ]

  # Upload webapp.zip to the instance
  provisioner "file" {
    source      = "../webapp.zip"
    destination = "/tmp/webapp.zip"
  }

  # Run setup script for your application (if needed)
  provisioner "shell" {
    script = "./script/setup.sh"
  }

  # Install and configure the Amazon CloudWatch Agent
  provisioner "shell" {
    inline = [
      # Update packages and install CloudWatch agent (for Ubuntu)
      "sudo apt-get update -y",
      "sudo apt-get install -y amazon-cloudwatch-agent",

      # Create CloudWatch agent configuration (adjust as needed)
      "sudo tee /opt/aws/amazon-cloudwatch-agent/etc/amazon-cloudwatch-agent.json <<EOF",
      "{",
      "\"agent\": { \"metrics_collection_interval\": 60, \"logfile\": \"/var/log/amazon-cloudwatch-agent.log\" },",
      "\"metrics\": { \"append_dimensions\": { \"InstanceId\": \"$(curl http://169.254.169.254/latest/meta-data/instance-id)\" }, \"metrics_collected\": { \"cpu\": { \"measurement\": [\"cpu_usage_idle\"] } } },",
      "\"logs\": { \"logs_collected\": { \"files\": [{\"file_path\": \"/var/log/syslog\", \"log_group_name\": \"/var/log/syslog\"}] } }",
      "} EOF",

      # Enable and start the CloudWatch agent service on boot
      "sudo systemctl enable amazon-cloudwatch-agent.service",
      "sudo systemctl start amazon-cloudwatch-agent.service"
    ]
  }
}

# packer {
#   required_plugins {
#     amazon = {
#       version = ">= 1.0.0, <2.0.0"
#       source  = "github.com/hashicorp/amazon"
#     }
#   }
# }

# variable "aws_region" {
#   type    = string
#   default = "us-east-1"
# }

# variable "source_ami" {
#   type    = string
#   default = "ami-0866a3c8686eaeeba"
# }

# variable "ssh_username" {
#   type    = string
#   default = "ubuntu"
# }

# variable "subnet_id" {
#   type    = string
#   default = "subnet-06c25e1fc37c5c81d"
# }


# source "amazon-ebs" "ubuntu-ami" {
#   region          = var.aws_region
#   source_ami      = var.source_ami
#   instance_type   = "t2.small"
#   ssh_username    = var.ssh_username
#   ami_name        = "custom-ubuntu-{{timestamp}}"
#   vpc_id          = "vpc-0e87ef6bfc8964ea0"
#   subnet_id       = var.subnet_id
#   ami_description = "AMI for CSYE6225"
#   ami_regions = [
#     "us-east-1",
#   ]

#   aws_polling {
#     delay_seconds = 120
#     max_attempts  = 50
#   }

#   launch_block_device_mappings {
#     delete_on_termination = true
#     device_name           = "/dev/sda1"
#     volume_size           = 8
#     volume_type           = "gp2"
#   }
# }

# build {
#   sources = [
#     "source.amazon-ebs.ubuntu-ami"
#   ]

#   provisioner "file" {
#     source      = "../webapp.zip"
#     destination = "/tmp/webapp.zip"
#   }

#   provisioner "shell" {
#     script = "./script/setup.sh"
#   }
# }