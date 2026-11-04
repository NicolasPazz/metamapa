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
- Production on AWS: EC2 for the containers and RDS for MySQL.
- Nginx reverse proxy with HTTPS (Let's Encrypt certificates via Certbot) and rate limiting.
- Health checks through Spring Boot Actuator.
- Caffeine caching and database-level pagination.

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
