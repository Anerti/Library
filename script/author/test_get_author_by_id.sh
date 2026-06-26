#! /usr/bin/env bash
# Test script for GET /authors/{authorId} endpoint
# Requires seed_author_for_get_by_id.sql to be run first.

AUTHOR_ID_1="81bfef1d-f921-4c1c-b3fd-77258c269ad3"
AUTHOR_ID_2="5fb6d6fe-9c70-4818-b500-6e7aabeb276d"
AUTHOR_ID_3="595e6446-cac7-43c0-b737-8e6fb0e87c22"

echo "── 1) OK — GET /authors/{authorId} (Kafka)  →  200 / Franz Kafka"
curlie "http://localhost:8080/authors/${AUTHOR_ID_1}"
echo

echo "── 2) OK — GET /authors/{authorId} (Woolf)  →  200 / Virginia Woolf"
curlie "http://localhost:8080/authors/${AUTHOR_ID_2}"
echo

echo "── 3) OK — GET /authors/{authorId} (García Márquez)  →  200 / Gabriel García Márquez"
curlie "http://localhost:8080/authors/${AUTHOR_ID_3}"
echo

echo "── 4) 404 — GET /authors/{authorId} with non-existent UUID  →  404"
curlie "http://localhost:8080/authors/00000000-0000-0000-0000-000000000000"
echo

echo "── 5) 404 — GET /authors/{authorId} with random UUID  →  404"
curlie "http://localhost:8080/authors/a1111111-b222-4333-c444-e55555555555"
echo

echo "── 6) 400 — GET /authors/{authorId} with invalid UUID format  →  400"
curlie "http://localhost:8080/authors/not-a-uuid"
echo
