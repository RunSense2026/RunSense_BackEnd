# 1단계: 빌드 스테이지 (Gradle 8.x + JDK 21 환경)
FROM gradle:8.5-jdk21 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src

# 빌드 시 테스트를 제외하고 가볍게 실행 파일(jar)만 생성합니다.
RUN gradle build --no-daemon -x test

# 2단계: 실행 스테이지 (경량화된 JDK 21 환경)
FROM eclipse-temurin:21-jre-jammy
EXPOSE 8080
RUN mkdir /app

# 1단계 빌드 스테이지에서 만들어진 jar 파일만 쏙 빼와서 복사합니다.
COPY --from=build /home/gradle/src/build/libs/*-SNAPSHOT.jar /app/spring-app.jar

# 컨테이너가 켜질 때 스프링 부트를 실행하는 명령어입니다.
ENTRYPOINT ["java", "-jar", "/app/spring-app.jar"]