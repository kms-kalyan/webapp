# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory in the container to /app
WORKDIR /app

# Add the current directory contents into the container at /app
ADD target/*.jar /app/target/webapp-0.0.1-SNAPSHOT.jar

# Expose port 8080 (or whatever port your app runs on)
EXPOSE 8081

# Run the Java application when the container launches
CMD ["java", "-jar", "target/webapp-0.0.1-SNAPSHOT.jar"]