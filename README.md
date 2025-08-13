# Popcorn Meter app

Movie rating app for Sky

## Running Postgresql, Prometheus and Grafana locally
Run command
```docker compose up```
in /docker directory.

* Grafana web interface: http://localhost:3000
  * Credentials: admin / admin
  * For now is authentication disabled for simplicity (see `/docker/grafana/grafana.ini`)

* Prometheus admin: http://localhost:9090/query

## Popcorn Meter app
The movie rating app is running on port 8080.

For local development a `spring.profiles.active=dev` must be set.
Other application.yaml files could be created
e.g. for production use (application-prod.yaml containing production setup)

Online API documentation
* API doc - http://localhost:8080/api-docs
* Swagger UI - http://localhost:8080/swagger
* Actuator - http://localhost:8080/actuator

### Junit and Integration tests
Are in the `/test/java/integration` folder. Integration tests are in uk.sky.pm package.

Cucumber feature file is in `/test/resources/features/movie.feature`.

Simple `Run` in JIdea without any profile will execute the tests.
Prerequisite is locally running app.