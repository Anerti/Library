#! /usr/bin/env bash
# Test script for GET /authors endpoint

echo "── 01) OK — GET /authors  →  200 / data / meta"
curlie "http://localhost:8080/authors"
echo
echo "── 02) OK — GET /authors?page=1&size=5  →  200 / 5 items per page"
curlie "http://localhost:8080/authors?page=1&size=5"
echo
echo "── 03) OK — GET /authors?page=2&size=20  →  200 / 2 items (22 total, page 2)"
curlie "http://localhost:8080/authors?page=2&size=20"
echo
echo "── 04) OK — GET /authors?search=Albert  →  200 / Albert Camus"
curlie "http://localhost:8080/authors?search=Albert"
echo
echo "── 05) OK — GET /authors?search=Camus  →  200 / Albert Camus"
curlie "http://localhost:8080/authors?search=Camus"
echo
echo "── 06) OK — GET /authors?search=Jean  →  200 / multiple authors (Jean-Paul, Jean, Jean-Baptiste)"
curlie "http://localhost:8080/authors?search=Jean"
echo
echo "── 07) OK — GET /authors?search=de  →  200 / de Saint-Exupéry, de Balzac, de Beauvoir"
curlie "http://localhost:8080/authors?search=de"
echo
echo "── 08) OK — GET /authors?search=Verne  →  200 / Jules Verne"
curlie "http://localhost:8080/authors?search=Verne"
echo
echo "── 09) OK — GET /authors?search=ZZZZNOTFOUND  →  200 / data=[] / meta.total=0"
curlie "http://localhost:8080/authors?search=ZZZZNOTFOUND"
echo
echo "── 10) OK — GET /authors?search=  →  200 / all authors (blank search)"
curlie "http://localhost:8080/authors?search="
echo
echo "── 11) OK — GET /authors?page=99&size=20  →  200 / data=[] / meta.total=22"
curlie "http://localhost:8080/authors?page=99&size=20"
echo
echo "── 12) OK — GET /authors?page=1&size=1  →  200 / exactly 1 item"
curlie "http://localhost:8080/authors?page=1&size=1"
echo
echo "── 13) OK — GET /authors?search=Jean-Paul  →  200 / Jean-Paul Sartre (hyphen in search)"
curlie "http://localhost:8080/authors?search=Jean-Paul"
echo
echo "── 14) 422 — GET /authors?search=<script>  →  422 invalid characters"
curlie "http://localhost:8080/authors" "search==<script>"
echo
echo "── 15) 422 — GET /authors?search=author|bad  →  422 invalid characters"
curlie "http://localhost:8080/authors" "search==author|bad"
echo
echo "── 16) 422 — GET /authors?search=test123  →  422 invalid characters (digits)"
curlie "http://localhost:8080/authors" "search==test123"
echo
echo "── 17) OK — GET /authors?search=Sagan  →  200 / Françoise Sagan (accented name)"
curlie "http://localhost:8080/authors?search=Sagan"
