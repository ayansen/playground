.PHONY: build pr

build:
	./gradlew build

pr:
	./gradlew jar

openapi_exemplar_docker_build: build
	docker build ./openapi -t ayansen-playground-openapi-exemplar

openapi_exemplar_run: openapi_exemplar_docker_build
	docker run -p 8083:8080 ayansen-playground-openapi-exemplar