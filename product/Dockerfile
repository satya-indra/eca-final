# Stage 1: Build the JAR File
FROM maven:3.9.6-eclipse-temurin-17 AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml and source code
COPY pom.xml .
COPY src ./src

# Run Maven build to create the JAR
RUN mvn clean package -DskipTests

# Use the official OpenJDK base image
FROM amazoncorretto:17

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file into the container
COPY --from=builder  /app/target/payment-0.0.1-SNAPSHOT.jar /app/payment.jar

# Expose the port your Spring Boot application will run on (default 8050)
EXPOSE 8050

# Command to run the Spring Boot application
ENTRYPOINT ["java", "-jar", "payment.jar"]