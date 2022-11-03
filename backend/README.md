# URL Shortener REST Backend

## Description

Expose the REST API.

Talks with and manage the database.

## Implemented with

* [Spring Boot](https://spring.io/projects/spring-boot)
* [Liquibase](http://www.liquibase.org/)
* [Java 8](https://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html)

## Database

- One table `link` to store the _shortened links_.
- One table `counter` to store the visitors count.

## Setting-up a local development environment

### Docker & other tools

* Install Docker on your system : <https://docs.docker.com/engine/install/>
* Install cURL
* Install [jq](https://stedolan.github.io/jq/)

### Database

We use a [bitnami](https://hub.docker.com/r/bitnami/postgresql) Postgresql image :

```shell
sudo docker run --name urlshortener_postgresql \
  -p 5432:5432 \
  --detach \
  --env POSTGRESQL_USERNAME=demo \
  --env POSTGRESQL_PASSWORD=s3cret \
  --env POSTGRESQL_DATABASE=urlshortener_db \
  bitnami/postgresql:latest
```

:bulb: Validate with `sudo docker exec -it urlshortener_postgresql psql -d urlshortener_db -U demo -c '\l'`

### Directory

We use a [bitnami](https://hub.docker.com/r/bitnami/openldap/) OpenLDAP image :

```shell
sudo docker run --name urlshortener_openldap \
  -p 1389:1389 \
  --detach \
  --env LDAP_ROOT=dc=local,dc=org \
  --env LDAP_ADMIN_USERNAME=admin \
  --env LDAP_ADMIN_PASSWORD=s3cret \
  --env LDAP_USERS=demo1,demo2 \
  --env LDAP_PASSWORDS=demo1,demo2 \
  bitnami/openldap:latest
```

:bulb: Validate with `curl 'ldap://localhost:1389/dc=local,dc=org?*?sub?(objectclass=*)'`

### Run the App

* Maven : `mvn spring-boot:run -Dspring-boot.run.profiles=local`
* Command Line : `java -jar target/url-shortener-1.0.0-SNAPSHOT.jar --spring.profiles.active=local`
* IDE : run `src/main/java/[...]/UrlShortenerApplication.java` with _active profiles_ : `local`

:bulb: Validate with `curl http://localhost:8080/manage/health | jq`

:bulb: [Swagger UI](http://localhost:8080/openapi/swagger-ui/)

### Test the app

* Check credentials `curl --fail -X POST -u "demo1:demo1" http://localhost:8080/api/user/login`
* Shortened Links:
  * Add a new link `curl --fail -X POST -u "demo1:demo1" -H "Content-Type: application/json" -d '{ "target": "https://github.com" }' http://localhost:8080/api/link`
  * Get link infos `curl --fail http://localhost:8080/api/link/{link_id} | jq`
  * Get link target `curl --fail http://localhost:8080/api/link/{link_id}/target`
  * Get the links created by the user "demo1" `curl --fail http://localhost:8080/api/link/createdBy/demo1 | jq`
  * Get the links created by the user "demo1" between the "2020-01-01" and the "2020-12-31" : `curl --fail "http://localhost:8080/api/link?creator=demo1&from=2020-01-01T00:00:00Z&to=2020-12-31T23:59:59Z" | jq`
  * Check redirect in a browser : `http://localhost:8080/redirect/{link_id}`
  * Update link (if owner) : `curl --fail -X PUT -u "demo1:demo1" -H "Content-Type: application/json" -d '{ "id": "{link_id}", "target": "https://github.com" }' http://localhost:8080/api/link`
* Visitors counters:
  * Create new counter `curl --fail -X POST -u "demo1:demo1" http://localhost:8080/api/count?url=https%3A%2F%2Fgithub.com`
  * Get counter `curl http://localhost:8080/api/count/{counter_id} | jq`
  * Get counters created by the user "demo1" `curl --fail http://localhost:8080/api/count/createdBy/demo1 | jq`
  * Increment counter and get `curl http://localhost:8080/api/count/{counter_id}/v`
  * Increment counter and get SVG `curl http://localhost:8080/api/count/{counter_id}/svg`
  * Increment counter and get one pixel `curl http://localhost:8080/api/count/{counter_id}/px/0074CC --output target/pixel.png`
  * Increment counter and get PNG `curl http://localhost:8080/api/count/{counter_id}/png --output target/counter.png`
  * Reset counter (if owner) : `curl --fail -X PUT -u "demo1:demo1" http://localhost:8080/api/count/{counter_id}/reset`
* Aggregated Data:
  * All links with their shortcuts `curl http://localhost:8080/api/report`
  * All links with their shortcuts created by `curl http://localhost:8080/api/report?creator={creator_name}`
