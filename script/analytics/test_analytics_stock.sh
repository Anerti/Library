#! /usr/bin/env bash
# Test script for GET /libraries/{libraryId}/analytics/stock/{bookId} endpoint
# Requires seed_analytics_stock.sql to be run first.
#
# Expected data:
#   PAPERBACK → total=1, HARDCOVER → total=2, POCKET → total=1, ALL → total=4

LIB_ID="39795205-ffbc-4ace-92e4-925a8c413a88"
BOOK_ID="bb85ea41-0baa-4392-a874-747be604f868"

CUSTOMER_TOKEN=$(curlie POST "http://localhost:8080/auth/login" email="marie@mail.com" password="Str0ng!Passphrase1" | jq -r '.token')

echo "── 1) OK — GET /libraries/{id}/analytics/stock/{id} (ALL default)  →  200 / total=4, byFormat=null"
curlie "http://localhost:8080/libraries/${LIB_ID}/analytics/stock/${BOOK_ID}"
echo

echo "── 2) OK — GET /libraries/{id}/analytics/stock/{id}?format=ALL  →  200 / total=4, byFormat=null"
curlie "http://localhost:8080/libraries/${LIB_ID}/analytics/stock/${BOOK_ID}?format=ALL"
echo

echo "── 3) OK — GET /libraries/{id}/analytics/stock/{id}?format=PAPERBACK  →  200 / total=1, byFormat=PAPERBACK"
curlie "http://localhost:8080/libraries/${LIB_ID}/analytics/stock/${BOOK_ID}?format=PAPERBACK"
echo

echo "── 4) OK — GET /libraries/{id}/analytics/stock/{id}?format=HARDCOVER  →  200 / total=2, byFormat=HARDCOVER"
curlie "http://localhost:8080/libraries/${LIB_ID}/analytics/stock/${BOOK_ID}?format=HARDCOVER"
echo

echo "── 5) OK — GET /libraries/{id}/analytics/stock/{id}?format=POCKET  →  200 / total=1, byFormat=POCKET"
curlie "http://localhost:8080/libraries/${LIB_ID}/analytics/stock/${BOOK_ID}?format=POCKET"
echo

echo "── 6) 404 — GET with non-existent library UUID  →  404"
curlie "http://localhost:8080/libraries/00000000-0000-0000-0000-000000000000/analytics/stock/${BOOK_ID}"
echo

echo "── 7) 404 — GET with non-existent book UUID  →  404"
curlie "http://localhost:8080/libraries/${LIB_ID}/analytics/stock/00000000-0000-0000-0000-000000000000"
echo

echo "── 8) 400 — GET with invalid library UUID format  →  400"
curlie "http://localhost:8080/libraries/not-a-uuid/analytics/stock/${BOOK_ID}"
echo

echo "── 9) 422 — GET with invalid format value  →  422"
curlie "http://localhost:8080/libraries/${LIB_ID}/analytics/stock/${BOOK_ID}?format=INVALID"
echo

echo "── 10) OK — GET /libraries/{id}/analytics/stock/{id} (customer token)  →  200 / total=4"
curlie -H "Authorization:Bearer ***" "http://localhost:8080/libraries/${LIB_ID}/analytics/stock/${BOOK_ID}"
echo
