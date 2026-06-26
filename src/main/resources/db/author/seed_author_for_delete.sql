INSERT INTO author (id, first_name, last_name)
VALUES ('f2ad8c09-b044-4519-b9cc-471c23a594e1', 'Delete', 'Me')
ON CONFLICT DO NOTHING;
