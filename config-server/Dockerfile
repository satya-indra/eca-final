# Stage 1: Build the application using Maven and include AWS CLI
FROM maven:3.8.8-amazoncorretto-17-al2023 AS build

# Install AWS CLI v2
# RUN apt-get update && apt-get install -y \
#     curl unzip && \
#     curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip" && \
#     unzip awscliv2.zip && \
#     ./aws/install && \
#     rm -rf awscliv2.zip aws

# Set the working directory inside the container
WORKDIR /home/app

# Copy the pom.xml file and the source code to the container
COPY pom.xml /home/app
COPY src /home/app/src

# Build the application using Maven
RUN mvn clean package -DskipTests

# Stage 2: Create a lightweight image for running the application
FROM openjdk:17-alpine


# Copy the built artifact from the 'build' stage
COPY --from=build /home/app/target/config-server-*.jar /usr/local/lib/config-server.jar

# Expose the port the app runs on
EXPOSE 8888

# Run the application
CMD ["java", "-jar", "/usr/local/lib/config-server.jar"]
