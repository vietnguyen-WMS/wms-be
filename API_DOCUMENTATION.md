# UMS API - Token-Based Authentication

## New Endpoints

### 1. POST /auth/login

Authenticate user and set token as HTTP-only cookie.

**Request:**

```json
{
  "username": "admin",
  "password": "password123"
}
```

**Response:**

```json
{
  "success": true,
  "message": "Login successful",
  "token": "token_abc123...",
  "userInfo": {...}
}
```

### 2. GET /auth/me

Get current user from token in cookie.

**Response:**

```json
{
  "id": 1,
  "username": "admin",
  "createdAt": "2024-01-01T10:00:00",
  "updatedAt": "2024-01-01T10:00:00"
}
```

### 3. POST /auth/logout

Clear authentication cookie.

**Response:**

```json
{
  "success": true,
  "message": "Logout successful"
}
```

## Frontend Usage

```javascript
// Login
const response = await fetch("http://localhost:8080/auth/login", {
  method: "POST",
  credentials: "include",
  headers: { "Content-Type": "application/json" },
  body: JSON.stringify({ username, password }),
});

// Check auth
const user = await fetch("http://localhost:8080/auth/me", {
  credentials: "include",
});

// Logout
await fetch("http://localhost:8080/auth/logout", {
  method: "POST",
  credentials: "include",
});
```

## Features

- Token stored in database with 24-hour expiration
- HTTP-only cookies for security
- Automatic token cleanup
- CORS configured for credentials
