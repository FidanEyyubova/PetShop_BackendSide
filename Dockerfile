# ---- Build stage: compile and package the Spring Boot jar ----
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Download dependencies first so they are cached until pom.xml changes.
COPY pom.xml .
RUN mvn -q -B dependency:go-offline

COPY src ./src
RUN mvn -q -B clean package -DskipTests

# ---- Run stage: small JRE image, non-root user ----
FROM eclipse-temurin:17-jre
WORKDIR /app

RUN groupadd --system --gid 10001 petshop && useradd --system --uid 10001 --gid petshop petshop \
    && mkdir -p /app/uploads && chown -R petshop:petshop /app

COPY --from=build --chown=petshop:petshop /app/target/*.jar app.jar
# Numeric user, so Kubernetes can verify runAsNonRoot.
USER 10001

ENV PORT=8080 \
    JAVA_OPTS="-XX:MaxRAMPercentage=75"
EXPOSE 8080
# Uploaded images (STORAGE_TYPE=local) are written to /app/uploads; mount a volume there to keep them.

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
