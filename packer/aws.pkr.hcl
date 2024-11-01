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
  default = "ami-0866a3c8686eaeeba"
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

  provisioner "file" {
    source      = "./webapp.service"
    destination = "/tmp/"
  }
  provisioner "file" {
    source      = "./webapp.path"
    destination = "/tmp/"
  }
  provisioner "file" {
    source      = "../target/webapp-0.0.1-SNAPSHOT.jar"
    destination = "/tmp/"
  }
  provisioner "shell" {
    scripts = ["./script/create-user.sh", "./script/install-java.sh",
    "./script/transfer.sh", "./script/cloudWatch.sh", "./script/start_setup.sh"]
  }
  post-processor "manifest" {
    output     = "manifest.json"
    strip_path = true
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