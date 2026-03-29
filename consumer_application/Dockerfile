FROM eclipse-temurin
WORKDIR /app
COPY target/senla-jar-with-dependencies.jar app.jar
ENTRYPOINT ["java", "-cp", "app.jar", "org.example.consumer_application.Main"]