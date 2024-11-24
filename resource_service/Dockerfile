FROM openjdk:22-jdk-slim

WORKDIR /app

COPY build/libs/resource_service-0.0.1-SNAPSHOT.jar /app/resource-service.jar

ENTRYPOINT ["java", "-jar", "/app/resource-service.jar"]

EXPOSE 8084
