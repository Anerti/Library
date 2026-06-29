#! /usr/bin/env bash
# Test script for PATCH /books/{bookId} endpoint
# Requires seed_book_for_patch.sql to be run first.
# Requires admin JWT (Bearer token obtained from POST /auth/login)
#
# Seeded books:
#   2ed55e73-fdf3-4879-a333-f38233fb9a88  —  Vingt mille lieues sous les mers
#   5d4962d3-f460-4268-a6f3-27459f6bf8bf  —  Un barrage contre le Pacifique
SUCCESS_ID="2ed55e73-fdf3-4879-a333-f38233fb9a88"
OTHER_ID="5d4962d3-f460-4268-a6f3-27459f6bf8bf"

ADMIN_TOKEN=$(curlie POST "http://localhost:8080/auth/login" email="admin@library.com" password="Str0ng!Passphrase1" | jq -r '.token')

echo "── 1) 200 — PATCH /books/{id} (update all fields)  →  200 / Voyage au bout de la nuit"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" PATCH "http://localhost:8080/books/${SUCCESS_ID}" title="Voyage au bout de la nuit" summary="Le chef-d oeuvre de Louis-Ferdinand Céline" isbn="978-2-07-036002-4" publisher="Denoel" publishedAt="1932-05-19"
echo

echo "── 2) 200 — PATCH /books/{id} (title only)  →  200 / Voyage au bout de la nuit"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" PATCH "http://localhost:8080/books/${SUCCESS_ID}" title="Le Docteur Semmelweis"
echo

echo "── 3) 200 — PATCH /books/{id} (title restored)  →  200 / Voyage au bout de la nuit"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" PATCH "http://localhost:8080/books/${SUCCESS_ID}" title="Voyage au bout de la nuit"
echo

echo "── 4) 200 — PATCH /books/{id} (summary only)  →  200"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" PATCH "http://localhost:8080/books/${SUCCESS_ID}" summary="Nouveau resume mis a jour"
echo

echo "── 5) 200 — PATCH /books/{id} (isbn only)  →  200"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" PATCH "http://localhost:8080/books/${SUCCESS_ID}" isbn="978-2-253-09864-3"
echo

echo "── 6) 200 — PATCH /books/{id} (publisher only)  →  200"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" PATCH "http://localhost:8080/books/${SUCCESS_ID}" publisher="Hachette"
echo

echo "── 7) 200 — PATCH /books/{id} (publishedAt only)  →  200"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" PATCH "http://localhost:8080/books/${SUCCESS_ID}" publishedAt="1950-01-01"
echo

echo "── 8) 200 — PATCH /books/{id} (hyphenated title)  →  200 / Saint-Exupéry"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" PATCH "http://localhost:8080/books/${SUCCESS_ID}" title="Vol de nuit"
echo

echo "── 9) 404 — PATCH /books/{id} (non-existent UUID)  →  404 / not found"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" PATCH "http://localhost:8080/books/00000000-0000-0000-0000-000000000000" title="Ghost"
echo

echo "── 10) 400 — PATCH /books/{id} (invalid UUID)  →  400 / bad request"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" PATCH "http://localhost:8080/books/not-a-uuid" title="Irrelevant"
echo

echo "── 11) 400 — PATCH /books/{id} (no body)  →  400 / missing body"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" PATCH "http://localhost:8080/books/${SUCCESS_ID}"
echo

echo "── 12) 422 — PATCH /books/{id} (empty object — all fields null)  →  422 / at least one field"
echo '{}' | curlie PATCH "http://localhost:8080/books/${SUCCESS_ID}" -H "Authorization:Bearer $ADMIN_TOKEN"
echo

echo "── 13) 422 — PATCH /books/{id} (title with digits)  →  422 / forbidden characters"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" PATCH "http://localhost:8080/books/${SUCCESS_ID}" title="Harry Potter @ 3"
echo

echo "── 14) 422 — PATCH /books/{id} (title too long)  →  422 / cannot be longer than 100"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" PATCH "http://localhost:8080/books/${SUCCESS_ID}" title="ThisTitleIsDefinitelyWayTooRidiculouslyLongForABookBecauseTheMaximumAllowedLengthIsOneHundredCharactersAndThisExceedsThatX"
echo

echo "── 15) 422 — PATCH /books/{id} (summary too long)  →  422 / cannot be longer than 1000"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" PATCH "http://localhost:8080/books/${SUCCESS_ID}" summary="$(printf 'a%.0s' {1..1001})"
echo

echo "── 16) 422 — PATCH /books/{id} (isbn too short)  →  422 / isbn is invalid"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" PATCH "http://localhost:8080/books/${SUCCESS_ID}" isbn="123456789"
echo

echo "── 17) 422 — PATCH /books/{id} (isbn with letters)  →  422 / isbn is invalid"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" PATCH "http://localhost:8080/books/${SUCCESS_ID}" isbn="abcdefghij"
echo

echo "── 18) 422 — PATCH /books/{id} (publisher with digits)  →  422 / forbidden characters"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" PATCH "http://localhost:8080/books/${SUCCESS_ID}" publisher="Pub 123"
echo

echo "── 19) 422 — PATCH /books/{id} (publisher too long)  →  422 / cannot be longer than 100"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" PATCH "http://localhost:8080/books/${SUCCESS_ID}" publisher="ThisPublisherNameIsDefinitelyWayTooRidiculouslyLongBecauseTheMaximumAllowedLengthIsOneHundredCharactersAndThisExceedsThat"
echo

echo "── 20) 409 — PATCH /books/{id} (isbn already exists on other book)  →  409 / already exists"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" PATCH "http://localhost:8080/books/${SUCCESS_ID}" isbn="978-2-07-036780-1"
echo
