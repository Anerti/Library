#! /usr/bin/env bash
# Test script for GET /books endpoint

echo "── 1) OK — GET /books  →  200 / 5 books"
curlie "http://localhost:8080/books"
echo
echo "── 2) OK — GET /books?title=Petit  →  200 / Le Petit Prince"
curlie "http://localhost:8080/books?title=Petit"
echo
echo "── 3) OK — GET /books?title=NONEXISTENT  →  200 / data=null / pagination.total=0"
curlie "http://localhost:8080/books?title=NONEXISTENT"
echo
echo "── 4) OK — GET /books?publisher=Penguin  →  200 / 1984"
curlie "http://localhost:8080/books?publisher=Penguin"
echo
echo "── 5) OK — GET /books?publisher=NONEXISTENT  →  200 / data=null / pagination.total=0"
curlie "http://localhost:8080/books?publisher=NONEXISTENT"
echo
echo "── 6) OK — GET /books?isbn=978-2-07-061275-8  →  200 / Le Petit Prince"
curlie "http://localhost:8080/books?isbn=978-2-07-061275-8"
echo
echo "── 7) OK — GET /books?lastName=Hugo  →  200 / Les Misérables"
curlie "http://localhost:8080/books?lastName=Hugo"
echo
echo "── 8) OK — GET /books?genre=Dystopian  →  200 / 1984"
curlie "http://localhost:8080/books?genre=Dystopian"
echo
echo "── 9) OK — GET /books?genre=Fiction  →  200 / 4 books"
curlie "http://localhost:8080/books?genre=Fiction"
echo
echo "── 10) OK — GET /books?genre=NONEXISTENT  →  200 / data=null / pagination.total=0"
curlie "http://localhost:8080/books?genre=NONEXISTENT"
echo
echo "── 11) OK — GET /books?page=1&size=2  →  200 / pagination.size=2"
curlie "http://localhost:8080/books?page=1&size=2"
echo
echo "── 12) OK — GET /books?page=99&size=20  →  200 / data=null / pagination.total=5"
curlie "http://localhost:8080/books?page=99&size=20"
echo
echo "── 13) OK — GET /books?title=Prince&publisher=Gallimard  →  200 / Le Petit Prince"
curlie "http://localhost:8080/books?title=Prince&publisher=Gallimard"
echo
echo "── 14) 422 — GET /books?title=<script>  →  422 invalid characters"
curlie "http://localhost:8080/books" "title==<script>"
echo
echo "── 15) 422 — GET /books?publisher=|bad  →  422 invalid characters"
curlie "http://localhost:8080/books" "publisher==|bad"
echo
echo "── 16) 422 — GET /books?isbn=abc  →  422 isbn too short"
curlie "http://localhost:8080/books" "isbn==abc"
echo
echo "── 17) 422 — GET /books?lastName=<script>  →  422 invalid characters"
curlie "http://localhost:8080/books" "lastName==<script>"
echo
echo "── 18) 422 — GET /books?genre=<script>  →  422 invalid characters"
curlie "http://localhost:8080/books" "genre==<script>"
