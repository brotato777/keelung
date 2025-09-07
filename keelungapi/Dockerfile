# 1️⃣ Build 前端階段
FROM node:20-alpine AS frontend-build
WORKDIR /app/frontend
COPY ../frontend/package*.json ./
RUN npm install
COPY ../frontend .
RUN npm run build

# 2️⃣ Build 後端階段
FROM maven:3.9.2-eclipse-temurin-17 AS backend-build
WORKDIR /app/backend
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src

# 將前端 build 複製到後端 resources/static
COPY --from=frontend-build /app/frontend/dist ./src/main/resources/static

RUN mvn clean package -DskipTests

# 3️⃣ Production 運行
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY --from=backend-build /app/backend/target/keelungapi-0.0.1-SNAPSHOT.jar app.jar
ENV PORT=8080
ENTRYPOINT ["java","-jar","/app/app.jar","--server.port=${PORT}"]
