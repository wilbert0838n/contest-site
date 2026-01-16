#Build Stage
FROM eclipse-temurin:21-jre-alpine AS build

WORKDIR /app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN chmod +x ./mvnw
RUN ./mvnw clean package -DskipTests

#Runtime stage
FROM eclipse-temurin:21-jre-alpine
ARG PROFILE=dev
ARG APP_VERSION=4.0.0

WORKDIR /app
COPY --from=build /app/target/*.jar /app/

EXPOSE 8080

ENV DB_URL=jdbc:postgresql://contest_db:5432/contest_db

ENV ACTIVE_PROFILE=${PROFILE}
ENV JAR_VERSION=${APP_VERSION}

CMD java -jar -Dspring.profiles.active=${ACTIVE_PROFILE} contest-site-${JAR_VERSION}.jar