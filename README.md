# quiz-api

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using: 
```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/quiz-api-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.

## Related Guides

- JDBC Driver - PostgreSQL ([guide](https://quarkus.io/guides/datasource)): Connect to the PostgreSQL database via JDBC
- Hibernate ORM with Panache ([guide](https://quarkus.io/guides/hibernate-orm-panache)): Simplify your persistence code for Hibernate ORM via the active record or the repository pattern

## Provided Code

### Hibernate ORM

Create your first JPA entity

[Related guide section...](https://quarkus.io/guides/hibernate-orm)

[Related Hibernate with Panache section...](https://quarkus.io/guides/hibernate-orm-panache)


### RESTEasy Reactive

Easily start your Reactive RESTful Web Services

[Related guide section...](https://quarkus.io/guides/getting-started-reactive#reactive-jax-rs-resources)


### To begin  the development 
```
quarkus create app net.resultrite.api:quiz-api \
    --extension='quarkus-resteasy-reactive-jackson,quarkus-jdbc-postgresql,quarkus-hibernate-orm-panache'
```
### To make the native build

1. Packge 
```
./mvnw package -Dnative -Dquarkus.native.container-build=true

```
2. Build
```
docker build -f src/main/docker/Dockerfile.native-micro -t quarkus/quiz-api:1.0 .
```
3. Run using docker compose 
```
cat <<EOF > docker-compose.yaml
version: '3.1'

services:
  pgdblocal:
    image: postgres:16.1-bookworm
    container_name: pgdblocal
    environment:
      POSTGRES_USER: dbadmin
      POSTGRES_PASSWORD: admin4test
      POSTGRES_DB: postgres
      PGDATA: /var/lib/postgresql/data
    volumes:
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql
      - ./data:/var/lib/postgresql/data
    ports:
      - 5432:5432

  api:
     image: quarkus/quiz-api:2.0
     container_name: api
     depends_on:
     - pgdblocal
     environment:
      LANG: C
     ports:
       - 8080:8080
EOF

docker compose up -d

```

## Secure the API using JWT token
1. Generate the keys for signing 
```
cd src/main/resources
openssl genrsa -out rsaPrivateKey.pem 2048
openssl rsa -pubout -in rsaPrivateKey.pem -out publicKey.pem
openssl pkcs8 -topk8 -nocrypt -inform pem -in rsaPrivateKey.pem -outform pem -out privateKey.pem

```