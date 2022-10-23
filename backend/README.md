# URL Shortener REST Backend

## Description

Expose the REST API.

Talks with and manage the database.

## Implemented with

* [Spring Boot](https://spring.io/projects/spring-boot)
* [Liquibase](http://www.liquibase.org/)
* [Java 8](https://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html)

## Database

One table : `link` to store the _shortened links_ and one table _counter_ for the count capability.

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
* IDE : run `src/main/java/[...]/UrlShortenerApplication.java` with _active profiles :_  `local`

:bulb: Validate with `curl http://localhost:8080/manage/health`

### Test the app

* Check credentials `curl --fail -X POST -u "demo1:demo1" http://localhost:8080/api/user/login`
* Add a new link `curl --fail -X POST -u "demo1:demo1" -H "Content-Type: application/json" -d '{ "target": "http://github.com" }' http://localhost:8080/api/link`
* Get link infos `curl --fail http://localhost:8080/api/link/{link_id} | jq`
* Get link target `curl --fail http://localhost:8080/api/link/{link_id}/target`
* Get the links created by the user "demo1" `curl --fail http://localhost:8080/api/link/createdBy/demo1 | jq`
* Get the links created by the user "demo1" between the "2020-01-01" and the "2020-12-31" : `curl --fail "http://localhost:8080/api/link?creator=demo1&from=2020-01-01T00:00:00Z&to=2020-12-31T23:59:59Z" | jq`
* Check redirect in a browser : `http://localhost:8080/redirect/{link_id}`
