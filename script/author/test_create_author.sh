#! /usr/bin/env bash
# Test script for POST /authors endpoint
# Requires admin JWT (Bearer token obtained from POST /auth/login)

ADMIN_TOKEN=$(curlie POST "http://localhost:8080/auth/login" email="admin@library.com" password="Str0ng!Passphrase1" | jq -r '.token')
CUSTOMER_TOKEN=$(curlie POST "http://localhost:8080/auth/login" email="marie@mail.com" password="Str0ng!Passphrase1" | jq -r '.token')

# Test 1 + 2 + 3: create → duplicate (self-contained conflict)
echo "── 1) 201 — POST /authors (valid first + last)  →  201 / Jean Dupont"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" POST "http://localhost:8080/authors" firstName="Jean" lastName="Dupont"
echo

echo "── 2) 201 — POST /authors (another valid)  →  201 / Jean-Christophe"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" POST "http://localhost:8080/authors" firstName="Jean-Christophe" lastName="Moreau"
echo

echo "── 3) 409 — POST /authors (duplicate of test 1)  →  409 / already exists"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" POST "http://localhost:8080/authors" firstName="Jean" lastName="Dupont"
echo

echo "── 4) 422 — POST /authors (lastName with digits)  →  422 / forbidden characters"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" POST "http://localhost:8080/authors" firstName="Albert" lastName="Dupont2"
echo

echo "── 5) 422 — POST /authors (firstName with digits)  →  422 / forbidden characters"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" POST "http://localhost:8080/authors" firstName="Albert3" lastName="Camus"
echo

echo "── 6) 422 — POST /authors (lastName with underscore)  →  422 / forbidden characters"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" POST "http://localhost:8080/authors" firstName="Albert" lastName="Du_pont"
echo

echo "── 7) 422 — POST /authors (lastName too long)  →  422 / longer than 100 chars"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" POST "http://localhost:8080/authors" firstName="Albert" lastName="ThisLastNameIsDefinitelyWayTooRidiculouslyLongForAnAuthorBecauseTheMaximumAllowedLengthIsOneHundredCharactersAndThisExceedsThat"
echo

echo "── 8) 422 — POST /authors (firstName too long)  →  422 / longer than 100 chars"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" POST "http://localhost:8080/authors" firstName="ThisFirstNameIsDefinitelyWayTooRidiculouslyLongForAnAuthorBecauseTheMaximumAllowedLengthIsOneHundredCharactersAndThisExceedsThat" lastName="Camus"
echo

echo "── 9) 422 — POST /authors (firstName blank)  →  422 / required and cannot be blank"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" POST "http://localhost:8080/authors" firstName="" lastName="Camus"
echo

echo "── 10) 422 — POST /authors (lastName blank)  →  422 / required and cannot be blank"
curlie -H "Authorization:Bearer $ADMIN_TOKEN" POST "http://localhost:8080/authors" firstName="Albert" lastName=""
echo

echo "── 11) 403 — POST /authors (customer token)  →  403 / forbidden"
curlie -H "Authorization:Bearer ***" POST "http://localhost:8080/authors" firstName="Customer" lastName="Attempt"
echo

echo "── 12) 401 — POST /authors (no token)  →  401 / unauthorized"
curlie POST "http://localhost:8080/authors" firstName="No" lastName="Token"
echo
