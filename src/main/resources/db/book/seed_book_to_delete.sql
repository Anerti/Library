INSERT INTO book (id, title, summary, isbn, publisher, published_at, created_at)
VALUES ('39a86751-7098-4460-a7fd-fc4ece9ea996', 'Le Comte de Monte-Cristo', 'Un roman d''aventure et de vengeance d''Alexandre Dumas', '978-2-07-040918-1', 'Gallimard', '1844-01-01', CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;