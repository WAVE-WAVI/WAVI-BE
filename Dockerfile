# 1단계: Gradle과 JDK 17을 사용하여 애플리케이션 빌드
FROM gradle:7.5-jdk17 AS build
WORKDIR /app
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle
COPY src ./src
RUN ./gradlew bootJar -x test

# 2단계: JRE만 포함된 가벼운 최종 이미지 생성
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]