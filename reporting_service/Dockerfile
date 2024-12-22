FROM openjdk:22-jdk-slim

WORKDIR /app

COPY build/libs/reporting_service-0.0.1-SNAPSHOT.jar /app/reporting-service.jar

ENTRYPOINT ["java", "-jar", "/app/reporting-service.jar"]

EXPOSE 8086
