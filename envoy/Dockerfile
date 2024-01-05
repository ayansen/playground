FROM openjdk:17-jdk-slim-buster
WORKDIR /app
ENV APP_NAME=envoy-control-plane
ENV APP_VERSION=1.0.0
COPY ./build/libs/${APP_NAME}-${APP_VERSION}.jar build/
COPY configs configs/

WORKDIR /app/build
ENTRYPOINT java -jar ${APP_NAME}-${APP_VERSION}.jar
EXPOSE 8080
