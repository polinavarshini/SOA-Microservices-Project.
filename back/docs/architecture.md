# Microservice Architecture

## System Overview
The Vanguard Mutual Insurance backend is a highly modular Spring Cloud microservices architecture. It strictly isolates domain contexts to allow independent scaling, localized database scaling (Neon PostgreSQL), and isolated security perimeters. 

## Component Flow Diagram
```text
CLIENT
  |
  v
API Gateway (8080)
  |
  +--> [lb://AUTH-SERVICE]     -> Auth Service (8081)
  |
  +--> [lb://POLICY-SERVICE]   -> Policy Service (8082)
  |
  +--> [lb://CLAIM-SERVICE]    -> Claim Service (8083)
          |
          +-- (Feign + LB) --> Policy Service (Validation & Deductibles)
          |
          +-- (Feign + LB) --> Settlement Service (Payout Execution)
  |
  +--> [lb://SETTLEMENT-SERVICE] -> Settlement Service (8084)
```

## Discovery Server (Eureka)
- **Port**: 8761
- **Responsibility**: Centralized Spring Cloud Netflix Eureka registry.
- **Dependencies**: `spring-cloud-starter-netflix-eureka-server`

## API Gateway
- **Port**: 8080
- **Responsibility**: Single Edge entry point, JWT verification, Rate Limiting, Correlation ID generation, CORS enforcement, and Error Masking.
- **Routing Mechanism**: Dynamic routing using `lb://SERVICE-NAME`.
- **Dependencies**: `spring-cloud-starter-gateway`, `jjwt`

## Auth Service
- **Port**: 8081
- **Responsibility**: User identity, credential hashing (BCrypt), and stateless JWT generation.
- **Database**: Owns the `users` table.
- **APIs**: `POST /api/auth/login`, `POST /api/auth/register`

## Policy Service
- **Port**: 8082
- **Responsibility**: Insurance policy issuance, limit/deductible management, and programmatic coverage validation.
- **Database**: Owns the `policies` table.
- **APIs**: `POST /api/policies`, `GET /api/policies`, `POST /api/policies/{id}/validate-coverage`
- **Security**: Independently validates JWTs.

## Claim Service
- **Port**: 8083
- **Responsibility**: Lifecycle management of claims (Submission -> Approval).
- **Database**: Owns `claims` and `claim_sequence` tables.
- **APIs**: `POST /api/claims`, `GET /api/claims`, `PUT /api/claims/{id}/approve`
- **Inter-service Communication**:
  - Uses OpenFeign + Spring Cloud LoadBalancer to asynchronously request Policy validations.
  - Uses OpenFeign + Spring Cloud LoadBalancer to dispatch final payouts to Settlement.
- **Resilience**: Feign calls are protected by Resilience4j. If downstream services fail, fallback methods trigger `503 Service Unavailable` exceptions, preventing the database transaction from advancing to an invalid state.

## Settlement Service
- **Port**: 8084
- **Responsibility**: Financial payout calculation and finalized settlement ledgers.
- **Database**: Owns `settlements` and `settlement_sequence` tables.
- **APIs**: `POST /api/settlements/process`, `GET /api/settlements/{id}`

## Why This System is Modular
1. **Decentralized Data**: No service accesses another service's database directly. Claim Service does not query the `policies` table; it calls the Policy Service via a defined API interface.
2. **Resilience**: The loss of the Policy Service does not bring down the Auth Service.
3. **Location Transparency**: Eureka and LoadBalancer ensure no microservice knows the physical IP of its peers.
