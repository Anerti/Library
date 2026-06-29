#! /usr/bin/env bash
# Test script for DELETE /authors/{authorId} endpoint
# Requires seed_author_for_delete.sql to be run first.
# Requires admin JWT (Bearer token obtained from POST /auth/login)
#
# Seeded author:
#   f2ad8c09-b044-4519-b9cc-471c23a594e1  —  Delete Me

AUTHOR_ID="f2ad8c09-b044-4519-b9cc-471c23a594e1"
ADMIN_TOKEN=$(curlie POST "http://localhost:8080/auth/login" email="admin@library.com" password="Str0ng!Passphrase1" | jq -r '.token')
CUSTOMER_TOKEN=$(curlie POST "http://localhost:8080/auth/login" email="marie@mail.com" password="Str0ng!Passphrase1" | jq -r '.token')

echo "── 1) 204 — DELETE /authors/{uuid} (existing author)  →  204 / no content"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" DELETE "http://localhost:8080/authors/${AUTHOR_ID}"
echo

echo "── 2) 400 — DELETE /authors/{invalid} (malformed UUID)  →  400 / bad request"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" DELETE "http://localhost:8080/authors/not-a-uuid"
echo

echo "── 3) 404 — DELETE /authors/{uuid} (already deleted)  →  404 / not found"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" DELETE "http://localhost:8080/authors/${AUTHOR_ID}"
echo

echo "── 4) 403 — DELETE /authors/{uuid} (customer token)  →  403 / forbidden"
curlie -H "Authorization:Bearer $CUSTOMER_TOKEN" DELETE "http://localhost:8080/authors/${AUTHOR_ID}"
echo

echo "── 5) 401 — DELETE /authors/{uuid} (no token)  →  401 / unauthorized"
curlie DELETE "http://localhost:8080/authors/${AUTHOR_ID}"
echo
