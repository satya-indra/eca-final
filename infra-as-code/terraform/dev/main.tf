provider "aws" {
  region = var.region
}

module "vpc" {
  source = "../modules/vpc"
  cidr_block = "10.0.0.0/16"
  vpc_name = "dev-vpc"
  public_subnets = ["10.0.1.0/24", "10.0.2.0/24"] // fetching it from aws at runtime
  availability_zones = ["ap-south-1"] // need to check it
}

module "ecs" {
  source = "../modules/ecs"
  cluster_name = var.cluster_name
  family = "eca-infra"
  container_definitions = [
    {
      name      = "ms_pg_sql"
      image     = "postgres"
      essential = true
      memory    = 512
      cpu       = 256
      environment = [
        { name = "POSTGRES_USER", value = "eca" },
        { name = "POSTGRES_PASSWORD", value = "eca" },
        { name = "PGDATA", value = "/data/postgres" }
      ]
      portMappings = [
        { containerPort = 5432, hostPort = 5432 }
      ]
    },
    {
      name      = "ms_pgadmin"
      image     = "dpage/pgadmin4"
      essential = true
      memory    = 512
      cpu       = 256
      environment = [
        { name = "PGADMIN_DEFAULT_EMAIL", value = "pgadmin4@pgadmin.org" },
        { name = "PGADMIN_DEFAULT_PASSWORD", value = "admin" },
        { name = "PGADMIN_CONFIG_SERVER_MODE", value = "False" }
      ]
      portMappings = [
        { containerPort = 80, hostPort = 5050 }
      ]
    },
    {
      name      = "zipkin"
      image     = "openzipkin/zipkin"
      essential = true
      memory    = 512
      cpu       = 256
      portMappings = [
        { containerPort = 9411, hostPort = 9411 }
      ]
    },
    {
      name      = "mongo_db"
      image     = "mongo"
      essential = true
      memory    = 512
      cpu       = 256
      environment = [
        { name = "MONGO_INITDB_ROOT_USERNAME", value = "eca" },
        { name = "MONGO_INITDB_ROOT_PASSWORD", value = "eca" }
      ]
      portMappings = [
        { containerPort = 27017, hostPort = 27017 }
      ]
    },
    {
      name      = "mongo_express"
      image     = "mongo-express"
      essential = true
      memory    = 512
      cpu       = 256
      environment = [
        { name = "ME_CONFIG_MONGODB_ADMINUSERNAME", value = "eca" },
        { name = "ME_CONFIG_MONGODB_ADMINPASSWORD", value = "eca" },
        { name = "ME_CONFIG_MONGODB_SERVER", value = "mongodb" }
      ]
      portMappings = [
        { containerPort = 8081, hostPort = 8081 }
      ]
    },
    {
      name      = "zookeeper"
      image     = "confluentinc/cp-zookeeper:latest"
      essential = true
      memory    = 512
      cpu       = 256
      environment = [
        { name = "ZOOKEEPER_SERVER_ID", value = "1" },
        { name = "ZOOKEEPER_CLIENT_PORT", value = "2181" },
        { name = "ZOOKEEPER_TICK_TIME", value = "2000" }
      ]
      portMappings = [
        { containerPort = 2181, hostPort = 22181 }
      ]
    },
    {
      name      = "ms_kafka"
      image     = "confluentinc/cp-kafka:latest"
      essential = true
      memory    = 512
      cpu       = 256
      environment = [
        { name = "KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR", value = "1" },
        { name = "KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR", value = "1" },
        { name = "KAFKA_TRANSACTION_STATE_LOG_MIN_ISR", value = "1" },
        { name = "KAFKA_ZOOKEEPER_CONNECT", value = "zookeeper:2181" },
        { name = "KAFKA_LISTENER_SECURITY_PROTOCOL_MAP", value = "PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT" },
        { name = "KAFKA_ADVERTISED_LISTENERS", value = "PLAINTEXT://localhost:9092" }
      ]
      portMappings = [
        { containerPort = 9092, hostPort = 9092 }
      ]
    },
    {
      name      = "ms-mail-dev"
      image     = "maildev/maildev"
      essential = true
      memory    = 512
      cpu       = 256
      portMappings = [
        { containerPort = 1080, hostPort = 1080 },
        { containerPort = 1025, hostPort = 1025 }
      ]
    },
    {
      name      = "keycloak_web"
      image     = "quay.io/keycloak/keycloak:23.0.7"
      essential = true
      memory    = 512
      cpu       = 256
      environment = [
        { name = "KC_DB", value = "postgres" },
        { name = "KC_DB_URL", value = "jdbc:postgresql://host.docker.internal:5432/keycloak" },
        { name = "KC_DB_USERNAME", value = "eca" },
        { name = "KC_DB_PASSWORD", value = "eca" },
        { name = "KC_HOSTNAME", value = "localhost" },
        { name = "KC_HOSTNAME_PORT", value = "8080" },
        { name = "KC_HOSTNAME_STRICT", value = "false" },
        { name = "KC_HOSTNAME_STRICT_HTTPS", value = "false" },
        { name = "KC_LOG_LEVEL", value = "info" },
        { name = "KC_METRICS_ENABLED", value = "true" },
        { name = "KC_HEALTH_ENABLED", value = "true" },
        { name = "KEYCLOAK_ADMIN", value = "admin" },
        { name = "KEYCLOAK_ADMIN_PASSWORD", value = "admin" }
      ]
      portMappings = [
        { containerPort = 8080, hostPort = 8080 }
      ]
    },
    {
      name      = "elasticsearch"
      image     = "docker.elastic.co/elasticsearch/elasticsearch:8.15.1"
      essential = true
      memory    = 512
      cpu       = 256
      environment = [
        { name = "xpack.security.enabled", value = "false" },
        { name = "discovery.type", value = "single-node" }
      ]
      portMappings = [
        { containerPort = 9200, hostPort = 9200 },
        { containerPort = 9300, hostPort = 9300 }
      ]
    },
    {
      name      = "kibana"
      image     = "docker.elastic.co/kibana/kibana:8.15.1"
      essential = true
      memory    = 512
      cpu       = 256
      portMappings = [
        { containerPort = 5601, hostPort = 5601 }
      ]
    },
    {
      name      = "logstash"
      image     = "docker.elastic.co/logstash/logstash:8.15.1"
      essential = true
      memory    = 512
      cpu       = 256
      portMappings = [
        { containerPort = 5044, hostPort = 5044 }
      ]
    },
    {
      name      = "prometheus"
      image     = "prom/prometheus:latest"
      essential = true
      memory    = 512
      cpu       = 256
      portMappings = [
        { containerPort = 9090, hostPort = 9090 }
      ]
    },
    {
      name      = "grafana"
      image     = "grafana/grafana:latest"
      essential = true
      memory    = 512
      cpu       = 256
      portMappings = [
        { containerPort = 3000, hostPort = 3000 }
      ]
    }
  ]
  service_name = "eca-infra"
  desired_count = 1
  subnets = data.aws_subnets.default.ids
  security_groups = [data.aws_security_group.default.id]
  container_name = ""
  cpu = 0
  environment = module.ecs.container_definitions
  image = ""
  memory = 0
  port_mappings = []
}

resource "aws_route53_zone" "main" {
  name = var.domain_name
}

resource "aws_route53_record" "my_service" {
  zone_id = aws_route53_zone.main.zone_id
  name    = "eca-infra.${var.domain_name}" // eca-infra.dev-eca.com
  type    = "A"

  alias {
    name                   = aws_lb.main.dns_name
    zone_id                = aws_lb.main.zone_id
    evaluate_target_health = true
  }
}

data "aws_subnets" "default" {
  filter {
    name   = "default-for-az"
    values = ["true"]
  }
}

data "aws_security_group" "default" {
  filter {
    name   = "group-name"
    values = ["default"]
  }
  vpc_id = module.vpc.vpc_id
}

resource "aws_lb" "main" {
  name               = "main-lb"
  internal           = false
  load_balancer_type = "application"
  security_groups    = [data.aws_security_group.default.id]
  subnets            = data.aws_subnets.default.ids

  enable_deletion_protection = false
}

output "dns_name" {
  value = aws_lb.main.dns_name
}

output "zone_id" {
  value = aws_lb.main.zone_id
}

