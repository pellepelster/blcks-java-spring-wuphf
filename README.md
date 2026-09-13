# java-spring-wuphf example

A self-contained Spring Boot application that counts how often it has been visited, shows how it is
put together, and answers in the format the caller asked for. It is its own Gradle build - it is not
part of the blcks build and has its own wrapper, so it can be copied out of this repository and
still work.

## Building

    ./gradlew dist

The archive lands at `build/distributions/java-spring-wuphf.zip`, produced by Gradle's standard `application`
plugin. It unpacks to a single directory holding the start scripts and every runtime dependency:

    java-spring-wuphf/
      bin/java-spring-wuphf          # and .bat for windows
      lib/*.jar

`./gradlew test` runs the unit tests.

## Running

    unzip java-spring-wuphf.zip
    ./java-spring-wuphf/bin/java-spring-wuphf

The application listens on port 8080. `./gradlew bootRun` runs it without packaging.

## What it does

Every request to `/` records a visit and answers with three things: how many visits there have
been, where those visits are kept, and which environment variables the process was given. The page
shows them as the fictional company WUPHF.com; a caller that asks for json gets the same data.

Where the visits are kept depends on whether the service is linked to a database. Linked, they go
into a `visits` table in postgres - one row per visit - so the count survives a restart and is
shared by every instance connected to it. Unlinked, they stay in the process: restarting resets
the count, and two instances count separately.

The application does not find out twice where its visits go. The component that counts them is the
component that describes them, thus the page and the json cannot disagree.

## The database

The application reads the environment variables a blcks link hands over. It expects a link to a
`postgresql-standalone` service named `database1`, for the database `hello-world` it holds:

    BLCKS_DATABASE1_HOST                     the machine the instance runs on
    BLCKS_DATABASE1_PORT                     the port it listens on
    BLCKS_DATABASE1_HELLO_WORLD_DATABASE     the database
    BLCKS_DATABASE1_HELLO_WORLD_USER         its admin user, created with the database
    BLCKS_DATABASE1_HELLO_WORLD_PASSWORD     that user's password

`BLCKS_DATABASE1_HOST` is what the application looks at: set, it starts on the `database` profile
and connects; absent, it starts on the `memory` profile with no datasource at all.

The table is the application's own. Liquibase creates it on start-up from
`src/main/resources/db/changelog/db.changelog-master.yaml`, with the admin user the link named -
so the application owns its schema rather than expecting somebody to have prepared one.

## The environment

The application shows all the environment variables it was given, in two groups: first the
variables of a link, whose names start with `BLCKS_`, then the remaining variables of the process.
This is what makes a link visible: the page shows what the platform put there.

A variable whose **name** holds `PASSWORD`, `PASSWD`, `SECRET`, `TOKEN`, `KEY`, `CREDENTIAL`,
`PRIVATE`, `AUTH`, `SIGNATURE`, `SALT` or `CERT` shows `********` in the place of its value. The
name is always shown, the value never. The decision is made on the name and not on the value,
because a name is a contract and a value is not. A name that only looks like a secret is masked as
well: to mask too much costs a path on a page, to mask too little costs a password.

**This page has no authentication.** It shows the environment of its process because an example
must show what a link gives to an application. Do not do this in a service that holds real data.

## The two representations

The representation follows the request's `Accept` header:

    $ curl -H 'Accept: application/json' localhost:8080/
    {
      "visited" : 1,
      "storage" : {
        "kind" : "database",
        "name" : "PostgreSQL",
        "description" : "One row for each visit in the linked postgres database, ...",
        "persistent" : true,
        "shared" : true,
        "details" : {
          "host" : "10.0.0.42",
          "port" : "5432",
          "database" : "hello-world",
          "user" : "hello-world_admin",
          "url" : "jdbc:postgresql://10.0.0.42:5432/hello-world",
          "table" : "visits"
        }
      },
      "environment" : {
        "link" : [ {
          "name" : "BLCKS_DATABASE1_HELLO_WORLD_PASSWORD",
          "value" : "********",
          "secret" : true
        }, {
          "name" : "BLCKS_DATABASE1_HOST",
          "value" : "10.0.0.42",
          "secret" : false
        } ],
        "other" : [ {
          "name" : "PATH",
          "value" : "/usr/local/sbin:/usr/local/bin",
          "secret" : false
        } ]
      }
    }

    $ curl localhost:8080/
    <!doctype html>
    ...
        <p class="count">2</p>
        <p class="lede">This service has been visited <strong>2</strong> time(s).</p>

Without a link, `storage.kind` is `memory`, `storage.details` is empty and `environment.link` is
empty.

Only an explicit `application/json` returns json. A wildcard (`*/*`, what a plain `curl` sends) and
a browser's `text/html,...,*/*;q=0.8` both get the page, so html is the default rather than the
exception. `visited` stays a number at the top of the json document: it is what the blcks
integration tests read to know that a deployed application runs and counts.

The page is self-contained. It has one style block and one drawing in it, and it asks for no script,
no font and no image, because the machine it runs on can have no route to the internet.
