# 1️⃣ Build stage
FROM gradle:8.7-jdk17 AS builder
WORKDIR /app

# Gradle 캐시 활용
COPY build.gradle settings.gradle ./
COPY gradle gradle
RUN gradle dependencies --no-daemon

# 소스 복사 및 빌드
COPY . .
RUN gradle bootJar --no-daemon

# 2️⃣ Run stage
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
