#! /usr/bin/env bash
# Test script for GET /libraries endpoint
set -euo pipefail

echo "── 1) OK — list all libraries (default pagination)  →  200 + data + meta" && curlie -- "http://localhost:8080/libraries"
echo "── 2) OK — page=1&size=5  →  200 + meta.size=5" && curlie -- "http://localhost:8080/libraries?page=1&size=5"
echo "── 3) OK — no match  →  200 + data=[] + meta.total=0" && curlie -- "http://localhost:8080/libraries?search=ZZZZNOTFOUND"
echo "── 4) OK — phone prefix +261  →  200 + libraries with +261" && curlie -- "http://localhost:8080/libraries?search=+261"
echo "── 5) OK — phone indicatif +261 34  →  200 + libraries with +261 34" && curlie -- "http://localhost:8080/libraries?search=+261 34"
echo "── 6) OK — full phone number  →  200 + Librairie Générale" && curlie -- "http://localhost:8080/libraries?search=+261 33 98 765 43"
echo "── 7) OK — name 'Tech'  →  200 + Tech Library" && curlie -- "http://localhost:8080/libraries?search=Tech"
echo "── 8) OK — name with accents 'Médiathèque'  →  200 + Médiathèque Centrale" && curlie -- "http://localhost:8080/libraries?search=Médiathèque"
echo "── 9) OK — name with apostrophe  →  200 + St. Martin's Library" && curlie -- "http://localhost:8080/libraries?search=St. Martin's"
echo "── 10) OK — email domain @library.com  →  200 + tech@library.com" && curlie -- "http://localhost:8080/libraries?search=@library.com"
echo "── 11) OK — email local-part info@  →  200 + Librairie Générale" && curlie -- "http://localhost:8080/libraries?search=info@"
echo "── 12) OK — address 'Campus'  →  200 + libraries with Campus" && curlie -- "http://localhost:8080/libraries?search=Campus"
echo "── 13) OK — address 'Avenue'  →  200 + libraries with Avenue" && curlie -- "http://localhost:8080/libraries?search=Avenue"
echo "── 14) OK — page=99&size=20  →  200 + data=[] + meta.total=15" && curlie -- "http://localhost:8080/libraries?page=99&size=20"
echo "── 15) OK — page=1&size=1  →  200 + exactly 1 item" && curlie -- "http://localhost:8080/libraries?page=1&size=1"
echo "── 16) OK — dot+space 'St. Martin'  →  200 + St. Martin's Library" && curlie -- "http://localhost:8080/libraries?search=St. Martin"
echo "── 17) 422 — angle brackets  →  422 invalid characters" && curlie -- "http://localhost:8080/libraries?search=<script>"
echo "── 18) 422 — pipe  →  422 invalid characters" && curlie -- "http://localhost:8080/libraries?search=test|pipe"
