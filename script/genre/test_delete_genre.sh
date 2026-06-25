#! /usr/bin/env bash
# Test script for DELETE /genres/{genreId} endpoint
# Requires: seed_genre_for_delete.sql to be run first to create the genre.

echo "── 1) 204 — DELETE /genres/{uuid} (existing genre)  →  204 / no content"
curlie DELETE "http://localhost:8080/genres/6d6a30cb-bb49-48a1-9a46-81354d600eac"
echo

echo "── 2) 400 — DELETE /genres/{invalid} (malformed UUID)  →  400 / bad request"
curlie DELETE "http://localhost:8080/genres/not-a-uuid"
echo

echo "── 3) 404 — DELETE /genres/{uuid} (already deleted)  →  404 / not found"
curlie DELETE "http://localhost:8080/genres/6d6a30cb-bb49-48a1-9a46-81354d600eac"
echo
