INSERT INTO book (id, title, summary, isbn, publisher, published_at, created_at)
VALUES ('9f0f4191-c2e4-4e89-8773-4b8d1568c6da', 'Le Petit Prince', 'Un classique de la littérature française', '978-2-07-061275-8', 'Gallimard', '1943-04-06', CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;
