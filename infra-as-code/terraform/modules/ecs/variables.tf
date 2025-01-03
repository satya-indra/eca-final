variable "cluster_name" {
  description = "The name of the ECS cluster"
  type        = string
}

variable "family" {
  description = "The family of the ECS task definition"
  type        = string
}

variable "container_definitions" {
  description = "The container definitions for the ECS task"
  type = list(object({
    name         = string
    image        = string
    essential    = bool
    memory       = number
    cpu          = number
    environment  = list(object({
      name  = string
      value = string
    }))
    portMappings = list(object({
      containerPort = number
      hostPort      = number
    }))
  }))
}

variable "service_name" {
  description = "The name of the ECS service"
  type        = string
}

variable "desired_count" {
  description = "The desired number of ECS service instances"
  type        = number
}

variable "subnets" {
  description = "The subnets for the ECS service"
  type        = list(string)
}

variable "security_groups" {
  description = "The security groups for the ECS service"
  type        = list(string)
}

variable "cpu" {
  description = "The number of CPU units used by the task"
  type        = number
}

variable "memory" {
  description = "The amount of memory (in MiB) used by the task"
  type        = number
}

variable "container_name" {
  description = "The name of the container"
  type        = string
}

variable "image" {
  description = "The image used for the container"
  type        = string
}

variable "environment" {
  description = "The environment variables for the container"
  type        = list(map(string))
  default = [
    {
      name  = "ENVIRONMENT"
      value = "dev"
    }
  ]
}

variable "port_mappings" {
  description = "The port mappings for the container"
  type        = list(object({
    containerPort = number
    hostPort      = number
  }))
}