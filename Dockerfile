# Build
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src src
RUN mvn package -DskipTests -B

# Run
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN adduser -D appuser
USER appuser

COPY --from=build /app/target/Agrosotf-crud-0.0.1-SNAPSHOT.jar app.jar

# Railway inyecta PORT; escuchar en todas las interfaces
ENV JAVA_OPTS="-Dserver.address=0.0.0.0"
ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT} -jar app.jar"]
