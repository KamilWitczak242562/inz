FROM openjdk:22-jdk-slim

WORKDIR /app

COPY build/libs/machine_service-0.0.1-SNAPSHOT.jar /app/machine-service.jar

ENTRYPOINT ["java", "-jar", "/app/machine-service.jar"]

EXPOSE 8082
