CREATE TABLE IF NOT EXISTS library (
    id      UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    name    VARCHAR(100) NOT NULL,
    phone   VARCHAR(30)  NOT NULL UNIQUE,
    email   VARCHAR(100) NOT NULL UNIQUE,
    address VARCHAR(100) NOT NULL UNIQUE
);
