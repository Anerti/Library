#! /usr/bin/env bash
# Test script for POST /books endpoint
# Requires admin JWT (Bearer token obtained from POST /auth/login)

ADMIN_TOKEN=$(curlie POST "http://localhost:8080/auth/login" email="admin@library.com" password="Str0ng!Passphrase1" | jq -r '.token')

# Test 1 + 3: create → duplicate (self-contained conflict)
echo "── 1) 201 — POST /books (all valid)  →  201 / Madame Bovary"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" POST "http://localhost:8080/books" title="Madame Bovary" summary="Un classique du roman realiste français" isbn="978-2-07-061275-8" publisher="Gallimard" publishedAt="1857-04-01"
echo

echo "── 2) 201 — POST /books (another valid)  →  201 / L'Etranger"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" POST "http://localhost:8080/books" title="L'Etranger" summary="Un roman existentialiste d Albert Camus" isbn="978-2-07-036002-4" publisher="Gallimard" publishedAt="1942-06-15"
echo

echo "── 3) 409 — POST /books (duplicate ISBN from test 1)  →  409 / already exists"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" POST "http://localhost:8080/books" title="Madame Bovary" summary="Un classique du roman realiste français" isbn="978-2-07-061275-8" publisher="Gallimard" publishedAt="1857-04-01"
echo

echo "── 4) 422 — POST /books (title blank)  →  422 / title is required and cannot be blank"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" POST "http://localhost:8080/books" title="" summary="Sans titre" isbn="0123456789" publisher="Gallimard" publishedAt="2020-01-01"
echo

echo "── 5) 422 — POST /books (title too long)  →  422 / cannot be longer than 100"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" POST "http://localhost:8080/books" title="ThisTitleIsDefinitelyWayTooRidiculouslyLongForABookBecauseTheMaximumAllowedLengthIsOneHundredCharactersAndThisExceedsThatX" isbn="0123456789" publisher="Gallimard" publishedAt="2020-01-01"
echo

echo "── 6) 422 — POST /books (title with forbidden characters)  →  422 / title contains invalid characters"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" POST "http://localhost:8080/books" title="Harry Potter @ 3" isbn="0123456789" publisher="Bloomsbury" publishedAt="1997-06-26"
echo

echo "── 7) 422 — POST /books (summary too long)  →  422 / cannot be longer than 1000"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" POST "http://localhost:8080/books" title="Long Summary Book" summary="$(printf 'a%.0s' {1..1001})" isbn="0123456789" publisher="Gallimard" publishedAt="2020-01-01"
echo

echo "── 8) 422 — POST /books (summary with forbidden characters)  →  422 / contains invalid characters"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" POST "http://localhost:8080/books" title="Bad Summary" summary="Résumé avec des caractères interdits < > /" isbn="0123456789" publisher="Gallimard" publishedAt="2020-01-01"
echo

echo "── 9) 422 — POST /books (isbn blank)  →  422 / isbn is required and cannot be blank"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" POST "http://localhost:8080/books" title="No ISBN" summary="Sans ISBN" isbn="" publisher="Gallimard" publishedAt="2020-01-01"
echo

echo "── 10) 422 — POST /books (isbn too short)  →  422 / isbn is invalid or contain Illegal characters"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" POST "http://localhost:8080/books" title="Bad ISBN" summary="ISBN trop court" isbn="123456789" publisher="Gallimard" publishedAt="2020-01-01"
echo

echo "── 11) 422 — POST /books (isbn with letters)  →  422 / isbn is invalid or contain Illegal characters"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" POST "http://localhost:8080/books" title="Bad ISBN" summary="ISBN invalide" isbn="abcdefghij" publisher="Gallimard" publishedAt="2020-01-01"
echo

echo "── 12) 422 — POST /books (publisher blank)  →  422 / publisher is required and cannot be blank"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" POST "http://localhost:8080/books" title="No Publisher" summary="Sans editeur" isbn="0123456789" publisher="" publishedAt="2020-01-01"
echo

echo "── 13) 422 — POST /books (publisher too long)  →  422 / cannot be longer than 100"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" POST "http://localhost:8080/books" title="Long Publisher" summary="Editeur trop long" isbn="0123456789" publisher="ThisPublisherNameIsDefinitelyWayTooRidiculouslyLongBecauseTheMaximumAllowedLengthIsOneHundredCharactersAndThisExceedsThat" publishedAt="2020-01-01"
echo

echo "── 14) 422 — POST /books (publisher with digits)  →  422 / forbidden characters"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" POST "http://localhost:8080/books" title="Bad Publisher" summary="Editeur avec chiffres" isbn="0123456789" publisher="Pub 123" publishedAt="2020-01-01"
echo

echo "── 15) 422 — POST /books (publishedAt blank)  →  422 / publishedAt is required and cannot be blank"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" POST "http://localhost:8080/books" title="No Date" summary="Sans date" isbn="0123456789" publisher="Gallimard" publishedAt=""
echo