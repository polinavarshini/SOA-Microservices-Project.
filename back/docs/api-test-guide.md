# API Test Guide (Postman / cURL)

This guide provides example requests for testing the Vanguard API Gateway on `localhost:8080`. 

**Note**: Replace `<JWT_TOKEN>`, `<USER_ID>`, `<POLICY_NUMBER>`, and `<CLAIM_NUMBER>` with actual values received during testing.

---

### 1. Login (Public)
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "jdoe", "password": "password123"}'
```
*Extract the token from the response for subsequent requests.*

### 2. Create Policy (Requires ADJUSTER)
```bash
curl -X POST http://localhost:8080/api/policies \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "holderUserId": "<USER_ID>",
    "coverageLimit": 50000,
    "deductible": 1000,
    "effectiveDate": "2024-01-01",
    "expirationDate": "2025-01-01"
  }'
```

### 3. Get Policies
```bash
curl -X GET http://localhost:8080/api/policies \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

### 4. Submit Claim (Requires POLICYHOLDER)
```bash
curl -X POST http://localhost:8080/api/claims \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "policyNumber": "<POLICY_NUMBER>",
    "claimedAmount": 4000,
    "incidentDate": "2024-05-15",
    "description": "Fender bender"
  }'
```
*If successful, returns claim in `VALIDATED` status.*

### 5. Get Claims
```bash
curl -X GET http://localhost:8080/api/claims \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

### 6. Approve Claim (Requires ADJUSTER)
```bash
curl -X PUT http://localhost:8080/api/claims/<CLAIM_NUMBER>/approve \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"approvedAmount": 3500}'
```
*If successful, orchestrates settlement and returns `SETTLED`.*

### 7. Retrieve Settlement
```bash
curl -X GET http://localhost:8080/api/settlements/claim/<CLAIM_NUMBER> \
  -H "Authorization: Bearer <JWT_TOKEN>"
```
*Expect payoutAmount to reflect (approvedAmount - deductible).*
