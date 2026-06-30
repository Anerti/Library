#! /usr/bin/env bash
# Test script for DELETE /users/{id} endpoint
# Requires seed users to exist (admin@library.com and marie@mail.com login users).
# Requires db/user/seed_user.sql to be run first (provides John Doe, CUSTOMER).
# Self-delete only: a user can only delete their own account.
#
# User IDs (from db/auth/seed_user_for_login.sql):
#   fcd16cd1-0f2b-460e-a6c2-3dece9e89d22  —  Marie Dupont (CUSTOMER)
#   ebd9a337-8ae6-4ad7-be38-151d81bc27c9  —  Admin System  (ADMIN)
#
# Seeded user (from db/user/seed_user.sql):
#   e5477ed7-046b-4974-983b-4fa0702991d8  —  John Doe (CUSTOMER)

MARIE_ID="fcd16cd1-0f2b-460e-a6c2-3dece9e89d22"
JOHN_ID="e5477ed7-046b-4974-983b-4fa0702991d8"
MARIE_TOKEN=$(curlie POST "http://localhost:8080/auth/login" email="marie@mail.com" password="Str0ng!Passphrase1" | jq -r '.token')
ADMIN_TOKEN=$(curlie POST "http://localhost:8080/auth/login" email="admin@library.com" password="Str0ng!Passphrase1" | jq -r '.token')

echo "── 1) 204 — DELETE /users/{id} (own account, CUSTOMER)  →  204 / no content"
curlie -H "Authorization:Bearer ${MARIE_TOKEN}" DELETE "http://localhost:8080/users/${MARIE_ID}"
echo

echo "── 2) 400 — DELETE /users/{invalid} (malformed UUID)  →  400 / bad request"
curlie -H "Authorization:Bearer ${ADMIN_TOKEN}" DELETE "http://localhost:8080/users/not-a-uuid"
echo

echo "── 3) 404 — DELETE /users/{id} (already deleted)  →  404 / not found"
curlie -H "Authorization:Bearer ${MARIE_TOKEN}" DELETE "http://localhost:8080/users/${MARIE_ID}"
echo

echo "── 4) 403 — DELETE /users/{id} (ADMIN tries to delete another user)  →  403 / forbidden"
curlie -H "Authorization:Bearer ${ADMIN_TOKEN}" DELETE "http://localhost:8080/users/${JOHN_ID}"
echo

echo "── 5) 401 — DELETE /users/{id} (no token)  →  401 / unauthorized"
curlie DELETE "http://localhost:8080/users/${JOHN_ID}"
echo
