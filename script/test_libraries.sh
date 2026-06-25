#! /usr/bin/env bash
# Test script for GET /libraries endpoint

echo "── 1) OK — list all libraries (default pagination)  →  200 / data / meta"
curlie -- "http://localhost:8080/libraries"
echo
echo "── 2) OK — page=1&size=5  →  200 / meta.size=5"
curlie -- "http://localhost:8080/libraries?page=1&size=5"
echo
echo "── 3) OK — no match  →  200 / data=[] / meta.total=0"
curlie -- "http://localhost:8080/libraries?search=ZZZZNOTFOUND"
echo
echo "── 4) OK — phone prefix 261  →  200 / libraries with 261"
curlie -- "http://localhost:8080/libraries?search=261"
echo
echo "── 5) OK — phone indicatif 261 34  →  200 / libraries with 261 34"
curlie -- "http://localhost:8080/libraries?search=261 34"
echo
echo "── 6) OK — full phone number  →  200 / Librairie Generale"
curlie -- "http://localhost:8080/libraries?search=261 33 98 765 43"
echo
echo "── 7) OK — name 'Tech'  →  200 / Tech Library"
curlie -- "http://localhost:8080/libraries?search=Tech"
echo
echo "── 8) OK — name with accents 'Mediatheque'  →  200 / Mediatheque Centrale"
curlie -- "http://localhost:8080/libraries?search=Mediatheque"
echo
echo "── 9) OK — name with apostrophe  →  200 / St Martin's Library"
curlie -- "http://localhost:8080/libraries?search=St. Martin's"
echo
echo "── 10) OK — email domain @library.com  →  200 / tech@library.com"
curlie -- "http://localhost:8080/libraries?search=@library.com"
echo
echo "── 11) OK — email local-part info@  →  200 / Librairie Generale"
curlie -- "http://localhost:8080/libraries?search=info@"
echo
echo "── 12) OK — address 'Campus'  →  200 / libraries with Campus"
curlie -- "http://localhost:8080/libraries?search=Campus"
echo
echo "── 13) OK — address 'Avenue'  →  200 / libraries with Avenue"
curlie -- "http://localhost:8080/libraries?search=Avenue"
echo
echo "── 14) OK — page=99&size=20  →  200 / data=[] / meta.total=15"
curlie -- "http://localhost:8080/libraries?page=99&size=20"
echo
echo "── 15) OK — page=1&size=1  →  200 / exactly 1 item"
curlie -- "http://localhost:8080/libraries?page=1&size=1"
echo
echo "── 16) OK — dot and space 'St Martin'  →  200 / St Martin's Library"
curlie -- "http://localhost:8080/libraries?search=St. Martin"
echo
echo "── 17) 422 — angle brackets  →  422 invalid characters"
curlie -- "http://localhost:8080/libraries?search=<script>"
echo
echo "── 18) 422 — pipe  →  422 invalid characters"
curlie -- "http://localhost:8080/libraries?search=test|pipe"
