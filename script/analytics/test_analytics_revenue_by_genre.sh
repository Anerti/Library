#! /usr/bin/env bash
# Test script for GET /libraries/{libraryId}/analytics/revenue/by-genre endpoint
# Requires seed_analytics_revenue_by_genre.sql to be run first.
#
# Expected revenue by genre (all sales):
#   Fantasy      → totalRevenue=$25.98, totalSold=2
#   Science Fiction → totalRevenue=$14.99, totalSold=1
#   Mystery      → totalRevenue=$11.99, totalSold=1
# With date filter [2026-01-01, 2026-01-31]:
#   Fantasy      → totalRevenue=$12.99, totalSold=1
#   Science Fiction → totalRevenue=$14.99, totalSold=1

LIB_ID="d86fc243-e0a7-4112-974e-f00e06efcd2f"

ADMIN_TOKEN=$(curlie POST "http://localhost:8080/auth/login" email="admin@library.com" password="Str0ng!Passphrase1" | jq -r '.token')

echo "---- 1) OK -- GET default params (desc, page=1, size=20)  ->  200 / 3 genres, Fantasy first"
curlie GET "http://localhost:8080/libraries/${LIB_ID}/analytics/revenue/by-genre" -H "Authorization:Bearer ***"
echo

echo "---- 2) OK -- GET with date filter  ->  200 / 2 genres (Fantasy, Science Fiction)"
curlie GET "http://localhost:8080/libraries/${LIB_ID}/analytics/revenue/by-genre?from=2026-01-01T00:00:00Z&to=2026-01-31T23:59:59Z" -H "Authorization:Bearer ***"
echo

echo "---- 3) OK -- GET sortOrder=asc  ->  200 / 3 genres, Mystery first"
curlie GET "http://localhost:8080/libraries/${LIB_ID}/analytics/revenue/by-genre?sortOrder=asc" -H "Authorization:Bearer ***"
echo

echo "---- 4) OK -- GET with all params  ->  200 / filtered + paginated"
curlie GET "http://localhost:8080/libraries/${LIB_ID}/analytics/revenue/by-genre?from=2026-01-01T00:00:00Z&to=2026-12-31T23:59:59Z&sortOrder=desc&page=1&size=2" -H "Authorization:Bearer ***"
echo

echo "---- 5) OK -- GET date range with no sales  ->  200 / data=null"
curlie GET "http://localhost:8080/libraries/${LIB_ID}/analytics/revenue/by-genre?from=2025-01-01T00:00:00Z&to=2025-12-31T23:59:59Z" -H "Authorization:Bearer ***"
echo

echo "---- 6) OK -- GET page 2 (size=2)  ->  200 / 1 genre (Mystery)"
curlie GET "http://localhost:8080/libraries/${LIB_ID}/analytics/revenue/by-genre?page=2&size=2" -H "Authorization:Bearer ***"
echo

echo "---- 7) 422 -- GET invalid sort order  ->  422"
curlie GET "http://localhost:8080/libraries/${LIB_ID}/analytics/revenue/by-genre?sortOrder=invalid" -H "Authorization:Bearer ***"
echo

echo "---- 8) 422 -- GET end before start date  ->  422"
curlie GET "http://localhost:8080/libraries/${LIB_ID}/analytics/revenue/by-genre?from=2026-06-01T00:00:00Z&to=2026-01-01T00:00:00Z" -H "Authorization:Bearer ***"
echo

echo "---- 9) 400 -- GET invalid library UUID  ->  400"
curlie GET "http://localhost:8080/libraries/not-a-uuid/analytics/revenue/by-genre" -H "Authorization:Bearer ***"
echo

echo "---- 10) 401 -- GET with no token  ->  401"
curlie GET "http://localhost:8080/libraries/${LIB_ID}/analytics/revenue/by-genre"
echo

echo "---- 11) 403 -- GET with customer token  ->  403"
curlie GET "http://localhost:8080/libraries/${LIB_ID}/analytics/revenue/by-genre" -H "Authorization:Bearer ***"
echo
