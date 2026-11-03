# Rubric Coverage Matrix

| Rubric | Implemented Evidence | Documentation Evidence | Remaining Submission Evidence |
|--------|----------------------|------------------------|-------------------------------|
| **1. Problem Analysis and Requirement Specification** | N/A (Theoretical) | `docs/requirements-specification.md` explicitly details problem, solution, users, functional limits, and claim lifecycles. | None |
| **2. Microservice Identification and Service Discovery** | 6 isolated Spring Boot applications. `discovery-server` on 8761. Domain databases separated. Feign clients utilized. | `docs/architecture.md` explicitly details the service boundaries, Eureka, and LoadBalancing. | None |
| **3. JWT Authentication** | `auth-service` generates JWT. `JwtTokenFilter` in Policy/Claim/Settlement enforces local RBAC. `FeignClientInterceptor` propagates tokens. | `docs/authentication.md` explains signature, expiration, and independent validation. | None |
| **4. API Gateway Configuration** | `api-gateway` on 8080. `JwtAuthenticationFilter`, `LoginRateLimiterFilter`, `GatewayExceptionHandler`, `CorrelationIdFilter`. CORS configured. | `docs/gateway.md` explains edge optimizations, sanitized errors, timeouts, and route masking. | None |
| **5. LinkedIn Article & DTI Concepts** | N/A (Theoretical) | `docs/dti-concepts.md`, `docs/survey.md`, and `docs/linkedin-article.md` drafted honestly without fabricated data. | Collect real survey data. Publish article to LinkedIn. Insert URLs/Data into placeholders. |
