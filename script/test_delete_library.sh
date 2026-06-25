#! /usr/bin/env bash
# Test script for DELETE /libraries/{libraryId} endpoint
# Requires: seed_library_to_delete.sql to be run first to create the library.

echo "── 1) 204 — DELETE /libraries/{uuid} (existing library)  →  204 / no content"
curlie DELETE "http://localhost:8080/libraries/51d81748-207c-4a67-b3bc-9434634bbad5"
echo

echo "── 2) 400 — DELETE /libraries/{invalid} (malformed UUID)  →  400 / bad request"
curlie DELETE "http://localhost:8080/libraries/not-a-uuid"
echo

echo "── 3) 404 — DELETE /libraries/{uuid} (non-existent UUID)  →  404 / not found"
curlie DELETE "http://localhost:8080/libraries/51d81748-207c-4a67-b3bc-9434634bbad5"
echo
