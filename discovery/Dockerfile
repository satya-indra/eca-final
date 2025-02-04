# Stage 1: Build the JAR File
FROM maven:3.9.6-eclipse-temurin-17 AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml and source code
COPY pom.xml .
COPY src ./src

# Run Maven build to create the JAR
RUN mvn clean package -DskipTests

# Stage 2: Run the Application
FROM amazoncorretto:17

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR from the previous stage
COPY --from=builder /app/target/discovery-0.0.1-SNAPSHOT.jar /app/discovery.jar

# Expose the port (default for Eureka Server)
EXPOSE 8761

# Command to run the Spring Boot application
ENTRYPOINT ["java", "-jar", "discovery.jar"]
