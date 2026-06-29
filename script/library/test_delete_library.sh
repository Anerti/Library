#! /usr/bin/env bash
# Test script for DELETE /libraries/{libraryId} endpoint
# Requires: seed_library_to_delete.sql to be run first to create the library.
# Requires admin JWT (Bearer token obtained from POST /auth/login)

ADMIN_TOKEN=$(curlie POST "http://localhost:8080/auth/login" email="admin@library.com" password="Str0ng!Passphrase1" | jq -r '.token')

echo "── 1) 204 — DELETE /libraries/{uuid} (existing library)  →  204 / no content"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" DELETE "http://localhost:8080/libraries/51d81748-207c-4a67-b3bc-9434634bbad5"
echo

echo "── 2) 400 — DELETE /libraries/{invalid} (malformed UUID)  →  400 / bad request"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" DELETE "http://localhost:8080/libraries/not-a-uuid"
echo

echo "── 3) 404 — DELETE /libraries/{uuid} (non-existent UUID)  →  404 / not found"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" DELETE "http://localhost:8080/libraries/51d81748-207c-4a67-b3bc-9434634bbad5"
echo
