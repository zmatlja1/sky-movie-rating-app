# Popcorn Meter app

Movie rating app for Sky

## Prerequisites 
* Running docker daemon locally
* Installed maven 3.x, JDK 21

## Running Postgresql, Prometheus and Grafana locally
Run command
```docker compose up```
in /docker directory. Must be run before application is started.

* Grafana web interface: http://localhost:3000
  * Credentials: admin / admin
  * For now is authentication disabled for simplicity (see `/docker/grafana/grafana.ini`)

* Prometheus admin: http://localhost:9090/query

## Popcorn Meter app
The movie rating app is running on port 8080.

For local development a `spring.profiles.active=dev` must be set.
Other application.yaml files could be created
e.g. for production use (application-prod.yaml containing production setup)

Compile application `mvn clean compile` and run it as Spring Boot app `uk.sky.pm.PopcornMeterApplication`
with `spring.profiles.active=dev`.

Init database data is defined in `app/src/main/resources/db/changes/002_data_init.xml`. 
Pwd is 123 for both users.

Online API documentation
* API doc - http://localhost:8080/api-docs
* Swagger UI - http://localhost:8080/swagger
* Actuator - http://localhost:8080/actuator

Added custom metric add_movie_rating_api_metric_total - count of add movie rating API available in
http://localhost:8080/actuator/prometheus

Exported APIs in Postman collection v2.1 is `postman/SkyPopcornMeter_v2.1.postman_collection`

### Junit and Integration tests
Integration tests are in the `/test/java/integration` folder. 
JUnit tests are in `/test/java/uk.sky.pm`.

Cucumber feature file is in `/test/resources/features/movie.feature`.

Alien movie with id=3 is used for integration test - **!PLEASE DO NOT SET RATING THERE!**

Simple `Run` on feature file in JIdea without any profile will execute the tests.
Prerequisite is locally running app on port 8080.