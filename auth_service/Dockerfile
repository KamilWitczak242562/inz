FROM openjdk:22-jdk-slim

WORKDIR /app

COPY build/libs/auth_service-0.0.1-SNAPSHOT.jar /app/auth-service.jar

ENTRYPOINT ["java", "-jar", "/app/auth-service.jar"]

EXPOSE 8083
