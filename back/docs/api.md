# API Documentation

## Auth Service

### POST `/api/auth/register`
Registers a new user (always created as `POLICYHOLDER`). Public self-registration of `ADJUSTER` accounts is strictly forbidden.

**Request:**
```json
{
  "username": "newuser",
  "password": "securepassword123"
}
```
*Note: `email` is optional (defaults to `<username>@vanguard.com`). Any `role` parameter sent by the client is ignored; accounts are always created with `POLICYHOLDER` role.*

**Response (201 Created):**
```json
{
  "message": "User registered successfully."
}
```

**Errors:**
- `400 Bad Request`: Validation failure (empty username/password, password < 6 chars, username < 4 chars).
- `409 Conflict`: Username or email is already registered.

---

### POST `/api/auth/login`
Authenticates a user and returns a JWT.

**Request:**
```json
{
  "username": "johndoe",
  "password": "securepassword123"
}
```

**Response (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 1800,
  "role": "POLICYHOLDER"
}
```

**Errors:**
- `401 Unauthorized`: Invalid credentials.
