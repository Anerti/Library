#! /usr/bin/env bash
# Test script for GET /users/{id} endpoint
# Requires seed users to exist (alice.reader@mail.com and bob.moderator@mail.com login users).
# Requires db/user/seed_user_for_get.sql to be run first.
# Self-read + ADMIN-can-read-CUSTOMER.
#
# Login user IDs (from db/user/seed_user_for_get.sql):
#   943781cb-8237-46e1-9da5-59d3c11e972f  —  Alice Reader   (CUSTOMER)
#   383d233b-06cb-4383-a18e-7be27adaca37  —  Bob Moderator  (ADMIN)
# Password for both: password123
#
# Non-existent UUID used across tests:
#   00000000-0000-0000-0000-000000000000  —  non-existent user

ALICE_ID="943781cb-8237-46e1-9da5-59d3c11e972f"
BOB_ID="383d233b-06cb-4383-a18e-7be27adaca37"
NONEXISTENT_ID="00000000-0000-0000-0000-000000000000"

ALICE_TOKEN=$(curlie POST "http://localhost:8080/auth/login" email="alice.reader@mail.com" password="Str0ng!Passphrase1" | jq -r '.token')
BOB_TOKEN=$(curlie POST "http://localhost:8080/auth/login" email="bob.moderator@mail.com" password="Str0ng!Passphrase1" | jq -r '.token')

echo "── 1) 200 — CUSTOMER (Alice) reads own account  →  200 / Alice Reader"
curlie -H "Authorization:Bearer ${ALICE_TOKEN}" "http://localhost:8080/users/${ALICE_ID}"
echo

echo "── 2) 200 — ADMIN (Bob) reads own account  →  200 / Bob Moderator"
curlie -H "Authorization:Bearer ${BOB_TOKEN}" "http://localhost:8080/users/${BOB_ID}"
echo

echo "── 3) 200 — ADMIN (Bob) reads CUSTOMER (Alice)  →  200 / Alice Reader"
curlie -H "Authorization:Bearer ${BOB_TOKEN}" "http://localhost:8080/users/${ALICE_ID}"
echo

echo "── 4) 403 — CUSTOMER (Alice) tries to read ADMIN (Bob)  →  403 / forbidden"
curlie -H "Authorization:Bearer ${ALICE_TOKEN}" "http://localhost:8080/users/${BOB_ID}"
echo

echo "── 5) 403 — CUSTOMER (Alice) reads non-existent UUID  →  403 / forbidden"
curlie -H "Authorization:Bearer ${ALICE_TOKEN}" "http://localhost:8080/users/${NONEXISTENT_ID}"
echo

echo "── 6) 404 — ADMIN (Bob) reads non-existent UUID  →  404 / not found"
curlie -H "Authorization:Bearer ${BOB_TOKEN}" "http://localhost:8080/users/${NONEXISTENT_ID}"
echo

echo "── 7) 400 — GET /users/{id} (malformed UUID)  →  400 / bad request"
curlie -H "Authorization:Bearer ${ALICE_TOKEN}" "http://localhost:8080/users/not-a-uuid"
echo

echo "── 8) 401 — GET /users/{id} (no token)  →  401 / unauthorized"
curlie "http://localhost:8080/users/${ALICE_ID}"
echo
