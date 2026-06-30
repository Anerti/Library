#! /usr/bin/env bash
# Test script for DELETE /users/{id} endpoint
# Requires db/user/seed_user.sql to be run first.
# Requires admin JWT (Bearer token obtained from POST /auth/login)
#
# Seeded user:
#   e5477ed7-046b-4974-983b-4fa0702991d8  —  John Doe

USER_ID="e5477ed7-046b-4974-983b-4fa0702991d8"
ADMIN_TOKEN=$(curlie POST "http://localhost:8080/auth/login" email="admin@library.com" password="Str0ng!Passphrase1" | jq -r '.token')
CUSTOMER_TOKEN=$(curlie POST "http://localhost:8080/auth/login" email="marie@mail.com" password="Str0ng!Passphrase1" | jq -r '.token')

echo "── 1) 204 — DELETE /users/{id} (existing user)  →  204 / no content"
curlie -H "Authorization:Bearer ***" DELETE "http://localhost:8080/users/${USER_ID}"
echo

echo "── 2) 400 — DELETE /users/{invalid} (malformed UUID)  →  400 / bad request"
curlie -H "Authorization:Bearer ***" DELETE "http://localhost:8080/users/not-a-uuid"
echo

echo "── 3) 404 — DELETE /users/{id} (already deleted)  →  404 / not found"
curlie -H "Authorization:Bearer ***" DELETE "http://localhost:8080/users/${USER_ID}"
echo

echo "── 4) 401 — DELETE /users/{id} (no token)  →  401 / unauthorized"
curlie DELETE "http://localhost:8080/users/${USER_ID}"
echo
