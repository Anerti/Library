#! /usr/bin/env bash
# Test script for GET /libraries endpoint

echo "── 1) OK — GET /libraries  →  200 / data / meta"
curlie "http://localhost:8080/libraries"
echo
echo "── 2) OK — GET /libraries?page=1&size=5  →  200 / meta.size=5"
curlie "http://localhost:8080/libraries?page=1&size=5"
echo
echo "── 3) OK — GET /libraries?search=ZZZZNOTFOUND  →  200 / data=[] / meta.total=0"
curlie "http://localhost:8080/libraries?search=ZZZZNOTFOUND"
echo
echo "── 4) OK — GET /libraries?search=261  →  200 / libraries with 261"
curlie "http://localhost:8080/libraries?search=261"
echo
echo "── 5) OK — GET /libraries?search=261 34  →  200 / libraries with 261 34"
curlie "http://localhost:8080/libraries" "search==261 34"
echo
echo "── 6) OK — GET /libraries?search=261 33 98 765 43  →  200 / Librairie Generale"
curlie "http://localhost:8080/libraries" "search==261 33 98 765 43"
echo
echo "── 7) OK — GET /libraries?search=Tech  →  200 / Tech Library"
curlie "http://localhost:8080/libraries?search=Tech"
echo
echo "── 8) OK — GET /libraries?search=Mediatheque  →  200 / Mediatheque Centrale"
curlie "http://localhost:8080/libraries?search=Mediatheque"
echo
echo "── 9) OK — GET /libraries?search=St. Martin's  →  200 / St Martin's Library"
curlie "http://localhost:8080/libraries" "search==St. Martin's"
echo
echo "── 10) OK — GET /libraries?search=@library.com  →  200 / tech@library.com"
curlie "http://localhost:8080/libraries?search=@library.com"
echo
echo "── 11) OK — GET /libraries?search=info@  →  200 / Librairie Generale"
curlie "http://localhost:8080/libraries?search=info@"
echo
echo "── 12) OK — GET /libraries?search=Campus  →  200 / libraries with Campus"
curlie "http://localhost:8080/libraries?search=Campus"
echo
echo "── 13) OK — GET /libraries?search=Avenue  →  200 / libraries with Avenue"
curlie "http://localhost:8080/libraries?search=Avenue"
echo
echo "── 14) OK — GET /libraries?page=99&size=20  →  200 / data=[] / meta.total=15"
curlie "http://localhost:8080/libraries?page=99&size=20"
echo
echo "── 15) OK — GET /libraries?page=1&size=1  →  200 / exactly 1 item"
curlie "http://localhost:8080/libraries?page=1&size=1"
echo
echo "── 16) OK — GET /libraries?search=St. Martin  →  200 / St Martin's Library"
curlie "http://localhost:8080/libraries" "search==St. Martin"
echo
echo "── 17) 422 — GET /libraries?search=<script>  →  422 invalid characters"
curlie "http://localhost:8080/libraries" "search==<script>"
echo
echo "── 18) 422 — GET /libraries?search=test|pipe  →  422 invalid characters"
curlie "http://localhost:8080/libraries" "search==test|pipe"
