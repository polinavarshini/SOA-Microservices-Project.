# Design Thinking Innovation (DTI) Concepts

This document maps the development of the "Automated Commercial Insurance Claim Verification & Settlement Engine" to the 5 stages of the Design Thinking methodology.

## 1. EMPATHIZE
**What this stage means**: Understanding the frustrations, needs, and operational bottlenecks of the primary stakeholders (Policyholders and Claims Adjusters).
**What was identified**: 
- Policyholders feel "in the dark" about claim statuses and wait weeks for payout checks.
- Adjusters suffer from "swivel-chair" workflows, manually looking up policy deductibles and limits in legacy databases to validate claims.
**What evidence exists**:
- *To be completed with actual survey/review evidence.* (See `survey.md`)

## 2. DEFINE
**What this stage means**: Framing the core problem based on the empathize phase into actionable problem statements.
**What was identified**: The monolithic architecture of traditional claim processing prevents rapid scaling and isolates data (Identity vs Policies vs Claims).
**Design Decision**: We defined the core requirement as a system needing absolute real-time verification and mathematical payout automation.
**Implemented**: We defined strict microservice boundaries: Auth, Policy, Claim, and Settlement.

## 3. IDEATE
**What this stage means**: Brainstorming technical and architectural solutions to address the defined problem without constraints.
**What was identified**: How do we safely pass user identity across 4 independent systems? How do we prevent total system failure if the Policy database goes offline?
**Design Decision**: 
- Adopt decentralized JWT for identity.
- Adopt Eureka for dynamic discovery.
- Adopt Resilience4j Circuit Breakers to fail gracefully.
**Implemented**: Zero-trust JWT propagation via OpenFeign Interceptors.

## 4. PROTOTYPE
**What this stage means**: Building the actual, functional representation of the ideas.
**What was identified**: We needed a working, testable codebase representing the commercial claim flow.
**Design Decision**: Java 17, Spring Boot 3.3.4, and Neon PostgreSQL chosen for modern, robust backend implementation.
**Implemented**: The actual codebase residing in `D:\SOA\back`. We successfully mocked the API Gateway edge, service inter-communication, and automated database schema generation. 

## 5. TEST / REVIEW
**What this stage means**: Validating the prototype against the defined problems and gathering user feedback.
**What was identified**: We required rigorous testing to ensure cross-service failures didn't corrupt financial ledgers.
**Implemented**: H2 database automated integration tests, Gateway `WebTestClient` WireMock tests, and Resilience4j fallback verifications.
**What evidence exists**:
- *To be completed with actual survey/review evidence following prototype demonstration.*
