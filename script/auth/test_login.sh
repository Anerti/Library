#! /usr/bin/env bash
# Test script for POST /auth/login endpoint
#
# Prerequisites: seed_user_for_login.sql applied to the database.
# Update passwords below if the Argon2 hashes were generated with different plaintexts.

echo "── 1) 200 — POST /auth/login (CUSTOMER, marie@mail.com)  →  200 / token+user"
curlie POST "http://localhost:8080/auth/login" email="marie@mail.com" password="Str0ng!Passphrase1"
echo

echo "── 2) 200 — POST /auth/login (ADMIN, admin@library.com)  →  200 / token+user"
curlie POST "http://localhost:8080/auth/login" email="admin@library.com" password="Str0ng!Passphrase1"
echo

echo "── 3) 401 — POST /auth/login (wrong password)  →  401 / Invalid credentials"
curlie POST "http://localhost:8080/auth/login" email="marie@mail.com" password="WrongPass123!"
echo

echo "── 4) 401 — POST /auth/login (unknown email)  →  401 / Invalid credentials"
curlie POST "http://localhost:8080/auth/login" email="unknown@mail.com" password="Str0ng!Passphrase1"
echo

echo "── 5) 422 — POST /auth/login (email null)  →  422 / email is required"
curlie POST "http://localhost:8080/auth/login" password="Str0ng!Passphrase1"
echo

echo "── 6) 422 — POST /auth/login (email blank)  →  422 / email is required"
curlie POST "http://localhost:8080/auth/login" email="" password="Str0ng!Passphrase1"
echo

echo "── 7) 422 — POST /auth/login (invalid email format)  →  422 / Invalid email format"
curlie POST "http://localhost:8080/auth/login" email="not-an-email" password="Str0ng!Passphrase1"
echo

echo "── 8) 422 — POST /auth/login (email forbidden chars)  →  422 / Invalid input for email"
curlie POST "http://localhost:8080/auth/login" email="marie @mail.com" password="Str0ng!Passphrase1"
echo

echo "── 9) 422 — POST /auth/login (password null)  →  422 / password is required"
curlie POST "http://localhost:8080/auth/login" email="marie@mail.com"
echo

echo "── 10) 422 — POST /auth/login (password blank)  →  422 / password is required"
curlie POST "http://localhost:8080/auth/login" email="marie@mail.com" password=""
echo
