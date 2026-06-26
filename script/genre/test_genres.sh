#! /usr/bin/env bash
# Test script for GET /genres endpoint

echo "── 1) OK — GET /genres  →  200 / data / meta"
curlie "http://localhost:8080/genres"
echo
echo "── 2) OK — GET /genres?page=1&size=5  →  200 / meta.size=5"
curlie "http://localhost:8080/genres?page=1&size=5"
echo
echo "── 3) OK — GET /genres?search=ZZZZNOTFOUND  →  200 / data=[] / meta.total=0"
curlie "http://localhost:8080/genres?search=ZZZZNOTFOUND"
echo
echo "── 4) OK — GET /genres?search=Fantasy  →  200 / Fantasy genre"
curlie "http://localhost:8080/genres?search=Fantasy"
echo
echo "── 5) OK — GET /genres?search=Fiction  →  200 / Science Fiction, Historical Fiction, Non-Fiction"
curlie "http://localhost:8080/genres?search=Fiction"
echo
echo "── 6) OK — GET /genres?search=Mystery  →  200 / Mystery"
curlie "http://localhost:8080/genres?search=Mystery"
echo
echo "── 7) OK — GET /genres?search=Thriller  →  200 / Thriller"
curlie "http://localhost:8080/genres?search=Thriller"
echo
echo "── 8) OK — GET /genres?search=Poetry  →  200 / Poetry"
curlie "http://localhost:8080/genres?search=Poetry"
echo
echo "── 9) OK — GET /genres?search=Science  →  200 / Science Fiction"
curlie "http://localhost:8080/genres?search=Science"
echo
echo "── 10) OK — GET /genres?search=Historical  →  200 / Historical Fiction"
curlie "http://localhost:8080/genres?search=Historical"
echo
echo "── 11) OK — GET /genres?page=99&size=20  →  200 / data=[] / meta.total=10"
curlie "http://localhost:8080/genres?page=99&size=20"
echo
echo "── 12) OK — GET /genres?page=1&size=1  →  200 / exactly 1 item per page"
curlie "http://localhost:8080/genres?page=1&size=1"
echo
echo "── 13) OK — GET /genres?search=  →  200 / all genres (blank search)"
curlie "http://localhost:8080/genres?search="
echo
echo "── 14) 422 — GET /genres?search=<script>  →  422 invalid characters"
curlie "http://localhost:8080/genres" "search==<script>"
echo
echo "── 15) 422 — GET /genres?search=genre|bad  →  422 invalid characters"
curlie "http://localhost:8080/genres" "search==genre|bad"
