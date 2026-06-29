#! /usr/bin/env bash
# Test script for POST /libraries endpoint
# Requires admin JWT (Bearer token obtained from POST /auth/login)

ADMIN_TOKEN=$(curlie POST "http://localhost:8080/auth/login" email="admin@library.com" password="Str0ng!Passphrase1" | jq -r '.token')

echo "── 1) 201 — POST /libraries (all valid)  →  201 / id + fields"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" POST "http://localhost:8080/libraries" name="Central Library" phone="+261 33 44 55 77" email="test-create@test.mg" address="Main Street"
echo

echo "── 2) 201 — POST /libraries (another valid)  →  201 / different data"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" POST "http://localhost:8080/libraries" name="Parc Library" phone="+261 34 98 765 43" email="parc-create@test.mg" address="Parc de la Lecture"
echo

echo "── 3) 409 — POST /libraries (duplicate email from test 1)  →  409 / already exists"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" POST "http://localhost:8080/libraries" name="Central Library" phone="+261 33 44 55 77" email="test-create@test.mg" address="Main Street"
echo

echo "── 4) 422 — POST /libraries (name blank)  →  422 / name is required and cannot be blank"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" POST "http://localhost:8080/libraries" name="" phone="+261 33 44 55 77" email="no-name@test.mg" address="Main Street"
echo

echo "── 5) 422 — POST /libraries (name with digits)  →  422 / forbidden characters"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" POST "http://localhost:8080/libraries" name="Library1" phone="+261 33 44 55 77" email="bad-name@test.mg" address="Main Street"
echo

echo "── 6) 422 — POST /libraries (phone blank)  →  422 / phone is required and cannot be blank"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" POST "http://localhost:8080/libraries" name="Central Library" phone="" email="no-phone@test.mg" address="Main Street"
echo

echo "── 7) 422 — POST /libraries (phone invalid)  →  422 / Invalid phone format"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" POST "http://localhost:8080/libraries" name="Central Library" phone="abc" email="bad-phone@test.mg" address="Main Street"
echo

echo "── 8) 422 — POST /libraries (email blank)  →  422 / email is required and cannot be blank"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" POST "http://localhost:8080/libraries" name="Central Library" phone="+261 33 44 55 77" email="" address="Main Street"
echo

echo "── 9) 422 — POST /libraries (email invalid)  →  422 / Invalid email format"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" POST "http://localhost:8080/libraries" name="Central Library" phone="+261 33 44 55 77" email="not-an-email" address="Main Street"
echo

echo "── 10) 422 — POST /libraries (address blank)  →  422 / address is required and cannot be blank"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" POST "http://localhost:8080/libraries" name="Central Library" phone="+261 33 44 55 77" email="no-address@test.mg" address=""
echo

echo "── 11) 422 — POST /libraries (address invalid chars)  →  422 / invalid characters"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" POST "http://localhost:8080/libraries" name="Central Library" phone="+261 33 44 55 77" email="bad-address@test.mg" address="123 Main St|"
echo
