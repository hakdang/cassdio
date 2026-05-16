# Cassdio

Cassdio is a Cassandra studio application managed as a monorepo.

## Structure

- `cassdio-core`: shared Kotlin/Spring domain module
- `cassdio-web`: Spring Boot API application
- `cassdio-web/src/main/webapp`: React, TypeScript, and Vite client
- `docker`: local Cassandra development environment

## Requirements

- JDK 21
- Node.js 22+
- Docker and Docker Compose

## Run

```bash
./gradlew :cassdio-web:bootRun
```

```bash
cd cassdio-web/src/main/webapp
npm install
npm run dev
```

## Build And Test

```bash
./gradlew test ktlintCheck
```

```bash
cd cassdio-web/src/main/webapp
npm install
npm run lint
npm run test
npm run build
```

## Local Cassandra

The default local environment starts Cassandra 5.0:

```bash
docker compose up -d
```

Use another supported Cassandra version with:

```bash
CASSANDRA_VERSION=4.1 docker compose up -d
```

Supported versions: `3.11`, `4.0`, `4.1`, `5.0`.

## APIs

- `GET /api/health`
- `GET /api/version`
- `GET /actuator/health`

## License

Apache License 2.0. See [LICENSE.txt](LICENSE.txt).
