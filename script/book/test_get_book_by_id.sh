#! /usr/bin/env bash
# Test script for GET /books/{bookId} endpoint
# Requires seed_book.sql to be run first.

BOOK_ID="9f0f4191-c2e4-4e89-8773-4b8d1568c6da"
BOOK_WITH_RELS_ID="e2b3f6e1-7c1d-4b8a-9a6e-3a6f9b2c1d4e"

echo "── 1) OK — GET /books/{bookId}  →  200 / Le Petit Prince"
curlie "http://localhost:8080/books/${BOOK_ID}"
echo

echo "── 2) OK — GET /books/{bookId}  →  200 / Brave New World with authors and genres"
curlie "http://localhost:8080/books/${BOOK_WITH_RELS_ID}"
echo

echo "── 3) 404 — GET /books/{bookId} with non-existent UUID  →  404"
curlie "http://localhost:8080/books/00000000-0000-0000-0000-000000000000"
echo

echo "── 4) 404 — GET /books/{bookId} with random UUID  →  404"
curlie "http://localhost:8080/books/a1111111-b222-4333-c444-e55555555555"
echo

echo "── 5) 400 — GET /books/{bookId} with invalid UUID format  →  400"
curlie "http://localhost:8080/books/not-a-uuid"
echo
