FROM openjdk:17-jdk-slim-buster
WORKDIR /app
ENV APP_NAME=openapi-exemplar
ENV APP_VERSION=1.0
COPY ./openapi/build/libs/${APP_NAME}-${APP_VERSION}.jar build/
WORKDIR /app/build
ENTRYPOINT java -jar ${APP_NAME}-${APP_VERSION}.jar
EXPOSE 8080
