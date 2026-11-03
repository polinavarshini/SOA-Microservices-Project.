# API Gateway Configuration

The `API-GATEWAY` acts as the fortified edge of the microservice cluster.

## 1. Eureka-Based Routing
All paths dynamically resolve via `spring-cloud-starter-netflix-eureka-client`.
- No IP addresses or hostnames are hardcoded.
- Routes map cleanly using the `lb://` protocol (e.g. `lb://CLAIM-SERVICE`).

## 2. Protected & Public Endpoints
- **Public**: `/api/auth/login`, `/api/auth/register`, `/actuator/**`
- **Protected**: `/api/policies/**`, `/api/claims/**`, `/api/settlements/**`

## 3. JWT Security
- **Validation**: The `JwtAuthenticationFilter` validates the HMAC-SHA256 signature locally at the edge.
- **Propagation**: Unmodified `Bearer` tokens are forwarded natively to backends.

## 4. CORS Configuration
Global CORS is applied securely.
- Allows standard REST verbs (`GET, POST, PUT, DELETE, OPTIONS`).
- Injects configurable Origins via `${FRONTEND_URL}`.
- Restricts credentials and explicitly whitelists `Authorization`, `Content-Type`, `X-Correlation-ID`.

## 5. Correlation IDs
The `CorrelationIdFilter` assigns a `UUID` to `X-Correlation-ID` if absent. This token flows entirely through the backend trace and is injected into the final Gateway `HttpResponse` headers, ensuring complete end-to-end debugging observability.

## 6. Rate Limiting
To protect the authentication bottleneck without provisioning Redis clusters, a lightweight `LoginRateLimiterFilter` deploys a ConcurrentHashMap-backed Token Bucket algorithm. It explicitly caps `POST /api/auth/login` to a burst maximum (e.g., 5 requests per minute), returning `429 Too Many Requests` on violation.

## 7. Timeout Handling
Global fallbacks are configured via standard HTTP parameters in `.env`:
- `GATEWAY_CONNECT_TIMEOUT`
- `GATEWAY_RESPONSE_TIMEOUT`
This prevents hanging thread exhaustion.

## 8. Sanitized Errors
A bespoke `GatewayExceptionHandler` masks upstream Spring traces, database crashes, and 500 errors. It parses failures into a clean JSON structure:
`{"error": "Downstream Service Unavailable", "status": 503, "correlationId": "..."}`

## 9. Security Headers
`SecurityHeadersFilter` attaches `X-XSS-Protection`, `Strict-Transport-Security`, `X-Frame-Options`, and `X-Content-Type-Options` to all egress traffic.

## Conclusion
The Gateway is deeply optimized for its edge role. By delegating complex RBAC to the microservices and limiting its focus to edge-threats (Rate Limiting, Global Authentication, Topology Hiding, CORS, Timeouts), it remains highly performant and secure.
