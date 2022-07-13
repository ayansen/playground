.PHONY: build pr

build:
	./gradlew clean build

pr:
	./gradlew clean jar
