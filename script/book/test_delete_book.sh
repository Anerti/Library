#! /usr/bin/env bash
# Test script for DELETE /books/{bookId} endpoint
# Requires seed_book_to_delete.sql to be run first.
#
# Seeded book:
#   39a86751-7098-4460-a7fd-fc4ece9ea996  —  Le Comte de Monte-Cristo
SUCCESS_ID="39a86751-7098-4460-a7fd-fc4ece9ea996"

echo "── 1) 204 — DELETE /books/{id} (existing book)  →  204 / no content"
curlie DELETE "http://localhost:8080/books/${SUCCESS_ID}"
echo

echo "── 2) 400 — DELETE /books/{invalid} (malformed UUID)  →  400 / bad request"
curlie DELETE "http://localhost:8080/books/not-a-uuid"
echo

echo "── 3) 404 — DELETE /books/{id} (already deleted)  →  404 / not found"
curlie DELETE "http://localhost:8080/books/${SUCCESS_ID}"
echo