# JWT Authentication & Security Guide

This document outlines the zero-trust JWT implementation across the Vanguard Architecture.

## 1. Registration & Public Signup
Clients POST to `/api/auth/register` (Auth Service) bypassing the Gateway's JWT filter.
- **Role Enforcement:** All public registrations are unconditionally assigned the `POLICYHOLDER` role. Self-registration as `ADJUSTER` is strictly prohibited and rejected/ignored by the backend.
- **Password Security:** Passwords are cryptographically hashed using Spring Security's `BCryptPasswordEncoder` before database persistence. Plain-text passwords are never stored.
- **Email Generation:** If an email is omitted in the signup request, a default email (`<username>@vanguard.com`) is derived to satisfy database integrity constraints.
- **Status Codes:** Returns `201 Created` upon successful account creation, `409 Conflict` if the username is already registered, and `400 Bad Request` for validation failures.

## 2. Login & Identity
Clients POST to `/api/auth/login` (Auth Service) bypassing the Gateway's JWT filter. The Auth Service queries the DB, validates the BCrypt hash, and mints a token.

## 3. Token Generation & Signature
Tokens are generated using `jjwt`. They are cryptographically signed using `HMAC-SHA256` (HS256) against a strong, shared 256-bit `JWT_SECRET` loaded dynamically from the `.env` file.

## 4. Token Claims
The payload contains:
- `sub`: The username.
- `userId`: The immutable DB UUID of the user.
- `role`: The authorized role (`POLICYHOLDER` or `ADJUSTER`).
- `iat` / `exp`: Issue and Expiration timestamps.

## 5. Expiration
Tokens are strictly limited to an expiration window (e.g., 24 hours). Expired tokens are programmatically rejected by `jjwt` parsers.

## 6. Gateway Validation
The API Gateway contains a `JwtAuthenticationFilter`. For any protected route, it inspects `Authorization: Bearer <token>`.
- Parses the signature against `JWT_SECRET`.
- Immediately rejects `401 Unauthorized` if malformed, tampered, or expired.
- Does NOT process role boundaries (stateless boundary defense).

## 7. JWT Propagation
Once validated, the Gateway passes the original `Authorization` header exactly as-is to the downstream microservices. 

Similarly, when `CLAIM-SERVICE` calls `POLICY-SERVICE`, a `FeignClientInterceptor` captures the active HTTP Request's `Authorization` header and injects it into the outbound Feign request.

## 8. Downstream Independent Validation
Backend services (Policy, Claim, Settlement) **do not blindly trust the Gateway**. Internal boundaries are secured by a local `JwtTokenFilter` in every service. Even if an attacker circumvented the Gateway to hit a service directly, the local filter would intercept, validate the cryptographical signature, and build a localized `SecurityContext`.

## 9. Role-Based Authorization
Services enforce RBAC using method-level security (`@PreAuthorize("hasRole('ADJUSTER')")`) and localized controller logic.

## 10. Ownership Enforcement
Crucially, endpoints do not trust JSON payloads for identity. When submitting a claim, the `CLAIM-SERVICE` reads the `userId` directly from the authenticated `SecurityContext` (extracted from the JWT) to assign the `claimantUserId`. A Policyholder cannot view claims belonging to another `userId`.

## 11. Invalid/Expired Token Handling
- Missing/invalid/expired tokens yield HTTP 401.
- Valid tokens lacking required roles yield HTTP 403 Forbidden.
- Rejection occurs at the earliest possible boundary (Gateway).
