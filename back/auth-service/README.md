# Auth Service

The Auth Service handles user authentication (JWT) and manages roles for Vanguard Mutual Insurance.
It connects to a Neon PostgreSQL database.

## Prerequisites
- Java 17
- Maven
- A running Neon PostgreSQL database

## Setup and Running (Local Development)

Spring Boot relies on standard environment variables to resolve database credentials. We do not use third-party `.env` loaders to keep the project dependencies clean and standard.

1. **Configure your local credentials**
   Open `.env` (it is ignored by Git) and fill in your real Neon credentials.
   *Ensure the `DATABASE_URL` ends with `?sslmode=require`.*

2. **Load variables and start the service**
   Because Spring Boot does not automatically read `.env` files natively, you must inject these variables into your terminal session before starting Maven.

   **On Windows (PowerShell):**
   ```powershell
   cd D:\SOA\back\auth-service
   
   # Load the .env file into the current PowerShell session
   Get-Content .env | ForEach-Object {
       if ($_ -match '^(.*?)=(.*)$') {
           [System.Environment]::SetEnvironmentVariable($matches[1], $matches[2])
       }
   }
   
   # Start the service
   mvn spring-boot:run
   ```
   
   **On Linux/Mac (Bash):**
   ```bash
   cd D:\SOA\back\auth-service
   export $(grep -v '^#' .env | xargs)
   mvn spring-boot:run
   ```

   **Using an IDE (IntelliJ / Eclipse):**
   Simply install a `.env` plugin or paste the contents of `.env` directly into the "Environment Variables" section of your Run Configuration for `AuthServiceApplication`.

## First Startup
On the first successful startup:
1. Hibernate will automatically create the `users` table because `spring.jpa.hibernate.ddl-auto=update` is configured.
2. The `DataInitializer` will automatically seed the `demo_adjuster` and `demo_policyholder` users into the database.

## Tests
To run tests (which use an isolated in-memory H2 database and do NOT require Neon credentials):
```bash
mvn clean test
```
