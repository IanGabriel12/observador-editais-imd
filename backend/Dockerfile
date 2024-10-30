FROM eclipse-temurin:21
EXPOSE 8080
WORKDIR /app
COPY target/notice-tracker-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=common,prod", "app.jar"]
