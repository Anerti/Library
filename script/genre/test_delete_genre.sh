#! /usr/bin/env bash
# Test script for DELETE /genres/{genreId} endpoint
# Requires: seed_genre_for_delete.sql to be run first to create the genre.
# Requires admin JWT (Bearer token obtained from POST /auth/login)

ADMIN_TOKEN=$(curlie POST "http://localhost:8080/auth/login" email="admin@library.com" password="Str0ng!Passphrase1" | jq -r '.token')

echo "── 1) 204 — DELETE /genres/{uuid} (existing genre)  →  204 / no content"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" DELETE "http://localhost:8080/genres/6d6a30cb-bb49-48a1-9a46-81354d600eac"
echo

echo "── 2) 400 — DELETE /genres/{invalid} (malformed UUID)  →  400 / bad request"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" DELETE "http://localhost:8080/genres/not-a-uuid"
echo

echo "── 3) 404 — DELETE /genres/{uuid} (already deleted)  →  404 / not found"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" DELETE "http://localhost:8080/genres/6d6a30cb-bb49-48a1-9a46-81354d600eac"
echo
