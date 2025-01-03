/*
package com.eca.ecommerce.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class LogDirectoryInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(LogDirectoryInitializer.class);
    private final Environment environment;

    public LogDirectoryInitializer(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void run(String... args) throws Exception {
        // Fetch the spring application name from the environment
        String applicationName = environment.getProperty("spring.application.name", "default-service");

        // Define the log directory path
        Path logDir = Paths.get("logs");

        // Create directories if they don't exist
        if (Files.notExists(logDir)) {
            Files.createDirectories(logDir);
            logger.info("Created log directory: {}", logDir.toAbsolutePath());
        }

        // Ensure the log file exists (optional, Logback creates it automatically)
        Path logFile = logDir.resolve(applicationName + ".log");
        if (Files.notExists(logFile)) {
            Files.createFile(logFile);
            logger.info("Created log file: {}", logFile.toAbsolutePath());
        }
    }
}
*/
