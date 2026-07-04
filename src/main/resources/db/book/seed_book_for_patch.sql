INSERT INTO book (id, title, summary, isbn, publisher, published_at, created_at)
VALUES ('2ed55e73-fdf3-4879-a333-f38233fb9a88', 'Vingt mille lieues sous les mers', 'Un roman de science-fiction de Jules Verne', '978-2-253-09864-3', 'Hetzel', '1870-01-01', CURRENT_TIMESTAMP),
       ('5d4962d3-f460-4268-a6f3-27459f6bf8bf', 'Un barrage contre le Pacifique', 'Le premier roman de Marguerite Duras', '978-2-07-036780-1', 'Gallimard', '1950-01-01', CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;
