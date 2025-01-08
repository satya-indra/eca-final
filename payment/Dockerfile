# Use the official OpenJDK base image
FROM openjdk:17-jre-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file into the container
COPY target/payment.jar /app/payment.jar

# Expose the port your Spring Boot application will run on (default 8060)
EXPOSE 8060

# Command to run the Spring Boot application
ENTRYPOINT ["java", "-jar", "payment.jar"]
