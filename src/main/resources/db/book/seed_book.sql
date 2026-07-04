INSERT INTO book (id, title, summary, isbn, publisher, published_at, created_at)
VALUES ('9f0f4191-c2e4-4e89-8773-4b8d1568c6da', 'Le Petit Prince', 'Un classique de la littérature française', '978-2-07-061275-8', 'Gallimard', '1943-04-06', CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

INSERT INTO author (id, first_name, last_name)
VALUES ('a9358baa-5aa4-4d25-8dd8-ea404e968b29', 'Aldous', 'Huxley'),
       ('b2690ef3-0dcc-4eaf-af4c-6f0e5114b27e', 'Margaret', 'Atwood')
ON CONFLICT DO NOTHING;

INSERT INTO genre (id, name)
VALUES ('e0e9e5b8-8a75-481c-81f6-283913040dd7', 'Dystopian'),
       ('f5e70e4f-2db3-41c2-b0db-d8be43a3dcf2', 'Speculative Fiction')
    ON CONFLICT DO NOTHING;

INSERT INTO book (id, title, summary, isbn, publisher, published_at, created_at)
VALUES ('e2b3f6e1-7c1d-4b8a-9a6e-3a6f9b2c1d4e', 'Brave New World', 'A dystopian social science fiction novel', '978-0-06-085052-4', 'Harper Perennial', '1932-08-01', CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

INSERT INTO author_book (book_id, author_id)
VALUES ('e2b3f6e1-7c1d-4b8a-9a6e-3a6f9b2c1d4e', 'a9358baa-5aa4-4d25-8dd8-ea404e968b29'),
       ('e2b3f6e1-7c1d-4b8a-9a6e-3a6f9b2c1d4e', 'b2690ef3-0dcc-4eaf-af4c-6f0e5114b27e')
ON CONFLICT DO NOTHING;

INSERT INTO book_genre (book_id, genre_id)
VALUES ('e2b3f6e1-7c1d-4b8a-9a6e-3a6f9b2c1d4e', 'e0e9e5b8-8a75-481c-81f6-283913040dd7'),
       ('e2b3f6e1-7c1d-4b8a-9a6e-3a6f9b2c1d4e', 'f5e70e4f-2db3-41c2-b0db-d8be43a3dcf2')
ON CONFLICT DO NOTHING;
