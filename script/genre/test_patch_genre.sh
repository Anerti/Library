#! /usr/bin/env bash
# Test script for PATCH /genres/{genreId} endpoint
# Requires: seed_genre_for_patch_conflict.sql to be run first to create the genres.
#
# Seeded genres:
#   c5c659bb-5670-4409-8e4d-b02678ac8e72  —  ExistingGenreForConflict
#   0be3aa75-2757-4eae-941c-081abbdc32c5  —  GenreForPatchSuccess
CONFLICT_ID="c5c659bb-5670-4409-8e4d-b02678ac8e72"
SUCCESS_ID="0be3aa75-2757-4eae-941c-081abbdc32c5"

echo "── 1) 201 — POST /genres (create conflict target)  →  201 / PatchConflictTarget"
curlie POST "http://localhost:8080/genres" name="PatchConflictTarget"
echo

echo "── 2) 409 — PATCH /genres/{id} (name already exists)  →  409 / already exists"
curlie PATCH "http://localhost:8080/genres/${CONFLICT_ID}" name="PatchConflictTarget"
echo

echo "── 3) 200 — PATCH /genres/{id} (unique name)  →  200 / UpdatedSuccessName"
curlie PATCH "http://localhost:8080/genres/${SUCCESS_ID}" name="UpdatedSuccessName"
echo

echo "── 4) 404 — PATCH /genres/{id} (non-existent UUID)  →  404 / not found"
curlie PATCH "http://localhost:8080/genres/00000000-0000-0000-0000-000000000000" name="Ghost"
echo

echo "── 5) 400 — PATCH /genres/{id} (invalid UUID)  →  400 / bad request"
curlie PATCH "http://localhost:8080/genres/not-a-uuid" name="Irrelevant"
echo

echo "── 6) 422 — PATCH /genres/{id} (blank name)  →  422 / name is required"
curlie PATCH "http://localhost:8080/genres/${SUCCESS_ID}" name=""
echo

echo "── 7) 422 — PATCH /genres/{id} (name with digits)  →  422 / forbidden characters"
curlie PATCH "http://localhost:8080/genres/${SUCCESS_ID}" name="Genre1"
echo

echo "── 8) 422 — PATCH /genres/{id} (name with underscore)  →  422 / forbidden characters"
curlie PATCH "http://localhost:8080/genres/${SUCCESS_ID}" name="Fan_tasy"
echo

echo "── 9) 422 — PATCH /genres/{id} (name too long)  →  422 / cannot be longer than 100"
curlie PATCH "http://localhost:8080/genres/${SUCCESS_ID}" name="This name is definitely way too ridiculously long for a genre because the maximum allowed length is one hundred characters"
echo
