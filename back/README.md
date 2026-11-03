# Vanguard Automated Commercial Insurance Claim Verification & Settlement Engine

## Project Overview
This project is an academic demonstration of a microservice-based architecture designed to solve delays, manual verification bottlenecks, and transparency issues in the commercial insurance sector. It utilizes automated service-to-service orchestration to instantly validate policy coverages and calculate claim settlements.

## Architecture & Services
The backend is built on Java 17 and Spring Boot 3.3.4, utilizing Spring Cloud 2023.0.3 for distributed topologies. 

- **discovery-server** (Port 8761): Eureka Service Registry.
- **api-gateway** (Port 8080): Edge routing, Rate Limiting, CORS, and Global JWT validation.
- **auth-service** (Port 8081): Issues stateless JWTs (BCrypt).
- **policy-service** (Port 8082): Manages insurance policies and deductibles.
- **claim-service** (Port 8083): Accepts claims and orchestrates validation/settlements via Feign + Resilience4j.
- **settlement-service** (Port 8084): Calculates finalized payouts mathematically.

*Note: Runtime databases use Neon PostgreSQL. Tests use in-memory H2.*

## Technology Stack
- Java 17, Maven
- Spring Boot, Spring Cloud (Eureka, Gateway, OpenFeign, LoadBalancer)
- PostgreSQL (Neon Serverless)
- Resilience4j (Circuit Breakers)
- JJWT (JSON Web Tokens)

## Startup Order
To run the project locally, start the services in this exact order:
1. `discovery-server`
2. `auth-service`
3. `policy-service`
4. `claim-service`
5. `settlement-service`
6. `api-gateway`

## Environment Variables
The following variables must be supplied to the respective `.env` files (ignored by Git):
- `DATABASE_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_SECRET`
- `EUREKA_URL`
- `FRONTEND_URL` (Gateway only)

## Documentation References
Extensive documentation mapping to the project rubrics can be found in the `docs/` directory:
- **Requirements**: `docs/requirements-specification.md`
- **Architecture**: `docs/architecture.md`
- **JWT & Auth**: `docs/authentication.md`
- **Gateway Config**: `docs/gateway.md`
- **Integration Tests**: `docs/integration-testing.md`
- **API Tests (Postman)**: `docs/api-test-guide.md`
- **Rubric Coverage**: `docs/rubric-coverage.md`

## Design Thinking & LinkedIn Output
- **DTI Concepts**: `docs/dti-concepts.md`
- **Survey Data**: `docs/survey.md`
- **LinkedIn Article**: `docs/linkedin-article.md`

*(Placeholder: [INSERT PUBLISHED LINKEDIN ARTICLE URL])*
