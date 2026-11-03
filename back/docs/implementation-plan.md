# Implementation Plan: Vanguard Mutual Insurance Automated Claim Verification & Settlement Engine

## 1. Current Workspace Structure
- `D:\SOA\front`: Empty directory
- `D:\SOA\back`: Empty directory

## 2. Existing Components
- None. The workspace is currently empty.

## 3. Missing Components
- API Gateway
- Service Registry (Eureka)
- Auth Service
- Policy Service
- Claim Service
- Settlement Service
- Frontend Application

## 4. Required Architecture
- **Client**: Frontend Application
- **API Gateway**: Entry point for all client requests, handles routing.
- **Service Discovery**: Eureka server for microservice registration and discovery.
- **Microservices**:
  - Auth Service: JWT based authentication and authorization.
  - Policy Service: Manages policies and validates coverages.
  - Claim Service: Handles claim submissions and initial processing.
  - Settlement Service: Executes financial payouts after approval.

## 5. Proposed Technology Stack
- **Backend Framework**: Spring Boot (Java)
- **Service Discovery**: Spring Cloud Netflix Eureka
- **API Gateway**: Spring Cloud Gateway
- **Authentication**: Spring Security with JWT
- **Database**: H2 (In-memory, embedded) for simplicity in a student project.
- **Inter-service Communication**: Spring Cloud OpenFeign
- **Load Balancing**: Spring Cloud LoadBalancer (integrated with Eureka and Gateway)
- **Frontend**: React (Vite)

## 6. Service Responsibilities
- **Auth Service**: User registration, login, JWT token generation and validation. Roles: POLICYHOLDER, ADJUSTER.
- **Policy Service**: CRUD for insurance policies. Validates if a claim's incident is covered under the policy terms.
- **Claim Service**: Receives claims from policyholders. Orchestrates validation by calling Policy Service. Allows adjusters to review and approve/reject claims.
- **Settlement Service**: Triggered by Claim Service upon approval. Calculates final payout and records the settlement transaction.

## 7. Database Strategy
- Each microservice (except Gateway and Eureka) will have its own dedicated database (Database-per-service pattern).
- To avoid complex local infrastructure, H2 in-memory databases will be used for each service, with Spring Data JPA for ORM.

## 8. Authentication Strategy
- **JWT (JSON Web Tokens)**: Auth Service issues JWTs upon successful login.
- **API Gateway**: Validates the JWT signature or forwards it downstream.
- **Downstream Services**: Validate JWTs and perform Role-Based Access Control (RBAC).

## 9. API Gateway Strategy
- Spring Cloud Gateway to route requests based on path predicates (e.g., `/api/claims/**` to Claim Service).
- Cross-Origin Resource Sharing (CORS) configuration.

## 10. Eureka Strategy
- Standalone Spring Boot application with `@EnableEurekaServer`.
- All other microservices will be Eureka clients (`@EnableDiscoveryClient`) and register themselves.

## 11. Load Balancing Strategy
- Client-side load balancing via Spring Cloud LoadBalancer, automatically integrated with Eureka clients and the API Gateway.

## 12. Inter-service Communication Strategy
- Synchronous communication using Spring Cloud OpenFeign for REST calls between services (e.g., Claim Service calling Policy Service).

## 13. Testing Strategy
- **Unit Testing**: JUnit 5 and Mockito for business logic.
- **Integration Testing**: Spring Boot Test with MockMvc for REST endpoints.

## 14. Deployment Strategy
- Local deployment using standard Maven/Gradle builds.
- Configured to run on distinct ports (e.g., Eureka: 8761, Gateway: 8080, Auth: 8081, Policy: 8082, Claim: 8083, Settlement: 8084).

## 15. Documentation Requirements
- README.md at the project root.
- Postman Collection for API endpoints.
- LinkedIn Article detailing the DTI (Design, Technology, Implementation) concepts and review, as required by the rubric.

## 16. Risks or Possible Implementation Issues
- **Port Conflicts**: Ensuring all services start on their designated ports locally.
- **Startup Order**: Services must start in the correct order (Eureka first, then others, Gateway last).
- **Data Consistency**: In an H2 in-memory setup, data is lost on restart. Seed data (`data.sql`) will be needed for testing.

## 17. Step-by-step Implementation Phases
- **Phase 1: Infrastructure Setup**: Create Eureka Server and API Gateway.
- **Phase 2: Authentication & Foundation**: Create Auth Service and implement JWT generation/validation.
- **Phase 3: Core Domain Services**: Create Policy Service and Settlement Service.
- **Phase 4: Orchestration**: Create Claim Service and implement OpenFeign clients.
- **Phase 5: Security Integration**: Secure all endpoints using JWT validation across microservices.
- **Phase 6: Frontend Development**: Scaffold React app in `front/` and integrate with API Gateway.
- **Phase 7: Testing & Documentation**: Write unit tests, finalize README, and draft the LinkedIn article.
