FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8080 6565

ENTRYPOINT ["java", "-jar", "app.jar"]