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

