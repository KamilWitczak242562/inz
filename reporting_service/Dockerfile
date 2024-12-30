FROM openjdk:22-jdk-slim

WORKDIR /app

RUN apt-get update && apt-get install -y \
    libfreetype6 \
    libfreetype6-dev \
    fontconfig \
    && apt-get clean && rm -rf /var/lib/apt/lists/*

COPY build/libs/reporting_service-0.0.1-SNAPSHOT.jar /app/reporting-service.jar

ENTRYPOINT ["java", "-jar", "/app/reporting-service.jar"]

EXPOSE 8086
