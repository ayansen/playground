.PHONY: build pr

build:
	gradle build

pr:
	gradle jar
