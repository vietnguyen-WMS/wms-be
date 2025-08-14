# User Management System (UMS) - Authentication API

A Spring Boot REST API for user authentication with PostgreSQL database integration.

## Features

- User authentication with login endpoint
- PostgreSQL database integration
- Input validation
- Comprehensive error handling
- Logging
- Test data initialization

## Prerequisites

- Java 21
- Maven 3.6+
- PostgreSQL 12+

## Database Setup

1. Create PostgreSQL database and user:

```sql
CREATE DATABASE wms;
CREATE USER ums WITH PASSWORD 'ums';
GRANT ALL PRIVILEGES ON DATABASE wms TO ums;
```

2. Create the ums schema and grant permissions:

```sql
\c wms;
CREATE SCHEMA IF NOT EXISTS ums;
GRANT ALL PRIVILEGES ON SCHEMA ums TO ums;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA ums TO ums;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA ums TO ums;
```

3. The application will automatically create the `ums.users` table with the following structure:
   - `id` (BIGSERIAL, PRIMARY KEY)
   - `username` (VARCHAR(50), UNIQUE, NOT NULL)
   - `password` (VARCHAR(255), NOT NULL)
   - `created_at` (TIMESTAMP, NOT NULL)
   - `updated_at` (TIMESTAMP, NOT NULL)

## Running the Application

1. Clone the repository
2. Navigate to the project directory
3. Run the application:

```bash
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`

## API Endpoints

### POST /auth/login

Authenticate a user with username and password.

**Request Body:**

```json
{
  "username": "admin",
  "password": "password123"
}
```

**Success Response (200 OK):**

```json
{
  "success": true,
  "message": "Login successful",
  "token": "token_abc123def456...",
  "userInfo": {
    "id": 1,
    "username": "admin",
    "createdAt": "2024-01-01T10:00:00"
  },
  "timestamp": "2024-01-01T10:00:00"
}
```

**Error Response (401 Unauthorized):**

```json
{
  "success": false,
  "message": "Invalid username or password",
  "timestamp": "2024-01-01T10:00:00"
}
```

**Validation Error Response (400 Bad Request):**

```json
{
  "success": false,
  "message": "Validation failed",
  "errors": {
    "username": "Username is required",
    "password": "Password is required"
  },
  "timestamp": "2024-01-01T10:00:00"
}
```

### GET /auth/health

Health check endpoint.

**Response:**

```
Auth service is running
```

### Users CRUD

Base path: `/api/v1/users`

```bash
# List users
curl 'http://localhost:8080/api/v1/users?page=0&size=20'

# Get user by id
curl 'http://localhost:8080/api/v1/users/1'

# Create user
curl -X POST 'http://localhost:8080/api/v1/users' \
  -H 'Content-Type: application/json' \
  -d '{"username":"john","password":"secret"}'

# Update user
curl -X PUT 'http://localhost:8080/api/v1/users/1' \
  -H 'Content-Type: application/json' \
  -d '{"username":"johnny"}'

# Soft delete user
curl -X DELETE 'http://localhost:8080/api/v1/users/1'
```

## Test Users

The application automatically creates two test users on startup:

1. **Username:** `admin` | **Password:** `password123`
2. **Username:** `user` | **Password:** `user123`

## Testing the API

### Using curl:

```bash
# Successful login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "password123"}'

# Failed login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "wrongpassword"}'

# Health check
curl http://localhost:8080/auth/health
```

### Using Postman:

1. Create a new POST request to `http://localhost:8080/auth/login`
2. Set Content-Type header to `application/json`
3. Add request body:

```json
{
  "username": "admin",
  "password": "password123"
}
```

## Project Structure

```
src/main/java/com/example/ums/users/
├── controller/
│   └── AuthController.java          # REST controller
├── dto/
│   ├── LoginRequest.java            # Request DTO
│   └── LoginResponse.java           # Response DTO
├── entity/
│   └── User.java                    # JPA entity
├── repository/
│   └── UserRepository.java          # Data access layer
├── service/
│   ├── UserService.java             # Service interface
│   └── impl/
│       └── UserServiceImpl.java     # Service implementation
├── exception/
│   └── GlobalExceptionHandler.java  # Global exception handler
└── config/
    └── DataInitializer.java         # Data initialization
```

## Configuration

The application configuration is in `src/main/resources/application.properties`:

- Database connection settings (wms database, ums schema)
- JPA/Hibernate configuration
- Server port (8080)
- Logging configuration

## Security Notes

⚠️ **Important Security Considerations:**

1. **Password Storage**: This is a demo application. In production, always hash passwords using BCrypt or similar.
2. **Token Generation**: The current implementation uses simple UUID tokens. In production, use JWT tokens.
3. **Database Security**: Use strong passwords and proper database security measures.
4. **HTTPS**: Always use HTTPS in production environments.

## Future Enhancements

- JWT token implementation
- Password hashing with BCrypt
- User registration endpoint
- Password reset functionality
- Role-based access control
- API rate limiting
- Swagger/OpenAPI documentation

## Troubleshooting

1. **Database Connection Error**: Ensure PostgreSQL is running and the database/user credentials are correct.
2. **Schema Issues**: Make sure the `ums` schema exists in the `wms` database.
3. **Port Already in Use**: Change the server port in `application.properties`.
4. **Validation Errors**: Check the request body format and required fields.

## License

This project is for educational purposes.
