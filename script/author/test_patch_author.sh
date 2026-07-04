#! /usr/bin/env bash
# Test script for PATCH /authors/{authorId} endpoint
# Requires seed_author_for_patch.sql to be run first.
# Requires admin JWT (Bearer token obtained from POST /auth/login)
#
# Seeded authors:
#   0336c58f-664b-40da-96d0-cac2888cd00a  —  George Orwell
#   f8a4dddb-5f19-4961-8f71-f2cc6aa8ec36  —  Jane Austen
SUCCESS_ID="0336c58f-664b-40da-96d0-cac2888cd00a"
OTHER_ID="f8a4dddb-5f19-4961-8f71-f2cc6aa8ec36"

ADMIN_TOKEN=$(curlie POST "http://localhost:8080/auth/login" email="admin@library.com" password="Str0ng!Passphrase1" | jq -r '.token')
CUSTOMER_TOKEN=$(curlie POST "http://localhost:8080/auth/login" email="marie@mail.com" password="Str0ng!Passphrase1" | jq -r '.token')

echo "── 1) 200 — PATCH /authors/{id} (update both names)  →  200 / Eric Blair"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" PATCH "http://localhost:8080/authors/${SUCCESS_ID}" firstName="Eric" lastName="Blair"
echo

echo "── 2) 200 — PATCH /authors/{id} (firstName only)  →  200 / George Orwell restored"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" PATCH "http://localhost:8080/authors/${SUCCESS_ID}" firstName="George"
echo

echo "── 3) 200 — PATCH /authors/{id} (lastName only)  →  200 / Orwell restored"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" PATCH "http://localhost:8080/authors/${SUCCESS_ID}" lastName="Orwell"
echo

echo "── 4) 200 — PATCH /authors/{id} (hyphenated first name)  →  200 / Jean-Claude Orwell"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" PATCH "http://localhost:8080/authors/${SUCCESS_ID}" firstName="Jean-Claude"
echo

echo "── 5) 404 — PATCH /authors/{id} (non-existent UUID)  →  404 / not found"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" PATCH "http://localhost:8080/authors/00000000-0000-0000-0000-000000000000" firstName="Ghost"
echo

echo "── 6) 400 — PATCH /authors/{id} (invalid UUID)  →  400 / bad request"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" PATCH "http://localhost:8080/authors/not-a-uuid" firstName="Irrelevant"
echo

echo "── 7) 400 — PATCH /authors/{id} (no body)  →  400 / missing body"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" PATCH "http://localhost:8080/authors/${SUCCESS_ID}"
echo

echo "── 8) 422 — PATCH /authors/{id} (both names null — empty object)  →  422 / required"
echo '{}' | curlie PATCH "http://localhost:8080/authors/${SUCCESS_ID}" -H "Authorization:Bearer $ADMIN_TOKEN"
echo

echo "── 9) 422 — PATCH /authors/{id} (firstName blank)  →  422 / required and cannot be blank"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" PATCH "http://localhost:8080/authors/${SUCCESS_ID}" firstName="" lastName="Orwell"
echo

echo "── 10) 422 — PATCH /authors/{id} (lastName blank)  →  422 / required and cannot be blank"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" PATCH "http://localhost:8080/authors/${OTHER_ID}" firstName="Jane" lastName=""
echo

echo "── 11) 422 — PATCH /authors/{id} (firstName with digits)  →  422 / forbidden characters"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" PATCH "http://localhost:8080/authors/${SUCCESS_ID}" firstName="George2" lastName="Orwell"
echo

echo "── 12) 422 — PATCH /authors/{id} (lastName with underscore)  →  422 / forbidden characters"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" PATCH "http://localhost:8080/authors/${SUCCESS_ID}" firstName="George" lastName="Or_well"
echo

echo "── 13) 422 — PATCH /authors/{id} (firstName too long)  →  422 / longer than 100 chars"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" PATCH "http://localhost:8080/authors/${SUCCESS_ID}" firstName="ThisFirstNameIsDefinitelyWayTooRidiculouslyLongForAnAuthorBecauseTheMaximumAllowedLengthIsOneHundredCharactersAndThisExceedsThat" lastName="Orwell"
echo

echo "── 14) 422 — PATCH /authors/{id} (lastName too long)  →  422 / longer than 100 chars"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" PATCH "http://localhost:8080/authors/${SUCCESS_ID}" firstName="George" lastName="ThisLastNameIsDefinitelyWayTooRidiculouslyLongForAnAuthorBecauseTheMaximumAllowedLengthIsOneHundredCharactersAndThisExceedsThat"
echo

echo "── 15) 409 — PATCH /authors/{id} (name already exists)  →  409 / already exists"
# First create an author to collide with
curlie -H "Authorization:Bearer $ADMIN_TOKEN" POST "http://localhost:8080/authors" firstName="George" lastName="Orwell"
echo
# Then patch Jane Austen to the same name → conflict
curlie -H "Authorization:Bearer $ADMIN_TOKEN" PATCH "http://localhost:8080/authors/${OTHER_ID}" firstName="George" lastName="Orwell"
echo

echo "── 16) 403 — PATCH /authors/{id} (customer token)  →  403 / forbidden"
curlie -H "Authorization:Bearer $CUSTOMER_TOKEN" PATCH "http://localhost:8080/authors/${SUCCESS_ID}" firstName="Hacker" lastName="Attempt"
echo
