
variable "cidr_block" {
  description = "The CIDR block for the VPC"
  type        = string
}

variable "vpc_name" {
  description = "The name of the VPC" // need to add vpc name here
  type        = string
}

variable "public_subnets" {
  description = "A list of public subnet CIDR blocks" // need to find it.
  type        = list(string)
}

variable "availability_zones" {
  description = "A list of availability zones"
  type        = list(string)
}