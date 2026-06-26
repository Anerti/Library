#! /usr/bin/env bash
# Test script for PATCH /authors/{authorId} endpoint
# Requires seed_author_for_patch.sql to be run first.
#
# Seeded authors:
#   0336c58f-664b-40da-96d0-cac2888cd00a  —  George Orwell
#   f8a4dddb-5f19-4961-8f71-f2cc6aa8ec36  —  Jane Austen
SUCCESS_ID="0336c58f-664b-40da-96d0-cac2888cd00a"
OTHER_ID="f8a4dddb-5f19-4961-8f71-f2cc6aa8ec36"

echo "── 1) 200 — PATCH /authors/{id} (update both names)  →  200 / Eric Blair"
curlie PATCH "http://localhost:8080/authors/${SUCCESS_ID}" firstName="Eric" lastName="Blair"
echo

echo "── 2) 200 — PATCH /authors/{id} (firstName only)  →  200 / George Orwell restored"
curlie PATCH "http://localhost:8080/authors/${SUCCESS_ID}" firstName="George"
echo

echo "── 3) 200 — PATCH /authors/{id} (lastName only)  →  200 / Orwell restored"
curlie PATCH "http://localhost:8080/authors/${SUCCESS_ID}" lastName="Orwell"
echo

echo "── 4) 200 — PATCH /authors/{id} (hyphenated first name)  →  200 / Jean-Claude Orwell"
curlie PATCH "http://localhost:8080/authors/${SUCCESS_ID}" firstName="Jean-Claude"
echo

echo "── 5) 404 — PATCH /authors/{id} (non-existent UUID)  →  404 / not found"
curlie PATCH "http://localhost:8080/authors/00000000-0000-0000-0000-000000000000" firstName="Ghost"
echo

echo "── 6) 400 — PATCH /authors/{id} (invalid UUID)  →  400 / bad request"
curlie PATCH "http://localhost:8080/authors/not-a-uuid" firstName="Irrelevant"
echo

echo "── 7) 400 — PATCH /authors/{id} (no body)  →  400 / missing body"
curlie PATCH "http://localhost:8080/authors/${SUCCESS_ID}"
echo

echo "── 8) 422 — PATCH /authors/{id} (both names null — empty object)  →  422 / required"
echo '{}' | curlie PATCH "http://localhost:8080/authors/${SUCCESS_ID}"
echo

echo "── 9) 422 — PATCH /authors/{id} (firstName blank)  →  422 / required and cannot be blank"
curlie PATCH "http://localhost:8080/authors/${SUCCESS_ID}" firstName="" lastName="Orwell"
echo

echo "── 10) 422 — PATCH /authors/{id} (lastName blank)  →  422 / required and cannot be blank"
curlie PATCH "http://localhost:8080/authors/${OTHER_ID}" firstName="Jane" lastName=""
echo

echo "── 11) 422 — PATCH /authors/{id} (firstName with digits)  →  422 / forbidden characters"
curlie PATCH "http://localhost:8080/authors/${SUCCESS_ID}" firstName="George2" lastName="Orwell"
echo

echo "── 12) 422 — PATCH /authors/{id} (lastName with underscore)  →  422 / forbidden characters"
curlie PATCH "http://localhost:8080/authors/${SUCCESS_ID}" firstName="George" lastName="Or_well"
echo

echo "── 13) 422 — PATCH /authors/{id} (firstName too long)  →  422 / longer than 100 chars"
curlie PATCH "http://localhost:8080/authors/${SUCCESS_ID}" firstName="ThisFirstNameIsDefinitelyWayTooRidiculouslyLongForAnAuthorBecauseTheMaximumAllowedLengthIsOneHundredCharactersAndThisExceedsThat" lastName="Orwell"
echo

echo "── 14) 422 — PATCH /authors/{id} (lastName too long)  →  422 / longer than 100 chars"
curlie PATCH "http://localhost:8080/authors/${SUCCESS_ID}" firstName="George" lastName="ThisLastNameIsDefinitelyWayTooRidiculouslyLongForAnAuthorBecauseTheMaximumAllowedLengthIsOneHundredCharactersAndThisExceedsThat"
echo

echo "── 15) 409 — PATCH /authors/{id} (name already exists)  →  409 / already exists"
# First create an author to collide with
curlie POST "http://localhost:8080/authors" firstName="George" lastName="Orwell"
echo
# Then patch Jane Austen to the same name → conflict
curlie PATCH "http://localhost:8080/authors/${OTHER_ID}" firstName="George" lastName="Orwell"
echo
