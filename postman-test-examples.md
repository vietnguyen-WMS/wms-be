# Postman Test Examples for UMS Login API

## Base URL

```
http://localhost:8080
```

## 1. Health Check

**GET** `/auth/health`

**Expected Response:**

```
Auth service is running
```

## 2. Login Tests

### Test 1: Valid Login (if you know the password)

**POST** `/auth/login`

**Headers:**

```
Content-Type: application/json
```

**Body (raw JSON):**

```json
{
  "username": "admin",
  "password": "your_actual_password"
}
```

**Expected Success Response:**

```json
{
  "success": true,
  "message": "Login successful",
  "token": "token_abc123def456...",
  "userInfo": {
    "id": 1,
    "username": "admin",
    "createdAt": "2025-07-30T23:44:39.766+07:00"
  },
  "timestamp": "2025-07-31T00:12:00.000"
}
```

### Test 2: Invalid Password

**POST** `/auth/login`

**Headers:**

```
Content-Type: application/json
```

**Body (raw JSON):**

```json
{
  "username": "admin",
  "password": "wrongpassword"
}
```

**Expected Error Response:**

```json
{
  "success": false,
  "message": "Invalid username or password",
  "token": null,
  "userInfo": null,
  "timestamp": "2025-07-31T00:12:00.000"
}
```

### Test 3: Invalid Username

**POST** `/auth/login`

**Headers:**

```
Content-Type: application/json
```

**Body (raw JSON):**

```json
{
  "username": "nonexistent",
  "password": "anypassword"
}
```

**Expected Error Response:**

```json
{
  "success": false,
  "message": "Invalid username or password",
  "token": null,
  "userInfo": null,
  "timestamp": "2025-07-31T00:12:00.000"
}
```

### Test 4: Missing Username (Validation Error)

**POST** `/auth/login`

**Headers:**

```
Content-Type: application/json
```

**Body (raw JSON):**

```json
{
  "password": "anypassword"
}
```

**Expected Validation Error Response:**

```json
{
  "success": false,
  "message": "Validation failed",
  "errors": {
    "username": "Username is required"
  },
  "timestamp": "2025-07-31T00:12:00.000"
}
```

### Test 5: Missing Password (Validation Error)

**POST** `/auth/login`

**Headers:**

```
Content-Type: application/json
```

**Body (raw JSON):**

```json
{
  "username": "admin"
}
```

**Expected Validation Error Response:**

```json
{
  "success": false,
  "message": "Validation failed",
  "errors": {
    "password": "Password is required"
  },
  "timestamp": "2025-07-31T00:12:00.000"
}
```

## Postman Collection Setup

1. Create a new collection called "UMS API"
2. Add the base URL: `http://localhost:8080`
3. Create requests for each test above
4. Set the Content-Type header to `application/json` for POST requests
5. Use the raw JSON body for POST requests

## Notes

- The application uses BCrypt password hashing
- Your existing user has the hash: `$2a$10$eBv9DdHqXPkU5U/5zW6B8.7kW5Z7nZdLxqF7qWyYOXtT0JhLdTuQu`
- You need to know the original plain text password to test successful login
- The API returns proper HTTP status codes (200 for success, 401 for auth failure, 400 for validation errors)
