#! /usr/bin/env bash
# Test script for GET /genres/{genreId} endpoint
# Requires seed_genre.sql to be run first.

GENRE_ID_1="a820bef3-0ef1-47b9-8e78-36793b7c1a11"

echo "── 1) OK — GET /genres/{genreId}  →  200 / Fantasy"
curlie "http://localhost:8080/genres/${GENRE_ID_1}"
echo

echo "── 2) 404 — GET /genres/{genreId} with non-existent UUID  →  404"
curlie "http://localhost:8080/genres/00000000-0000-0000-0000-000000000000"
echo

echo "── 3) 404 — GET /genres/{genreId} with random UUID  →  404"
curlie "http://localhost:8080/genres/a1111111-b222-4333-c444-e55555555555"
echo

echo "── 4) 400 — GET /genres/{genreId} with invalid UUID format  →  400"
curlie "http://localhost:8080/genres/not-a-uuid"
echo

