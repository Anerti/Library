-- Seed data for GET /books endpoint
-- Tests: title search, publisher filter, isbn filter, author lastName filter, genre filter
--
-- Generated with uuidgen. Use fixed UUIDs so tests can reference them by ID.
--
-- Books → Authors:
--   Le Petit Prince  → Antoine de Saint-Exupéry
--   Les Misérables   → Victor Hugo
--   L'Étranger       → Albert Camus
--   1984             → George Orwell
--   L'Assommoir      → Émile Zola
--
-- Books → Genres:
--   Le Petit Prince  → Fiction
--   Les Misérables   → Fiction
--   L'Étranger       → Philosophy
--   1984             → Fiction, Dystopian
--   L'Assommoir      → Fiction, Naturalism

INSERT INTO author (id, first_name, last_name)
VALUES ('a19838f9-24f5-4ebf-8665-85c034770093', 'Antoine', 'de Saint-Exupéry'),
       ('e2bda0f0-bc0e-4217-822a-8b3d5490a146', 'Victor', 'Hugo'),
       ('4afee7f8-c02a-4601-aacf-4f3c63e26322', 'Albert', 'Camus'),
       ('fed85b46-ea72-4964-8742-53aefa96cc5b', 'George', 'Orwell'),
       ('090a2e56-2e19-4ab5-b122-486a3fee4ac2', 'Émile', 'Zola') ON CONFLICT DO NOTHING;

INSERT INTO genre (id, name)
VALUES ('129664f7-3c0b-40a1-90d0-b4976010bf7b', 'Fiction'),
       ('0404449b-6923-4c11-8b84-7d78dc2b012d', 'Dystopian'),
       ('7270c4a6-1bbc-41ab-b003-4d78fec0b0d8', 'Philosophy'),
       ('db6fd249-9ce2-4ef3-84e6-1eeb66580ef3', 'Naturalism') ON CONFLICT DO NOTHING;

INSERT INTO book (id, title, summary, isbn, publisher, published_at, created_at)
VALUES ('9985f5fd-0130-4624-b8ba-9228ae8087e7', 'Le Petit Prince',
        'Un classique de la littérature française',
        '978-2-07-061275-8', 'Gallimard', '1943-04-06', CURRENT_TIMESTAMP),
       ('ae7fbad7-7810-45bf-b872-519c7a349180', 'Les Misérables',
        'Un roman historique sur la société française du XIXᵉ siècle',
        '978-2-07-040922-8', 'Gallimard', '1862-03-30', CURRENT_TIMESTAMP),
       ('d5207efe-52f2-4143-baed-42fd9a64e6f7', 'L''Étranger',
        'Un roman philosophique sur l''absurde',
        '978-2-07-036003-1', 'Gallimard', '1942-05-19', CURRENT_TIMESTAMP),
       ('a62eb2d1-5e5e-4036-9a6c-2e8cac46185f', '1984',
        'Une dystopie célèbre sur la surveillance de masse',
        '978-0-14-103614-4', 'Penguin', '1949-06-08', CURRENT_TIMESTAMP),
       ('fa621760-85ca-4b77-8905-f5e17c97da15', 'L''Assommoir',
        'Un roman naturaliste sur la classe ouvrière parisienne',
        '978-2-07-041714-8', 'Hachette', '1877-01-01', CURRENT_TIMESTAMP) ON CONFLICT DO NOTHING;

INSERT INTO author_book (book_id, author_id)
VALUES ('9985f5fd-0130-4624-b8ba-9228ae8087e7', 'a19838f9-24f5-4ebf-8665-85c034770093'),
       ('ae7fbad7-7810-45bf-b872-519c7a349180', 'e2bda0f0-bc0e-4217-822a-8b3d5490a146'),
       ('d5207efe-52f2-4143-baed-42fd9a64e6f7', '4afee7f8-c02a-4601-aacf-4f3c63e26322'),
       ('a62eb2d1-5e5e-4036-9a6c-2e8cac46185f', 'fed85b46-ea72-4964-8742-53aefa96cc5b'),
       ('fa621760-85ca-4b77-8905-f5e17c97da15', '090a2e56-2e19-4ab5-b122-486a3fee4ac2') ON CONFLICT DO NOTHING;

INSERT INTO book_genre (book_id, genre_id)
VALUES ('9985f5fd-0130-4624-b8ba-9228ae8087e7', '129664f7-3c0b-40a1-90d0-b4976010bf7b'),
       ('ae7fbad7-7810-45bf-b872-519c7a349180', '129664f7-3c0b-40a1-90d0-b4976010bf7b'),
       ('d5207efe-52f2-4143-baed-42fd9a64e6f7', '7270c4a6-1bbc-41ab-b003-4d78fec0b0d8'),
       ('a62eb2d1-5e5e-4036-9a6c-2e8cac46185f', '129664f7-3c0b-40a1-90d0-b4976010bf7b'),
       ('a62eb2d1-5e5e-4036-9a6c-2e8cac46185f', '0404449b-6923-4c11-8b84-7d78dc2b012d'),
       ('fa621760-85ca-4b77-8905-f5e17c97da15', '129664f7-3c0b-40a1-90d0-b4976010bf7b'),
       ('fa621760-85ca-4b77-8905-f5e17c97da15', 'db6fd249-9ce2-4ef3-84e6-1eeb66580ef3') ON CONFLICT DO NOTHING;
