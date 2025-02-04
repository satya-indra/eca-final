# Stage 1: Build the JAR File
FROM maven:3.9.6-eclipse-temurin-17 AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml and source code
COPY pom.xml .
COPY src ./src

# Run Maven build to create the JAR
RUN mvn clean package -DskipTests

# Stage 2: Create the final runtime image
FROM amazoncorretto:17

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file from the builder stage
COPY --from=builder /app/target/customer-0.0.1-SNAPSHOT.jar /app/customer.jar

# Expose the port your Spring Boot application will run on (default 8090)
EXPOSE 8090

# Command to run the Spring Boot application
ENTRYPOINT ["java", "-jar", "customer.jar"]
