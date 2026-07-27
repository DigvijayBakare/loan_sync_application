-- V1__init_user_schema.sql

CREATE TABLE roles (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);

CREATE TABLE users (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email           VARCHAR(150) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    first_name      VARCHAR(255),
    last_name       VARCHAR(255),
    phone_number    VARCHAR(255),
    is_enabled      BOOLEAN NOT NULL,
    is_verified     BOOLEAN NOT NULL,
    created_at      TIMESTAMP NOT NULL,
    updated_at      TIMESTAMP NOT NULL
);

CREATE TABLE user_roles (
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

CREATE INDEX idx_users_email ON users(email);

INSERT INTO roles (name, description) VALUES
    ('SUPER_ADMIN', 'Full unrestricted system access'),
    ('ADMIN', 'Administrative access'),
    ('LENDER', 'Manages loan products and approvals'),
    ('BORROWER', 'Applies for and manages own loans');