#! /usr/bin/env bash
# Test script for GET /libraries/{libraryId} endpoint
# Requires seed-libraries.sql to be run first.

LIB_ID_1="3bc243ec-7250-4266-b9cb-c9f45f3ef5e1"
LIB_ID_2="dd60c8ef-5835-4796-9f81-800407374506"
LIB_ID_3="e082e042-69e9-4ece-a0e0-f782fb117aac"

echo "── 1) OK — GET /libraries/{libraryId}  →  200 / Librairie Générale"
curlie "http://localhost:8080/libraries/${LIB_ID_1}"
echo

echo "── 2) OK — GET /libraries/{libraryId}  →  200 / Librairie Technique"
curlie "http://localhost:8080/libraries/${LIB_ID_2}"
echo

echo "── 3) OK — GET /libraries/{libraryId}  →  200 / Librairie Jeunesse"
curlie "http://localhost:8080/libraries/${LIB_ID_3}"
echo

echo "── 4) 404 — GET /libraries/{libraryId} with non-existent UUID  →  404"
curlie "http://localhost:8080/libraries/00000000-0000-0000-0000-000000000000"
echo

echo "── 5) 404 — GET /libraries/{libraryId} with random UUID  →  404"
curlie "http://localhost:8080/libraries/a1111111-b222-4333-c444-e555555555555"
echo

echo "── 6) 400 — GET /libraries/{libraryId} with invalid UUID format  →  400"
curlie "http://localhost:8080/libraries/not-a-uuid"
echo

echo "── 7) 400 — GET /libraries/{libraryId} with empty string  →  400"
curlie "http://localhost:8080/libraries/"
echo
