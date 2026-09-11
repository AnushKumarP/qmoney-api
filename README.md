# QMoney Portfolio Returns API

A production-style Spring Boot REST API that calculates total and annualized returns for a portfolio of equities and ranks the results from highest to lowest annualized return.

This repository is a deployable, recruiter-friendly API presentation of the return-calculation logic. It includes input validation, centralized error handling, automated testing, containerization, a health check, and an interactive browser demonstration.

## Key capabilities

- Calculate total return from purchase and selling prices
- Calculate annualized return using the investment holding period
- Rank multiple securities by annualized performance
- Validate incoming portfolio requests
- Return consistent HTTP 400 responses for invalid input
- Serve an interactive demonstration from the application root
- Expose a health endpoint for deployment monitoring
- Build and run consistently using Docker
- Automatically deploy new commits through a Render Blueprint

## Technology stack

| Area | Technology |
| --- | --- |
| Language | Java 17 |
| Framework | Spring Boot 3 |
| API | Spring Web REST |
| Validation | Jakarta Bean Validation |
| Monitoring | Spring Boot Actuator |
| Testing | JUnit 5, MockMvc |
| Build | Maven |
| Containerization | Docker |
| Deployment | Render Blueprint |

## Architecture

| Stage | Component | Responsibility |
| --- | --- | --- |
| 1 | Browser or API client | Sends sample or custom portfolio requests |
| 2 | Portfolio controller | Exposes REST endpoints and accepts JSON input |
| 3 | Jakarta validation | Rejects missing, blank, or non-positive values |
| 4 | Return calculation | Computes total and annualized returns |
| 5 | Ranking | Sorts holdings by annualized return |
| 6 | JSON response | Returns the requested end date and ranked results |
| Error path | Exception handler | Converts invalid requests into HTTP 400 responses |

The application is intentionally stateless. Each request supplies the required trade information, the service performs the calculations in memory, and the API returns the ranked result without storing personal or financial data.

## API endpoints

| Method | Endpoint | Purpose |
| --- | --- | --- |
| `GET` | `/` | Open the interactive browser demonstration |
| `GET` | `/api/portfolio/sample` | Run a portfolio calculation using safe sample data |
| `POST` | `/api/portfolio/returns` | Calculate returns for a supplied portfolio |
| `GET` | `/actuator/health` | Check application health |

## Try the sample endpoint

```bash
curl http://localhost:8080/api/portfolio/sample
```

The response contains the requested end date and a list ranked by annualized return:

```json
{
  "endDate": "2024-01-02",
  "returns": [
    {
      "symbol": "AAPL",
      "annualizedReturn": 0.363,
      "totalReturn": 3.702
    }
  ]
}
```

The sample above is abbreviated for readability. The live endpoint returns all included securities.

## Calculate custom portfolio returns

### Request

```bash
curl --request POST http://localhost:8080/api/portfolio/returns \
  --header "Content-Type: application/json" \
  --data '{
    "endDate": "2024-01-02",
    "trades": [
      {
        "symbol": "AAPL",
        "purchaseDate": "2019-01-02",
        "buyPrice": 39.48,
        "sellPrice": 185.64
      },
      {
        "symbol": "MSFT",
        "purchaseDate": "2019-01-02",
        "buyPrice": 101.12,
        "sellPrice": 370.87
      }
    ]
  }'
```

### Request fields

| Field | Type | Rules |
| --- | --- | --- |
| `endDate` | ISO date | Required; must be later than every purchase date |
| `trades` | Array | Required; must contain at least one trade |
| `symbol` | String | Required; cannot be blank |
| `purchaseDate` | ISO date | Required |
| `buyPrice` | Number | Required; must be greater than zero |
| `sellPrice` | Number | Required; must be greater than zero |

Invalid requests return HTTP `400 Bad Request` with an error message.

## Calculation model

Total return:

```text
(sell price - buy price) / buy price
```

Annualized return:

```text
(1 + total return) ^ (1 / holding period in years) - 1
```

The holding period is derived from the number of days between `purchaseDate` and `endDate`, divided by 365.

## Run locally

### Prerequisites

- Java 17 or later
- Maven 3.9 or later

### Start the application

```bash
git clone https://github.com/AnushKumarP/qmoney-api.git
cd qmoney-api
mvn spring-boot:run
```

Open [http://localhost:8080](http://localhost:8080) in a browser.

## Run the tests

```bash
mvn test
```

The controller test verifies that the sample endpoint responds successfully and returns a ranked portfolio.

## Run with Docker

```bash
docker build -t qmoney-api .
docker run --rm -p 8080:8080 qmoney-api
```

The multi-stage Docker build compiles the application with Maven and produces a smaller Java 17 runtime image. The container runs as a non-root user.

## Project structure

```text
qmoney-api/
├── src/
│   ├── main/
│   │   ├── java/com/anushkumar/qmoney/
│   │   │   ├── QMoneyApplication.java
│   │   │   ├── PortfolioController.java
│   │   │   └── ApiExceptionHandler.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── static/index.html
│   └── test/java/com/anushkumar/qmoney/
│       └── PortfolioControllerTest.java
├── Dockerfile
├── pom.xml
└── render.yaml
```

## Security and privacy

- No API keys or market-data credentials are committed to this repository.
- The demonstration uses fixed historical sample prices.
- The API is stateless and does not persist submitted portfolio information.
- Any future provider credential should be supplied through an environment variable or secret manager, never committed to source control.
- The production container runs as a dedicated non-root user.

## Potential enhancements

- Integrate a market-data provider using securely managed environment variables
- Add OpenAPI/Swagger documentation
- Add service and calculation unit-test coverage
- Add request logging, metrics, and distributed tracing
- Add persistence for saved portfolios
- Add GitHub Actions for automated build and test validation

## Author

**Anush Kumar**  
Software Engineer focused on Java, Spring Boot, REST APIs, microservices, AWS, Kafka, PostgreSQL, and React.

Portfolio: [LogicHarbor.dev](https://logicharbor.dev)
