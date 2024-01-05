.PHONY: build pr

build:
	./gradlew build

pr:
	./gradlew jar

envoy_docker_build: build
	docker build ./envoy -t ayansen-playground-envoy-control-plane

openapi_exemplar_docker_build: build
	docker build ./openapi -t ayansen-playground-openapi-exemplar

openapi_exemplar_run: openapi_exemplar_docker_build
	docker run -p 8083:8080 ayansen-playground-openapi-exemplar


envoy_run: envoy_docker_build
	docker run -p 8083:8080 -p 8000:8000 ayansen-playground-envoy-control-plane