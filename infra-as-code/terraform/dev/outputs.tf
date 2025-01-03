output "ecs_cluster_id" {
  value = module.ecs.cluster_id
}

output "service_dns" {
  value = aws_route53_record.my_service.fqdn
}