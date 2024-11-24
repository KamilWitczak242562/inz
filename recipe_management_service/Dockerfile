FROM openjdk:22-jdk-slim

WORKDIR /app

COPY build/libs/recipe_management_service-0.0.1-SNAPSHOT.jar /app/recipe-management-service.jar

ENTRYPOINT ["java", "-jar", "/app/recipe-management-service.jar"]

EXPOSE 8081
