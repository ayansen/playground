# playground
This repository is a playground where I try out different technologies which I'm trying to understand in a little more detail.
It's a gradle project.


## Graphql
This module runs programs where I try out different aspects of graphql.


## Kafka
This module runs programs where I try out different kafka and kstream applications. 


## Envoy Control Plane
This module runs programs where I try out different aspects of envoy control plane which can configure envoy.


## Open API Exemplar
This module runs programs where I try out different aspects of building an openapi app using spring-boot.

## Build
The kafka code uses [docker-compose](./kafka/docker-compose.yml) to spin up  kafka and its dependencies and then runs the integration tests on them to experiment the features.
To achieve that using gradle it uses the docker-compose plugin for gradle and then running the integration tests using the failsafe plugin
To try out the tests run
- `make build`

## Debug
To debug the tests, just run the command
-  Run the test in debug mode from your IDE