CREATE TABLE IF NOT EXISTS library (
    id      UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    name    VARCHAR(100) NOT NULL,
    phone   VARCHAR(30)  NOT NULL UNIQUE,
    email   VARCHAR(100) NOT NULL UNIQUE,
    address VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS genre (
    id         UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    name       VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS author (
    id         UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    first_name VARCHAR(100) NOT NULL,
    last_name  VARCHAR(100) NOT NULL,
    CONSTRAINT uq_author_name UNIQUE (first_name, last_name)
);

CREATE TABLE IF NOT EXISTS book (
    id          UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    title       VARCHAR(100) NOT NULL,
    summary     TEXT,
    isbn        VARCHAR(100) NOT NULL UNIQUE,
    publisher   VARCHAR(100) NOT NULL,
    published_at DATE        NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS author_book (
    book_id    UUID NOT NULL REFERENCES book (id) ON DELETE CASCADE,
    author_id  UUID NOT NULL REFERENCES author (id) ON DELETE CASCADE,
    PRIMARY KEY (book_id, author_id)
);

CREATE TABLE IF NOT EXISTS book_genre (
    book_id    UUID NOT NULL REFERENCES book (id) ON DELETE CASCADE,
    genre_id   UUID NOT NULL REFERENCES genre (id) ON DELETE CASCADE,
    PRIMARY KEY (book_id, genre_id)
);

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'user_role') THEN
    CREATE TYPE user_role AS ENUM ('ADMIN', 'CUSTOMER');
  END IF;
END
$$;

CREATE TABLE IF NOT EXISTS users (
    id          UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    last_name   VARCHAR(100) NOT NULL,
    first_name  VARCHAR(100),
    birth_date  DATE         NOT NULL,
    email       VARCHAR(100) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    phone       VARCHAR(30),
    role        user_role    NOT NULL DEFAULT 'CUSTOMER',
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'sale_status') THEN
    CREATE TYPE sale_status AS ENUM ('PENDING', 'SOLD', 'BOOKED', 'EXPIRED');
  END IF;
END
$$;

CREATE TABLE IF NOT EXISTS sale (
    id               UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    sale_date        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id          UUID         REFERENCES users(id) ON DELETE SET NULL,
    status           sale_status  NOT NULL DEFAULT 'BOOKED',
    library_id       UUID         NOT NULL REFERENCES library(id),
    expiration_date  TIMESTAMP,
    created_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS sale_book_copy (
    id              UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    book_copy_id    UUID         NOT NULL REFERENCES book_copy(id),
    sale_id         UUID         NOT NULL REFERENCES sale(id),
    quantity        INT          NOT NULL DEFAULT 1,
    price           NUMERIC(10,2) NOT NULL,
    created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_sale_book_copy UNIQUE (book_copy_id, sale_id)
);

