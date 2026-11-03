# Integration Testing Guide

## Prerequisites
- **Java 17** and **Maven** installed on your host machine.
- Neon PostgreSQL credentials configured in `.env` files for `auth-service`, `policy-service`, `claim-service`, and `settlement-service`. (Note: Never commit these).

## Startup Order
To bring up the entire Vanguard architecture locally, open separate terminal windows and start the services in the following order:

1. **Discovery Server** (`cd discovery-server && mvn spring-boot:run`)
2. **Auth Service** (`cd auth-service && mvn spring-boot:run`)
3. **Policy Service** (`cd policy-service && mvn spring-boot:run`)
4. **Claim Service** (`cd claim-service && mvn spring-boot:run`)
5. **Settlement Service** (`cd settlement-service && mvn spring-boot:run`)
6. **API Gateway** (`cd api-gateway && mvn spring-boot:run`)

*Wait for all services to successfully register in Eureka (visit http://localhost:8761).*

## End-to-End Test Sequence (Happy Path)

### A. Login as Policyholder
POST to `/api/auth/login` with Policyholder credentials. Extract the `token`.

### B. View/Create Policy
As Policyholder, GET `/api/policies` to find your active policy numbers. 
*(If none exist, login as Adjuster and POST to `/api/policies` to create one for your user).*

### C. Submit Claim
As Policyholder, POST to `/api/claims` with your Policy Number and a claim amount.

### D. Verify Policy Service Validation
Check the response. The `CLAIM-SERVICE` will have automatically Feign-called the `POLICY-SERVICE`. If within limits, the returned Claim status should be `VALIDATED`.

### E. Login as Adjuster
POST to `/api/auth/login` with Adjuster credentials. Extract the new `token`.

### F. Approve Claim
As Adjuster, PUT to `/api/claims/{claimNumber}/approve`. Provide an `approvedAmount` (less than or equal to claimed amount).

### G. Verify Settlement
Behind the scenes, Claim Service retrieves the deductible from Policy Service and triggers Settlement Service via LoadBalancer. Check the response headers for `X-Served-By` to prove load balancing.

### H. Verify Claim Becomes SETTLED
The claim response should now have `status: "SETTLED"`. 

## Negative Test Flows
- **Invalid JWT**: Send a modified token to any protected Gateway route. Expect `401 Unauthorized`.
- **Wrong Role**: As Policyholder, attempt to PUT `/api/claims/{claimNumber}/approve`. Expect `403 Forbidden`.
- **Insufficient Coverage**: Submit a claim amount exceeding the policy limits. Expect Claim status `REJECTED`.
- **Downstream Service Unavailable**: Stop the Policy Service and try submitting a claim. The Circuit Breaker will intercept it. Expect Claim status `SUBMITTED` and a clean `503` JSON error instead of a server crash.
