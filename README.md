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
`roleCode` must be provided (e.g., `user` or `admin`). Newly created users start with `active` status.
```bash
curl -b cookie.txt -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"alice@123","roleCode":"user"}'
```

### List users
```bash
curl -b cookie.txt "http://localhost:8080/users?page=0&size=10"
```

### Change own password
Requires the user's current password.
```bash
curl -b cookie.txt -X POST http://localhost:8080/me/password \
  -H "Content-Type: application/json" \
  -d '{"oldPassword":"alice@123","newPassword":"newStrongPass"}'
```

### Admin reset another user's password
Admins can set a new password for any user without the old password.
```bash
curl -b cookie.txt -X POST http://localhost:8080/users/2/password \
  -H "Content-Type: application/json" \
  -d '{"newPassword":"newStrongPass"}'
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

### Query database views
```bash
curl -b cookie.txt -X POST http://localhost:8080/views \
  -H "Content-Type: application/json" \
  -d '{
        "tbl":"USERS_VIEW",
        "schema":"ums",
        "default_sorts":[{"field":"id","asc":true}],
        "page":1,
        "page_size":50,
        "filters":[{"field":"username","op":"LIKE","value":"%admin%"}]
      }'
```
Supported filter operators: `EQ`, `NE`, `GT`, `GTE`, `LT`, `LTE`, `LIKE`.
