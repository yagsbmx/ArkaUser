FROM eclipse-temurin:21-jre
WORKDIR /app
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} /app/app.jar
ENV SPRING_PROFILES_ACTIVE=docker \
    SERVER_PORT=8084 \
    SPRING_APPLICATION_NAME=user-service \
    DB_HOST=postgres \
    DB_PORT=5432 \
    DB_NAME=usersdb \
    DB_USERNAME=postgres \
    EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://eureka:8761/eureka/ \
    EUREKA_CLIENT_REGISTER_WITH_EUREKA=true \
    EUREKA_CLIENT_FETCH_REGISTRY=true \
    EUREKA_INSTANCE_PREFER_IP_ADDRESS=true
EXPOSE 8084
ENTRYPOINT ["java","-XX:MaxRAMPercentage=75","-jar","/app/app.jar"]
