variable "dockerhub_username" {
  type = string
}

variable "dockerhub_password" {
  type = string
}

variable "docker_image_tag" {
  type = string
}

variable "nomad_environment" {
  type = string
  validation {
    condition     = var.nomad_environment == "development" || var.nomad_environment == "production"
    error_message = "The nomad_environment value must be either development or production."
  }
}

job "test-code-quality-tools-api" {
  region = "us-east-1"

  # Currently set to only use one DC, as there's only one replica defined.
  datacenters = ["aws-${var.nomad_environment}"]

  type = "service"

  vault {
    policies = ["test-code-quality-tools-api-secret-reader"]
  }

  group "test-code-quality-tools-api" {
    count = 1

    constraint {
      attribute = "${meta.application}"
      value     = "test-code-quality-tools-api"
    }

    # Ensure that the new deployment is healthy before switching
    update {
      # Only update one service at a time
      max_parallel = 1

      # Service is considered healthy when its check is healthy
      health_check = "checks"

      # Minumum time before proceeding to next update
      min_healthy_time = "15s"

      # Max time for the service to become healthy before the deployment is failed
      healthy_deadline = "5m"
      progress_deadline = "15m"

      # Revert if deployment fails
      auto_revert = true
    }

    # Using a 'to' will map the exposed container port to an external ephemeral port, 'static' creates a one-to-one port mapping to the exposed port
    # More details are available here: https://www.nomadproject.io/docs/drivers/docker#networking
    network {
      mode = "bridge"

      port "http" {
        static = 8080
        to     = 8080
      }
    }

    task "test-code-quality-tools-api-docker" {

      service {
        name = "test-code-quality-tools-api"
        tags = ["http", "application-metrics"]
        canary_tags = ["http", "application-metrics", "canary"]
        port = "http"

        check {
          name     = "test-code-quality-tools-api health check"
          type     = "http"
          path     = "/health"
          interval = "5s"
          timeout  = "30s"
        }
      }

      driver = "docker"

      # Warning: The memory value is in MB vs. the available memory for the EC2 instance that is specified in MiB.
      resources {
        memory = var.nomad_environment == "production" ? 1900 : 1900
        memory_max = var.nomad_environment == "production" ? 4000 : 2000
      }

      config {
        auth {
          username = var.dockerhub_username
          password = var.dockerhub_password
        }
        # The docker image to run
        image = "triplelift/test-code-quality-tools-api:${var.docker_image_tag}"
        # The ports that are going to be mapped in the "Service" stanza
        ports = ["http"]

        logging {
          type = "awslogs"
          config {
            awslogs-region = "us-east-1"
            awslogs-group = "test-code-quality-tools-api-${var.nomad_environment}"
            awslogs-create-group = true
          }
        }
      }

      template {
        data = <<EOF
SPRING_PROFILES_ACTIVE="${var.nomad_environment}"
EOF
        destination = "/secrets/file.env"
        env         = true
      }

    }
  }
}
