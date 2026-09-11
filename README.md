# QMoney API demo

A deployable Spring Boot REST presentation of the annualized-return logic in the original QMoney coursework project.

## Run locally

```bash
mvn spring-boot:run
```

Open `http://localhost:8080` or call:

- `GET /api/portfolio/sample`
- `POST /api/portfolio/returns`
- `GET /actuator/health`

The live sample uses fixed historical prices so no market-data credential is exposed. If live quotes are added later, store the provider token in an environment variable and never commit it.

## Deploy on Render

Create a Blueprint from this repository and select `render.yaml`. Render builds the service with Docker and checks `/actuator/health`.
