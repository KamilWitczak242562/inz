FROM openjdk:22-jdk-slim

WORKDIR /app

COPY build/libs/planning_service-0.0.1-SNAPSHOT.jar /app/planning-service.jar

ENTRYPOINT ["java", "-jar", "/app/planning-service.jar"]

EXPOSE 8085