# Requirements Specification
**Project**: Automated Commercial Insurance Claim Verification & Settlement Engine

## 1. Problem Statement
Commercial insurance claims processing is often slow, opaque, and highly manual. Claimants (policyholders) struggle with long waiting periods and lack of status visibility, while adjusters are overwhelmed by manual coverage verifications against policy limits.

## 2. Existing Problems / Challenges
- **Verification Delays**: Cross-referencing claim amounts against deductibles and policy limits requires manual lookup.
- **Opaque Tracking**: Policyholders cannot see the real-time status of their submissions.
- **Settlement Errors**: Manual calculation of payouts (Claim - Deductible) frequently leads to delays or accounting errors.
- **Monolithic Bottlenecks**: Legacy systems fail to scale under high claim volumes (e.g., post-disaster scenarios).

## 3. Proposed Solution
A distributed Microservices Architecture that isolates Identity, Policies, Claims, and Settlements into independent, highly cohesive services. The system relies on automated Feign-based orchestration to instantly validate claims against policies and calculate zero-floor settlements automatically.

## 4. Objectives
- Achieve instantaneous cross-service coverage validation.
- Enforce strict identity boundaries via JWT and API Gateway filtering.
- Provide a resilient processing engine using Circuit Breakers to prevent cascading failures.
- Automate the mathematical settlement layer.

## 5. Stakeholders
- **Vanguard Insurance Corp (Fictional)**: The underwriter.
- **Commercial Clients**: The policyholders filing claims.
- **Claims Adjusters**: Vanguard employees processing claims.

## 6. User Roles
- **POLICYHOLDER**: End-user. Can only view their own policies and claims. Can submit claims.
- **ADJUSTER**: Internal employee. Can view all policies and claims. Can create policies. Can approve, reject, or re-validate claims. Can orchestrate settlements.

## 7. Functional Requirements
- **FR1 (Auth)**: System must issue JWTs upon successful login.
- **FR2 (Policies)**: System must manage policy coverage, limits, and deductibles.
- **FR3 (Claims)**: System must accept claim submissions and automatically request policy validation.
- **FR4 (Settlements)**: System must calculate final payout based on approved claim amounts and policy deductibles.

## 8. Non-Functional Requirements
- **NFR1 (Scalability)**: Services must register with Eureka for dynamic scaling and load balancing.
- **NFR2 (Resilience)**: Inter-service communication must degrade gracefully using Resilience4j circuit breakers.
- **NFR3 (Security)**: Centralized JWT signature validation at the Gateway, with decentralized RBAC enforcement at the microservice level.

## 9. Business Rules
- A claim cannot be approved if it is not in the `VALIDATED` state.
- An approved amount cannot exceed the originally claimed amount.
- Payout amounts cannot fall below zero.

## 10. Claim Lifecycle
- `SUBMITTED`: Initial entry.
- `VALIDATED`: Policy limits/dates pass checks.
- `REJECTED`: Policy limits/dates fail, or adjuster manually rejects.
- `APPROVED`: Adjuster signs off on the amount.
- `SETTLED`: Settlement Service successfully calculates and finalizes payout.
- `SETTLEMENT_FAILED`: Settlement Service fails business logic or downstream error.

## 11. Settlement Rules
- `payoutAmount = max(0, approvedAmount - deductible)`
- A single claim can only have one settlement (duplicate prevention).
- Settlements transition from `PROCESSING` to `COMPLETED` or `FAILED`.

## 12. Security Requirements
- No plaintext passwords (BCrypt).
- Stateless JWT tokens (no sessions).
- Gateway rejects invalid signatures.
- Services independently extract and enforce `userId` ownership checks.

## 13. Service Discovery Requirements
- All services must dynamically bind to Eureka (`http://localhost:8761/eureka/`).
- Routing must use `lb://<SERVICE-NAME>`.

## 14. API Gateway Requirements
- Must act as a single point of entry (Port 8080).
- Must enforce global CORS policies.
- Must limit rate bursts on login endpoints (e.g., 5 req/min).
- Must inject `X-Correlation-ID`.

## 15. Database Requirements
- Runtime databases run on Neon PostgreSQL.
- Test databases use in-memory H2.
- Environment variables inject all credentials. No hardcoded DB passwords.

## 16. Error-Handling Requirements
- Stack traces must never escape the Gateway.
- Missing endpoints yield standardized 404 JSONs.
- Validation errors yield standardized 400 Bad Request JSONs.

## 17. Resilience Requirements
- Downstream failures during orchestration (e.g., Policy unavailable) must not corrupt the Claim. Claim remains `SUBMITTED` or `APPROVED` with a 503 response cleanly sent to the user.

## 18. Traceability
- Rubric 1 (Reqs) -> `requirements-specification.md`
- Rubric 2 (Microservices) -> `discovery-server`, `api-gateway`, Eureka
- Rubric 3 (JWT) -> `auth-service`, `JwtAuthenticationFilter`, `JwtTokenFilter`
- Rubric 4 (Gateway) -> `api-gateway`, `LoginRateLimiterFilter`
- Rubric 5 (LinkedIn/DTI) -> `dti-concepts.md`, `linkedin-article.md`
