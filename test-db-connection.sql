-- Test database connection and create schema
-- Run this as the postgres user or with appropriate permissions

-- Connect to the wms database
\c wms;

-- Create the ums schema if it doesn't exist
CREATE SCHEMA IF NOT EXISTS ums;

-- Create the users table manually (optional, as Hibernate will do this)
CREATE TABLE IF NOT EXISTS ums.users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Grant permissions to the ums user
GRANT ALL PRIVILEGES ON SCHEMA ums TO ums;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA ums TO ums;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA ums TO ums;

-- Test the connection
SELECT 'Database connection successful' as status; 