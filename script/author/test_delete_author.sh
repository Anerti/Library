#! /usr/bin/env bash
# Test script for DELETE /authors/{authorId} endpoint
# Requires seed_author_for_delete.sql to be run first.
#
# Seeded author:
#   f2ad8c09-b044-4519-b9cc-471c23a594e1  —  Delete Me

AUTHOR_ID="f2ad8c09-b044-4519-b9cc-471c23a594e1"

echo "── 1) 204 — DELETE /authors/{uuid} (existing author)  →  204 / no content"
curlie DELETE "http://localhost:8080/authors/${AUTHOR_ID}"
echo

echo "── 2) 400 — DELETE /authors/{invalid} (malformed UUID)  →  400 / bad request"
curlie DELETE "http://localhost:8080/authors/not-a-uuid"
echo

echo "── 3) 404 — DELETE /authors/{uuid} (already deleted)  →  404 / not found"
curlie DELETE "http://localhost:8080/authors/${AUTHOR_ID}"
echo
