#! /usr/bin/env bash
# Test script for PATCH /users/{id} endpoint
# Requires seed users to exist (admin@library.com and marie@mail.com login users).
# Requires db/user/seed_user_for_patch.sql to be run first (provides Jeanne, Tiana, Faly).
# Access rules: CUSTOMER can only patch own account; ADMIN can patch CUSTOMER but not another ADMIN.
#
# Login user IDs (from db/auth/seed_user_for_login.sql):
#   fcd16cd1-0f2b-460e-a6c2-3dece9e89d22  —  Marie Dupont (CUSTOMER)
#   ebd9a337-8ae6-4ad7-be38-151d81bc27c9  —  Admin System  (ADMIN)
#
# Seeded target users (from db/user/seed_user_for_patch.sql):
#   d3c7acc2-b37f-4ac3-ad8b-0707de99da3e  —  Jeanne Dupont (CUSTOMER)
#   b430abf6-da95-4609-a9bf-8af324a6b169  —  Tiana Rabe    (CUSTOMER)
#   43f5c06a-b0c9-4992-80a1-71c5507a07f1  —  Faly Rakoto   (ADMIN)

MARIE_ID="fcd16cd1-0f2b-460e-a6c2-3dece9e89d22"
ADMIN_ID="ebd9a337-8ae6-4ad7-be38-151d81bc27c9"
JEANNE_ID="d3c7acc2-b37f-4ac3-ad8b-0707de99da3e"
TIANA_ID="b430abf6-da95-4609-a9bf-8af324a6b169"
FALY_ID="43f5c06a-b0c9-4992-80a1-71c5507a07f1"
NONEXISTENT_ID="00000000-0000-0000-0000-000000000000"

MARIE_TOKEN=$(curlie POST "http://localhost:8080/auth/login" email="marie@mail.com" password="Str0ng!Passphrase1" | jq -r '.token')
ADMIN_TOKEN=$(curlie POST "http://localhost:8080/auth/login" email="admin@library.com" password="Str0ng!Passphrase1" | jq -r '.token')

echo "── 1) 200 — CUSTOMER (Marie) patches own firstName  →  200 / updated firstName"
curlie -H "Authorization:Bearer ${MARIE_TOKEN}" PATCH "http://localhost:8080/users/${MARIE_ID}" firstName="Marie Updated"
echo

echo "── 2) 200 — CUSTOMER (Marie) patches own email  →  200 / updated email"
curlie -H "Authorization:Bearer ${MARIE_TOKEN}" PATCH "http://localhost:8080/users/${MARIE_ID}" email="marie.updated@mail.com"
echo

echo "── 3) 200 — CUSTOMER (Marie) patches own phone  →  200 / updated phone"
curlie -H "Authorization:Bearer ${MARIE_TOKEN}" PATCH "http://localhost:8080/users/${MARIE_ID}" phone="+261 32 999 999"
echo

echo "── 4) 200 — CUSTOMER (Marie) patches own birthDate  →  200 / updated birthDate"
curlie -H "Authorization:Bearer ${MARIE_TOKEN}" PATCH "http://localhost:8080/users/${MARIE_ID}" birthDate="1990-01-01"
echo

echo "── 5) 200 — CUSTOMER (Marie) patches all fields at once  →  200 / all fields"
curlie -H "Authorization:Bearer ${MARIE_TOKEN}" PATCH "http://localhost:8080/users/${MARIE_ID}" \
  lastName="Dupont" firstName="Marie" birthDate="1995-03-10" email="marie@mail.com" phone="+261 32 456 789"
echo

echo "── 6) 200 — ADMIN patches CUSTOMER (Jeanne) lastName  →  200 / updated lastName"
curlie -H "Authorization:Bearer ${ADMIN_TOKEN}" PATCH "http://localhost:8080/users/${JEANNE_ID}" lastName="DupontUpdated"
echo

echo "── 7) 403 — CUSTOMER (Marie) patches another CUSTOMER (Tiana)  →  403 / forbidden"
curlie -H "Authorization:Bearer ${MARIE_TOKEN}" PATCH "http://localhost:8080/users/${TIANA_ID}" firstName="Hacked"
echo

echo "── 8) 403 — ADMIN patches another ADMIN (Faly)  →  403 / forbidden"
curlie -H "Authorization:Bearer ${ADMIN_TOKEN}" PATCH "http://localhost:8080/users/${FALY_ID}" firstName="Hacked"
echo

echo "── 9) 404 — PATCH /users/{id} (non-existent UUID)  →  404 / not found"
curlie -H "Authorization:Bearer ${ADMIN_TOKEN}" PATCH "http://localhost:8080/users/${NONEXISTENT_ID}" firstName="Nobody"
echo

echo "── 10) 400 — PATCH /users/{id} (malformed UUID)  →  400 / bad request"
curlie -H "Authorization:Bearer ${ADMIN_TOKEN}" PATCH "http://localhost:8080/users/not-a-uuid" firstName="Bad"
echo

echo "── 11) 422 — invalid email format  →  422 / unprocessable entity"
curlie -H "Authorization:Bearer ${MARIE_TOKEN}" PATCH "http://localhost:8080/users/${MARIE_ID}" email="not-an-email"
echo

echo "── 12) 422 — invalid phone format  →  422 / unprocessable entity"
curlie -H "Authorization:Bearer ${MARIE_TOKEN}" PATCH "http://localhost:8080/users/${MARIE_ID}" phone="abc"
echo

echo "── 13) 422 — invalid firstName (forbidden characters)  →  422 / unprocessable entity"
curlie -H "Authorization:Bearer ${MARIE_TOKEN}" PATCH "http://localhost:8080/users/${MARIE_ID}" firstName="Marie123"
echo

echo "── 14) 409 — email already in use (Admin patches Jeanne to marie@mail.com)  →  409 / conflict"
curlie -H "Authorization:Bearer ${ADMIN_TOKEN}" PATCH "http://localhost:8080/users/${JEANNE_ID}" email="marie@mail.com"
echo

echo "── 15) 401 — PATCH /users/{id} (no token)  →  401 / unauthorized"
curlie PATCH "http://localhost:8080/users/${MARIE_ID}" firstName="NoAuth"
echo
