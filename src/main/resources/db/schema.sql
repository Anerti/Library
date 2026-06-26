CREATE TABLE IF NOT EXISTS library (
    id      UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    name    VARCHAR(100) NOT NULL,
    phone   VARCHAR(30)  NOT NULL UNIQUE,
    email   VARCHAR(100) NOT NULL UNIQUE,
    address VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS genre (
    id         UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    name       VARCHAR(100) NOT NULL UNIQUE,
);

CREATE TABLE IF NOT EXISTS author (
    id         UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    first_name VARCHAR(100) NOT NULL,
    last_name  VARCHAR(100) NOT NULL,
    CONSTRAINT uq_author_name UNIQUE (first_name, last_name)
);

