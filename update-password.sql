-- Update the admin user's password to work with password "1"
-- This BCrypt hash was generated for the password "1"

UPDATE ums.users 
SET password = '$2a$10$xh31vvTWXXFBJl88g5ltsO2d9QldcL4XW/6LK6rxXPBpDtGVSYaCe'
WHERE username = 'admin';

-- Verify the update
SELECT id, username, password, created_at, updated_at 
FROM ums.users 
WHERE username = 'admin'; 