FROM gradle:8.4.0-jdk17-jammy as builder

# Установка Node.js 18.x и PNPM
RUN apt-get update && apt-get install -y curl
RUN curl -fsSL https://deb.nodesource.com/setup_18.x | bash - && \
    apt-get install -y nodejs
RUN npm install -g pnpm

WORKDIR /app
COPY . .
RUN gradle clean build --no-daemon --stacktrace

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar /app/app.jar
EXPOSE 8080
CMD ["java", "-jar", "/app/app.jar"]
