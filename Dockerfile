# =============================
# 1. Build Frontend (React + Vite + Tailwind)
# =============================
FROM node:20-alpine AS frontend-builder
WORKDIR /frontend

# 複製前端檔案
COPY keelung-sights-frontend/package*.json ./
RUN npm install

COPY keelung-sights-frontend/ ./
RUN npm run build

# =============================
# 2. Build Backend (Spring Boot)
# =============================
FROM maven:3.9.9-eclipse-temurin-17 AS backend-builder
WORKDIR /backend

# 複製後端專案
COPY keelungapi/pom.xml .
COPY keelungapi/src ./src

# 複製前端 build 後的靜態檔案，塞進 Spring Boot resources
COPY --from=frontend-builder /frontend/dist ./src/main/resources/static

# 打包 Spring Boot 可執行 jar
RUN mvn clean package -DskipTests

# =============================
# 3. Runtime Image
# =============================
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# 從 backend-builder 複製 jar
COPY --from=backend-builder /backend/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
