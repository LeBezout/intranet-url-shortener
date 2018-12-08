# URL Shortener REST Backend

## Description

Expose the REST API.

Talks with the database.

## Implemented with

* [Spring Boot](https://spring.io/projects/spring-boot)
* [Liquibase](http://www.liquibase.org/)
* [Java 8](https://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html)

## Database

One table : `link` to store the _shortened links_ with the following fields:

| Field | Type | Remark |
|-------|------|--------|
| id | `VARCHAR(15 CHAR)` | the internal id |
| target_url | `VARCHAR(255 CHAR)` | the target URL to redirect to |
| created_by | `VARCHAR(255 CHAR)` | the creator of the shortened link |
| created_date | `DATETIME` | the internal id |
| last_updated | `DATETIME` | the internal id |
| private | `BOOLEAN` | is the link only known by the creator |
| access_count | `BIGINT` | the link redirection counter |
