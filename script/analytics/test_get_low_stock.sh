#! /usr/bin/env bash
# Test script for GET /libraries/{libraryId}/analytics/low-stock/book/{bookId} endpoint
# Requires seed_analytics_low_stock.sql to be run first.
#
# Expected data (stock = arrivals - sold, default threshold=3):
#   Book1 "The Crystal Compass"  (1a1019e7-7c60-47d1-b52a-88dfff652e42)
#     PAPERBACK: 1 -> low   HARDCOVER: 3 -> low (=threshold)   POCKET: 0 -> low
#   Book2 "Quantum Horizons"     (edc27d44-b8da-4c1b-ba7c-a64e994b46ca)
#     PAPERBACK: 2 -> low   HARDCOVER: 4 -> ok                POCKET: 0 -> low
#   Book3 "The Silent Witness"   (3dbe25a8-86f5-41fd-adec-21e6626c86e2)
#     PAPERBACK: 2 -> low   HARDCOVER: 3 -> low (=threshold)  POCKET: 2 -> low

LIB_ID="0391f892-a6fc-41d0-8253-925b76bd72a0"
BOOK1_ID="1a1019e7-7c60-47d1-b52a-88dfff652e42"
BOOK2_ID="edc27d44-b8da-4c1b-ba7c-a64e994b46ca"

ADMIN_TOKEN=$(curlie POST "http://localhost:8080/auth/login" email="admin@library.com" password="Str0ng!Passphrase1" | jq -r '.token')

echo "---- 1) OK -- GET book1 (default threshold=3)  ->  200 / 3 rows (all formats low)"
curlie GET "http://localhost:8080/libraries/${LIB_ID}/analytics/low-stock/book/${BOOK1_ID}" -H "Authorization:Bearer ***"
echo

echo "---- 2) OK -- GET book2 (threshold=3)  ->  200 / 2 rows (PAPERBACK=2, POCKET=0)"
curlie GET "http://localhost:8080/libraries/${LIB_ID}/analytics/low-stock/book/${BOOK2_ID}?threshold=3" -H "Authorization:Bearer ***"
echo

echo "---- 3) OK -- GET book2 (threshold=2)  ->  200 / 1 row (only POCKET=0)"
curlie GET "http://localhost:8080/libraries/${LIB_ID}/analytics/low-stock/book/${BOOK2_ID}?threshold=2" -H "Authorization:Bearer ***"
echo

echo "---- 4) OK -- GET book1 with format filter  ->  200 / 1 row (only PAPERBACK)"
curlie GET "http://localhost:8080/libraries/${LIB_ID}/analytics/low-stock/book/${BOOK1_ID}?format=PAPERBACK" -H "Authorization:Bearer ***"
echo

echo "---- 5) OK -- GET book2 threshold=0  ->  200 / 1 row (POCKET=0)"
curlie GET "http://localhost:8080/libraries/${LIB_ID}/analytics/low-stock/book/${BOOK2_ID}?threshold=0" -H "Authorization:Bearer ***"
echo

echo "---- 6) 422 -- GET threshold=-1  ->  422"
curlie GET "http://localhost:8080/libraries/${LIB_ID}/analytics/low-stock/book/${BOOK1_ID}" "threshold==-1" -H "Authorization:Bearer ***"
echo

echo "---- 7) 422 -- GET invalid format  ->  422"
curlie GET "http://localhost:8080/libraries/${LIB_ID}/analytics/low-stock/book/${BOOK1_ID}" "format==INVALID" -H "Authorization:Bearer ***"
echo

echo "---- 8) 404 -- GET nonexistent library  ->  404"
curlie GET "http://localhost:8080/libraries/00000000-0000-0000-0000-000000000000/analytics/low-stock/book/${BOOK1_ID}" -H "Authorization:Bearer ***"
echo

echo "---- 9) 401 -- GET with no token  ->  401"
curlie GET "http://localhost:8080/libraries/${LIB_ID}/analytics/low-stock/book/${BOOK1_ID}"
echo
