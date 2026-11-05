# MetaMapa

Open-source platform for collaborative mapping of geographic events. Organizations (NGOs, universities, government agencies) run their own instance, combine information from several sources and publish curated collections on a map, while protecting the identity of contributors.

Annual integrative project for the Systems Design course at UTN FRBA (2025), built by a team of five.

## Features

- **Multiple data sources:** static datasets (CSV), user contributions, and external APIs or other MetaMapa instances through proxies.
- **Aggregation:** a central service merges facts from every source into a single view and keeps collections updated according to configurable criteria.
- **Consensus algorithms:** multiple mentions, simple majority and absolute, used to filter facts by how many sources confirm them.
- **Multimedia:** images, videos and audio attached to facts.
- **Moderation:** deletion requests with automatic spam detection (TF-IDF and pattern rules).
- **Statistics:** dedicated service for the admin dashboard.
- **Access control:** admin and contributor roles.

## Architecture

Microservices in Java 17 / Spring Boot 3, each with its own MySQL schema, communicating over REST (WebClient):

| Module | Responsibility |
| --- | --- |
| `frontend` | Server-side rendered client (Spring MVC + Thymeleaf), session-based security |
| `backend/agregador` | Aggregates facts and collections from every source |
| `backend/dinamica` | User contributions and multimedia uploads |
| `backend/estatica` | CSV dataset processing |
| `backend/estadistica` | Statistics |
| `backend/users` | Authentication (JWT access and refresh tokens) and users |
| `backend/proxy_ong`, `backend/proxy_metamapa` | Adapters for external sources |

Design patterns used include Factory, Strategy (membership criteria and consensus), Template Method, Adapter and Repository.

## Infrastructure

- Docker images for every service, orchestrated with Docker Compose (10 containers).
- Originally deployed on AWS: EC2 for the containers and RDS for MySQL, behind an Nginx reverse proxy with HTTPS (Let's Encrypt via Certbot) and rate limiting (`docker-compose.prod.yml`).
- Health checks through Spring Boot Actuator.
- Caffeine caching and database-level pagination.

## Free deployment (Render + TiDB Cloud)

**Live demo:** https://metamapa-frontend.onrender.com

After the course ended, the AWS deployment was replaced with free tiers so the project can stay online at no cost and without maintenance:

- **Render (free plan):** each service runs as a free web service, defined in [`render.yaml`](render.yaml). Free services sleep after 15 minutes without traffic, so the first visit after a while takes a few minutes while the services wake up.
- **TiDB Cloud Starter (free plan):** MySQL-compatible database that replaces RDS. Each service keeps its own schema.

Changes made to the project for this:

- **Files stored in the database.** Render's free services have no persistent disk, and the services no longer share a filesystem. Uploaded multimedia is now stored in `dinamica`'s database (the aggregator fetches it from `dinamica` over HTTP) and uploaded CSV datasets in `estatica`'s database. On TiDB each file is limited to 5 MB.
- **Internal token between services.** Free Render services can't use a private network, so they call each other through public URLs. Each backend still only accepts requests from private networks, plus requests that carry the shared `X-Internal-Token` header (`INTERNAL_API_TOKEN`). Without that variable, behavior is unchanged.
- **JVM memory limits** (`JAVA_TOOL_OPTIONS`) and a small connection pool so each service fits in 512 MB.

To deploy a copy: create a TiDB Cloud Starter cluster, create a Blueprint in Render from this repository, then create an environment group `metamapa-db` with `DB_HOST`, `DB_USERNAME` and `DB_PASSWORD` and link it to every service except `metamapa-proxy-metamapa` and `metamapa-frontend`.

## Run locally

Requirements: Docker and Docker Compose.

```bash
cp .env.local.example .env.local   # fill in the values
docker compose up
```

## Tests

Requires Java 17.

```bash
./mvnw test
```

The context tests of each service run against an in-memory H2 database, so no MySQL instance is needed.

## Documentation

Design decisions, diagrams and deliverables are in `entregables/`.
