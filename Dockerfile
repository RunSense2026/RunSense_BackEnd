# JDK 21 환경
FROM gradle:8.5-jdk21 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src

RUN chmod +x gradlew
RUN ./gradlew build -x test

FROM eclipse-temurin:21-jre-jammy
EXPOSE 8080
RUN mkdir /app

COPY --from=build /home/gradle/src/build/libs/*-SNAPSHOT.jar /app/spring-app.jar

ENTRYPOINT ["java", "-jar", "/app/spring-app.jar"]