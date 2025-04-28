
# Build stage
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# FROM azul/azul/zulu-openjdk-alpine:17-jre
# Run stage
FROM eclipse-temurin:17-jre-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the packaged application JAR file into the container at /app
# Adjust the source path as needed
COPY --from=build /app/target/product-*.jar app.jar

# Make port 8080 available to the world outside this container
# This doesn't actually publish the port, just documents it
EXPOSE 8080

# Define the command to run your application
# This is the command that will run when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]




