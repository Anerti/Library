#! /usr/bin/env bash
# Test script for DELETE /books/{bookId} endpoint
# Requires seed_book_to_delete.sql to be run first.
# Requires admin JWT (Bearer token obtained from POST /auth/login)
#
# Seeded book:
#   39a86751-7098-4460-a7fd-fc4ece9ea996  —  Le Comte de Monte-Cristo
SUCCESS_ID="39a86751-7098-4460-a7fd-fc4ece9ea996"

ADMIN_TOKEN=$(curlie POST "http://localhost:8080/auth/login" email="admin@library.com" password="Str0ng!Passphrase1" | jq -r '.token')

echo "── 1) 204 — DELETE /books/{id} (existing book)  →  204 / no content"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" DELETE "http://localhost:8080/books/${SUCCESS_ID}"
echo

echo "── 2) 400 — DELETE /books/{invalid} (malformed UUID)  →  400 / bad request"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" DELETE "http://localhost:8080/books/not-a-uuid"
echo

echo "── 3) 404 — DELETE /books/{id} (already deleted)  →  404 / not found"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" DELETE "http://localhost:8080/books/${SUCCESS_ID}"
echo