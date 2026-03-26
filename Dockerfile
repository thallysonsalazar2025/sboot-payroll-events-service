# Stage 1 - build
FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN ./mvnw -v || true
# Use system maven if mvnw not present
RUN mvn -B -DskipTests clean package

# Stage 2 - runtime
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/target/sboot-payroll-events-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
