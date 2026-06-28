#! /usr/bin/env bash
# Test script for GET /books endpoint

echo "── 1) OK — GET /books  →  200 / 6 books"
curlie GET "http://localhost:8080/books"
echo
echo "── 2) OK — GET /books?title=Petit  →  200 / Le Petit Prince"
curlie GET "http://localhost:8080/books?title=Petit"
echo
echo "── 3) OK — GET /books?title=NONEXISTENT  →  200 / data=null / pagination.total=0"
curlie GET "http://localhost:8080/books?title=NONEXISTENT"
echo
echo "── 4) OK — GET /books?publisher=Gallimard  →  200 / 3 books"
curlie GET "http://localhost:8080/books?publisher=Gallimard"
echo
echo "── 5) OK — GET /books?publisher=Penguin  →  200 / data=null / pagination.total=0"
curlie GET "http://localhost:8080/books?publisher=Penguin"
echo
echo "── 6) OK — GET /books?isbn=978-2-07-061275-8  →  200 / Le Petit Prince"
curlie GET "http://localhost:8080/books?isbn=978-2-07-061275-8"
echo
echo "── 7) OK — GET /books?lastName=Huxley  →  200 / Brave New World"
curlie GET "http://localhost:8080/books?lastName=Huxley"
echo
echo "── 8) OK — GET /books?genre=Dystopian  →  200 / Brave New World"
curlie GET "http://localhost:8080/books?genre=Dystopian"
echo
echo "── 9) OK — GET /books?genre=Fantasy  →  200 / data=null / pagination.total=0"
curlie GET "http://localhost:8080/books?genre=Fantasy"
echo
echo "── 10) OK — GET /books?page=1&size=2  →  200 / pagination.size=2"
curlie GET "http://localhost:8080/books?page=1&size=2"
echo
echo "── 11) OK — GET /books?page=99&size=20  →  200 / data=null / pagination.total=6"
curlie GET "http://localhost:8080/books?page=99&size=20"
echo
echo "── 12) OK — GET /books?title=Petit&publisher=Gallimard  →  200 / Le Petit Prince"
curlie GET "http://localhost:8080/books?title=Petit&publisher=Gallimard"
echo
echo "── 13) 422 — GET /books?title=<script>  →  422 invalid characters"
curlie GET "http://localhost:8080/books" "title==<script>"
echo
echo "── 14) 422 — GET /books?publisher=|bad  →  422 invalid characters"
curlie GET "http://localhost:8080/books" "publisher==|bad"
echo
echo "── 15) 422 — GET /books?isbn=abc  →  422 isbn invalid"
curlie GET "http://localhost:8080/books" "isbn==abc"
echo
echo "── 16) 422 — GET /books?lastName=<script>  →  422 invalid characters"
curlie GET "http://localhost:8080/books" "lastName==<script>"
echo
echo "── 17) 422 — GET /books?genre=<script>  →  422 invalid characters"
curlie GET "http://localhost:8080/books" "genre==<script>"
