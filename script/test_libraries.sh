#! /usr/bin/env bash
# ============================================================================
# Test script for GET /libraries endpoint
#
# Usage:
#   export BASE_URL="http://localhost:8080/libraries"
#   ./script/test_libraries.sh
#
# Requirements: curlie
# ============================================================================

set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080/libraries}"

echo "═══ Testing GET ${BASE_URL} ═══"
echo

# --- Basic / defaults ---
echo "── 1) OK — list all libraries (default pagination) ──"
echo "GET ${BASE_URL}  →  200 + data array + meta"
curlie -- "$BASE_URL"

echo
echo "── 2) OK — custom page=1, size=5 ──"
echo "GET ${BASE_URL}?page=1&size=5  →  200 + meta.size=5"
curlie -- "${BASE_URL}?page=1&size=5"

echo
echo "── 3) OK — empty result (no match) ──"
echo "GET ${BASE_URL}?search=ZZZZNOTFOUND  →  200 + data=[] + meta.total=0"
curlie -- "${BASE_URL}?search=ZZZZNOTFOUND"

# --- Search by phone ---
echo
echo "── 4) OK — search by phone prefix (+261) ──"
echo "GET ${BASE_URL}?search=+261  →  200 + libraries with +261"
curlie -- "${BASE_URL}?search=%2B261"

echo
echo "── 5) OK — search by specific indicatif (+261 34) ──"
echo "GET ${BASE_URL}?search=+261+34  →  200 + libraries with +261 34"
curlie -- "${BASE_URL}?search=%2B261%2034"

echo
echo "── 6) OK — search by full phone number ──"
echo "GET ${BASE_URL}?search=%2B261%2033%2098%20765%2043  →  200 + Librairie Générale"
curlie -- "${BASE_URL}?search=%2B261%2033%2098%20765%2043"

# --- Search by name ---
echo
echo "── 7) OK — search by name ──"
echo "GET ${BASE_URL}?search=Tech  →  200 + Tech Library"
curlie -- "${BASE_URL}?search=Tech"

echo
echo "── 8) OK — search with accents ──"
echo "GET ${BASE_URL}?search=Médiathèque  →  200 + Médiathèque Centrale"
curlie -- "${BASE_URL}?search=M%C3%A9diath%C3%A8que"

echo
echo "── 9) OK — search with apostrophe (St. Martin's) ──"
echo "GET ${BASE_URL}?search=St.+Martin%27s  →  200 + St. Martin's Library"
curlie -- "${BASE_URL}?search=St.%20Martin%27s"

# --- Search by email ---
echo
echo "── 10) OK — search by email domain ──"
echo "GET ${BASE_URL}?search=@library.com  →  200 + tech@library.com"
curlie -- "${BASE_URL}?search=%40library.com"

echo
echo "── 11) OK — search by email local-part ──"
echo "GET ${BASE_URL}?search=info@  →  200 + Librairie Générale"
curlie -- "${BASE_URL}?search=info%40"

# --- Search by address ---
echo
echo "── 12) OK — search by address ──"
echo "GET ${BASE_URL}?search=Campus  →  200 + libraries with Campus in address"
curlie -- "${BASE_URL}?search=Campus"

echo
echo "── 13) OK — search by full address ──"
echo "GET ${BASE_URL}?search=Avenue  →  200 + libraries with Avenue in address"
curlie -- "${BASE_URL}?search=Avenue"

# --- Pagination edge cases ---
echo
echo "── 14) OK — page beyond total pages ──"
echo "GET ${BASE_URL}?page=99&size=20  →  200 + data=[] + meta.total=15"
curlie -- "${BASE_URL}?page=99&size=20"

echo
echo "── 15) OK — minimal page/size ──"
echo "GET ${BASE_URL}?page=1&size=1  →  200 + exactly 1 item"
curlie -- "${BASE_URL}?page=1&size=1"

# --- Search with special but allowed characters ---
echo
echo "── 16) OK — search with dot and space ──"
echo "GET ${BASE_URL}?search=St.+Martin  →  200 + St. Martin's Library"
curlie -- "${BASE_URL}?search=St.%20Martin"

# --- Invalid input → 422 ---
echo
echo "── 17) 422 — search with angle brackets ──"
echo "GET ${BASE_URL}?search=malicious%3Cscript%3E  →  422 invalid characters"
curlie -- "${BASE_URL}?search=malicious%3Cscript%3E"

echo
echo "── 18) 422 — search with pipe ──"
echo "GET ${BASE_URL}?search=test%7Cpipe  →  422 invalid characters"
curlie -- "${BASE_URL}?search=test%7Cpipe"

echo
echo "═══ All tests completed ═══"
