# Local Docker Environment

Start the default Cassandra service:

```bash
docker compose up -d
```

Run a supported version:

```bash
CASSANDRA_VERSION=3.11 docker compose up -d
CASSANDRA_VERSION=4.0 docker compose up -d
CASSANDRA_VERSION=4.1 docker compose up -d
CASSANDRA_VERSION=5.0 docker compose up -d
```

Legacy per-version compose files are kept for compatibility with older workflows.
