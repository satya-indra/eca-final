#!/bin/bash
AWS_SERVICE_NAME="config-server-service"
AWS_CLUSTER_NAME="DevConfigCluster"

# Variables
CLUSTER_NAME=$AWS_CLUSTER_NAME
SERVICE_NAME=$AWS_SERVICE_NAME
TASK_DEFINITION_NAME="${AWS_SERVICE_NAME}-task"
ECR_REPOSITORY_NAME=$AWS_ECR_REGISTRY
ECR_REPOR="ecr/config-server"
ECR_IMAGE="$ECR_REPOSITORY_NAME/$ECR_REPOR":latest
DESIRED_COUNT=1 # Number of desired tasks
REGION=$AWS_DEFAULT_REGION


# Check if ECR repository exists
ECR_REPO_CHECK=$(aws ecr describe-repositories --repository-names $ECR_REPOR --region $REGION 2>&1)

echo "$ECR_REPO_CHECK"

if [[ $ECR_REPO_CHECK == *"RepositoryNotFoundException"* ]]; then
    echo "ECR repository $ECR_REPOR does not exist. Creating repository..."
    aws ecr create-repository --repository-name $ECR_REPOR --region $REGION
else
    echo "ECR repository $ECR_REPOR already exists."
fi

# Authenticate Docker to the ECR registry
#aws ecr get-login-password --region $REGION | docker login --username AWS --password-stdin $AWS_ECR_REPO_URI
docker login -u AWS -p $(aws ecr get-login-password --region  $AWS_DEFAULT_REGION) $AWS_ECR_REGISTRY
# Build Docker image
echo "Building Docker image..."
ls -ltr
docker build -t "$ECR_REPOSITORY_NAME/$ECR_REPOR":latest .

# Tag and push the Docker image to ECR
echo "Tagging and pushing Docker image to ECR..."
docker push "$ECR_REPOSITORY_NAME/$ECR_REPOR":latest

# Check if the ECS cluster exists
CLUSTER_STATUS=$(aws ecs describe-clusters --clusters $CLUSTER_NAME --query 'clusters[*].status' --output text)

if [ "$CLUSTER_STATUS" != "ACTIVE" ]; then
    echo "ECS cluster $CLUSTER_NAME does not exist. Creating cluster..."
    aws ecs create-cluster --cluster-name $CLUSTER_NAME
else
    echo "ECS cluster $CLUSTER_NAME already exists."
fi

# Check if the ECS service exists
SERVICE_STATUS=$(aws ecs describe-services --cluster $CLUSTER_NAME --services $SERVICE_NAME --query 'services[*].status' --output text)
SUBNET_IDS=$(aws ec2 describe-subnets --region $REGION --query "Subnets[*].SubnetId" --output text | tr '\t' ',')

# Fetch the Security Group ID
SECURITY_GROUP_ID=$(aws ec2 describe-security-groups --region $REGION --query "SecurityGroups[?GroupName=='sg-default'].GroupId" --output text)


echo "Subnets: $SUBNET_IDS"
echo "Security Group ID: $SECURITY_GROUP_ID"

#if [ "$SERVICE_STATUS" != "ACTIVE" ]; then
    echo "ECS service $SERVICE_NAME does not exist. Creating service..."

    # Create a new ECS service
    # aws ecs create-service \
    #     --cluster $CLUSTER_NAME \
    #     --service-name $SERVICE_NAME \
    #     --task-definition $TASK_DEFINITION_NAME \
    #     --desired-count $DESIRED_COUNT \
    #     --launch-type "EC2" # or FARGATE depending on your use case

aws ecs create-service \
    --cluster $CLUSTER_NAME \
    --service-name $SERVICE_NAME \
    --task-definition $TASK_DEFINITION_NAME \
    --desired-count $DESIRED_COUNT \
    --launch-type FARGATE \
    --network-configuration "awsvpcConfiguration={subnets=[${SUBNET_IDS}],securityGroups=[sg-002bc35cd5af3197c],assignPublicIp=ENABLED}"


#else
#echo "ECS service $SERVICE_NAME already exists."
#fi

# Register a new task definition for the updated image
# TASK_DEFINITION_ARN=$(aws ecs register-task-definition \
#     --family $TASK_DEFINITION_NAME \
#     --container-definitions "[{
#         \"name\": \"$TASK_DEFINITION_NAME\",
#         \"image\": \"$ECR_IMAGE\",
#         \"essential\": true,
#         \"memory\": 512,
#         \"cpu\": 256
#     }]" \
#     --query 'taskDefinition.taskDefinitionArn' --output text)

TASK_DEFINITION_ARN=$(aws ecs register-task-definition \
    --family $TASK_DEFINITION_NAME \
    --network-mode awsvpc \
    --requires-compatibilities FARGATE \
    --cpu 256 \
    --memory 512 \
    --container-definitions "[{
        \"name\": \"$TASK_DEFINITION_NAME\",
        \"image\": \"$ECR_IMAGE\",
        \"essential\": true,
        \"memory\": 512,
        \"cpu\": 256
    }]" \
    --execution-role-arn arn:aws:iam::241533151025:role/ecsTaskExecutionRole)
    #--query 'taskDefinition.taskDefinitionArn' --output text)


echo "New task definition registered: $TASK_DEFINITION_ARN"


# Update the ECS service to use the new task definition
aws ecs update-service \
    --cluster $CLUSTER_NAME \
    --service $SERVICE_NAME \
    --task-definition $TASK_DEFINITION_ARN

echo "ECS service updated to use the new task definition."
