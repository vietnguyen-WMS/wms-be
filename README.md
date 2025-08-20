# UMS Backend

Spring Boot User Management service with cookie-based JWT auth and PostgreSQL.

## Quick Start

Ensure PostgreSQL is running (Docker compose already includes it) and the database has the required schema and seed data.

```bash
mvn clean package
mvn spring-boot:run
```

## cURL Examples

### Login (store cookie)
```bash
curl -i -c cookie.txt -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin@123"}'
```

### Me (reads cookie)
```bash
curl -b cookie.txt http://localhost:8080/auth/me
```

### Create user
```bash
curl -b cookie.txt -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"alice@123","statusCode":"active"}'
```

### List users
```bash
curl -b cookie.txt "http://localhost:8080/users?page=0&size=10"
```

### Update user
```bash
curl -b cookie.txt -X PATCH http://localhost:8080/users/2 \
  -H "Content-Type: application/json" \
  -d '{"password":"newStrongPass"}'
```

### Soft delete
```bash
curl -b cookie.txt -X DELETE http://localhost:8080/users/2
```

### Logout (clears cookie)
```bash
curl -b cookie.txt -X POST http://localhost:8080/auth/logout
```

### Unlock locked user (admin only)
Resets a locked user's `failed_login_attempts` to zero and sets status back to `active`. The caller must be an admin.

```bash
curl -b cookie.txt -X POST http://localhost:8080/users/<userId>/reset-failed-attempts
```
