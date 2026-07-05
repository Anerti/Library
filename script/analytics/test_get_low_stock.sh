#! /usr/bin/env bash
# Test script for GET /libraries/{libraryId}/analytics/low-stock endpoint
# Requires seed_analytics_low_stock.sql to be run first.
#
# Expected data (stock = arrivals - sold, default threshold=3):
#   "The Crystal Compass"  (Fantasy, Elena Harper, 978-0-00-000001-1)
#     PAPERBACK: 1 -> low   HARDCOVER: 3 -> low (=threshold)   POCKET: 0 -> low
#   "Quantum Horizons"     (Sci-Fi, James Moriarty, 978-0-00-000002-2)
#     PAPERBACK: 2 -> low   HARDCOVER: 4 -> ok                POCKET: 0 -> low
#   "The Silent Witness"   (Mystery, Lin Wei, 978-0-00-000003-3)
#     PAPERBACK: 2 -> low   HARDCOVER: 3 -> low (=threshold)  POCKET: 2 -> low
#   -> threshold=3: 8 rows   threshold=2: 6 rows   threshold=0: 2 rows

LIB_ID="0391f892-a6fc-41d0-8253-925b76bd72a0"

ADMIN_TOKEN=$(curlie POST "http://localhost:8080/auth/login" email="admin@library.com" password="Str0ng!Passphrase1" | jq -r '.token')
CUSTOMER_TOKEN=$(curlie POST "http://localhost:8080/auth/login" email="marie@mail.com" password="Str0ng!Passphrase1" | jq -r '.token')

echo "---- 1) OK -- GET (default threshold=3)  ->  200 / 8 rows"
curlie GET "http://localhost:8080/libraries/${LIB_ID}/analytics/low-stock" -H "Authorization:Bearer ${ADMIN_TOKEN}"
echo

echo "---- 2) OK -- GET ?threshold=2  ->  200 / 6 rows"
curlie GET "http://localhost:8080/libraries/${LIB_ID}/analytics/low-stock?threshold=2" -H "Authorization:Bearer ${ADMIN_TOKEN}"
echo

echo "---- 3) OK -- GET ?threshold=5  ->  200 / 9 rows"
curlie GET "http://localhost:8080/libraries/${LIB_ID}/analytics/low-stock?threshold=5" -H "Authorization:Bearer ${ADMIN_TOKEN}"
echo

echo "---- 4) OK -- GET ?threshold=0  ->  200 / 2 rows (only POCKET sold=0)"
curlie GET "http://localhost:8080/libraries/${LIB_ID}/analytics/low-stock?threshold=0" -H "Authorization:Bearer ${ADMIN_TOKEN}"
echo

echo "---- 5) OK -- GET ?genre=Fantasy  ->  200 / 3 rows (The Crystal Compass)"
curlie GET "http://localhost:8080/libraries/${LIB_ID}/analytics/low-stock?genre=Fantasy" -H "Authorization:Bearer ${ADMIN_TOKEN}"
echo

echo "---- 6) OK -- GET ?author=Elena  ->  200 / 3 rows (Elena Harper)"
curlie GET "http://localhost:8080/libraries/${LIB_ID}/analytics/low-stock?author=Elena" -H "Authorization:Bearer ${ADMIN_TOKEN}"
echo

echo "---- 7) OK -- GET ?title=Crystal  ->  200 / 3 rows (The Crystal Compass)"
curlie GET "http://localhost:8080/libraries/${LIB_ID}/analytics/low-stock?title=Crystal" -H "Authorization:Bearer ${ADMIN_TOKEN}"
echo

echo "---- 8) OK -- GET ?isbn=978-0-00-000001-1  ->  200 / 3 rows"
curlie GET "http://localhost:8080/libraries/${LIB_ID}/analytics/low-stock?isbn=978-0-00-000001-1" -H "Authorization:Bearer ${ADMIN_TOKEN}"
echo

echo "---- 9) OK -- GET ?genre=Fantasy&threshold=1  ->  200 / 2 rows (HARDCOVER=3 excluded)"
curlie GET "http://localhost:8080/libraries/${LIB_ID}/analytics/low-stock?genre=Fantasy&threshold=1" -H "Authorization:Bearer ${ADMIN_TOKEN}"
echo

echo "---- 10) OK -- GET ?genre=Nonexistent  ->  200 / empty array"
curlie GET "http://localhost:8080/libraries/${LIB_ID}/analytics/low-stock?genre=Nonexistent" -H "Authorization:Bearer ${ADMIN_TOKEN}"
echo

echo "---- 11) OK -- GET ?isbn=978-0-00-999999-9  ->  200 / empty array"
curlie GET "http://localhost:8080/libraries/${LIB_ID}/analytics/low-stock?isbn=978-0-00-999999-9" -H "Authorization:Bearer ${ADMIN_TOKEN}"
echo

echo "---- 12) 422 -- GET ?threshold=-1  ->  422 / Threshold must be greater than 0"
curlie GET "http://localhost:8080/libraries/${LIB_ID}/analytics/low-stock" "threshold==-1" -H "Authorization:Bearer ${ADMIN_TOKEN}"
echo

echo "---- 13) 422 -- GET ?genre=<script>  ->  422 / invalid characters"
curlie GET "http://localhost:8080/libraries/${LIB_ID}/analytics/low-stock" "genre==<script>" -H "Authorization:Bearer ${ADMIN_TOKEN}"
echo

echo "---- 14) 422 -- GET ?author=<script>  ->  422 / invalid characters"
curlie GET "http://localhost:8080/libraries/${LIB_ID}/analytics/low-stock" "author==<script>" -H "Authorization:Bearer ${ADMIN_TOKEN}"
echo

echo "---- 15) 422 -- GET ?title=<script>  ->  422 / invalid characters"
curlie GET "http://localhost:8080/libraries/${LIB_ID}/analytics/low-stock" "title==<script>" -H "Authorization:Bearer ${ADMIN_TOKEN}"
echo

echo "---- 16) 422 -- GET ?isbn=abc  ->  422 / isbn invalid"
curlie GET "http://localhost:8080/libraries/${LIB_ID}/analytics/low-stock" "isbn==abc" -H "Authorization:Bearer ${ADMIN_TOKEN}"
echo

echo "---- 17) 401 -- GET with no token  ->  401 / Authentication required"
curlie GET "http://localhost:8080/libraries/${LIB_ID}/analytics/low-stock"
echo

echo "---- 18) OK -- GET with customer token  ->  200 / same data"
curlie GET "http://localhost:8080/libraries/${LIB_ID}/analytics/low-stock" -H "Authorization:Bearer ${CUSTOMER_TOKEN}"
echo
