CREATE TABLE IF NOT EXISTS genre (
    id         UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    name       VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP   DEFAULT now(),
    updated_at TIMESTAMP   DEFAULT now()
);
