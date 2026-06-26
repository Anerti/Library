-- Seed script for DELETE /authors/{authorId} endpoint
-- Run this before test_delete_author.sh
-- Uses fixed IDs generated with uuidgen for idempotent replay.

INSERT INTO author (id, first_name, last_name)
VALUES ('f2ad8c09-b044-4519-b9cc-471c23a594e1', 'Delete', 'Me')
ON CONFLICT (id) DO NOTHING;
