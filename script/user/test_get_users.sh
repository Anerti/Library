#! /usr/bin/env bash
# Test script for GET /users endpoint
# Requires seed users from db/user/seed_users.sql and db/auth/seed_user_for_login.sql.
# GET /users is ADMIN-only.
#
# Login users (from db/auth/seed_user_for_login.sql):
#   fcd16cd1-0f2b-460e-a6c2-3dece9e89d22  —  Marie Dupont   (CUSTOMER)
#   ebd9a337-8ae6-4ad7-be38-151d81bc27c9  —  Admin System    (ADMIN)
# Password for both: Str0ng!Passphrase1

ADMIN_TOKEN=$(curlie POST "http://localhost:8080/auth/login" email="admin@library.com" password="Str0ng!Passphrase1" | jq -r '.token')
CUSTOMER_TOKEN=$(curlie POST "http://localhost:8080/auth/login" email="marie@mail.com" password="Str0ng!Passphrase1" | jq -r '.token')

echo "── 1) OK — GET /users  →  200 / 20 users"
curlie -H "Authorization:Bearer ${ADMIN_TOKEN}" GET "http://localhost:8080/users"
echo

echo "── 2) OK — GET /users?search=Claire  →  200 / Claire Dupuis"
curlie -H "Authorization:Bearer ${ADMIN_TOKEN}" GET "http://localhost:8080/users?search=Claire"
echo

echo "── 3) OK — GET /users?search=Dupuis  →  200 / Claire Dupuis"
curlie -H "Authorization:Bearer ${ADMIN_TOKEN}" GET "http://localhost:8080/users?search=Dupuis"
echo

echo "── 4) OK — GET /users?search=manon.garcia  →  200 / Manon Garcia"
curlie -H "Authorization:Bearer ${ADMIN_TOKEN}" GET "http://localhost:8080/users?search=manon.garcia"
echo

echo "── 5) OK — GET /users?search=NONEXISTENT  →  200 / data=null / pagination.total=0"
curlie -H "Authorization:Bearer ${ADMIN_TOKEN}" GET "http://localhost:8080/users?search=NONEXISTENT"
echo

echo "── 6) OK — GET /users?page=1&size=3  →  200 / pagination.size=3"
curlie -H "Authorization:Bearer ${ADMIN_TOKEN}" GET "http://localhost:8080/users?page=1&size=3"
echo

echo "── 7) OK — GET /users?page=99&size=20  →  200 / data=null / pagination.total=20"
curlie -H "Authorization:Bearer ${ADMIN_TOKEN}" GET "http://localhost:8080/users?page=99&size=20"
echo

echo "── 8) 422 — GET /users?search=<script>  →  422 invalid characters"
curlie -H "Authorization:Bearer ${ADMIN_TOKEN}" GET "http://localhost:8080/users" "search==<script>"
echo

echo "── 9) 422 — GET /users?search=|bad  →  422 invalid characters"
curlie -H "Authorization:Bearer ${ADMIN_TOKEN}" GET "http://localhost:8080/users" "search==|bad"
echo

echo "── 10) OK — GET /users?search= (empty)  →  200 / 20 users (same as list all)"
curlie -H "Authorization:Bearer ${ADMIN_TOKEN}" GET "http://localhost:8080/users?search="
echo

echo "── 11) 401 — GET /users (no token)  →  401 / authentication required"
curlie GET "http://localhost:8080/users"
echo

echo "── 12) 403 — GET /users (customer token)  →  403 / insufficient privileges"
curlie -H "Authorization:Bearer ${CUSTOMER_TOKEN}" GET "http://localhost:8080/users"
echo
