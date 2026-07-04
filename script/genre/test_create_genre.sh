#! /usr/bin/env bash
# Test script for POST /genres endpoint
# Requires admin JWT (Bearer token obtained from POST /auth/login)

ADMIN_TOKEN=$(curlie POST "http://localhost:8080/auth/login" email="admin@library.com" password="Str0ng!Passphrase1" | jq -r '.token')
CUSTOMER_TOKEN=$(curlie POST "http://localhost:8080/auth/login" email="marie@mail.com" password="Str0ng!Passphrase1" | jq -r '.token')

# Test 1 + 2: create → duplicate (self-contained conflict)
echo "── 1) 201 — POST /genres (simple name)  →  201 / Test-Duplicate"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" POST "http://localhost:8080/genres" name="Test-Duplicate"
echo

echo "── 2) 409 — POST /genres (duplicate of test 1)  →  409 / already exists"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" POST "http://localhost:8080/genres" name="Test-Duplicate"
echo

echo "── 3) 201 — POST /genres (accented name)  →  201 / Poésie"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" POST "http://localhost:8080/genres" name="Poésie"
echo

echo "── 4) 422 — POST /genres (blank name)  →  422 / name is required and cannot be blank"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" POST "http://localhost:8080/genres" name=""
echo

echo "── 5) 422 — POST /genres (name with digits)  →  422 / forbidden characters"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" POST "http://localhost:8080/genres" name="Fantasy1"
echo

echo "── 6) 422 — POST /genres (name with underscore)  →  422 / forbidden characters"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" POST "http://localhost:8080/genres" name="Fan_tasy"
echo

echo "── 7) 422 — POST /genres (name too long)  →  422 / cannot be longer than 100"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" POST "http://localhost:8080/genres" name="This name is definitely way too ridiculously long for a genre because the maximum allowed length is one hundred characters"
echo

echo "── 8) 403 — POST /genres (customer token)  →  403 / forbidden"
curlie -H "Authorization:Bearer $CUSTOMER_TOKEN" POST "http://localhost:8080/genres" name="CustomerGenre"
echo
