#! /usr/bin/env bash
# Test script for POST /libraries endpoint

echo "── 1) 201 — POST /libraries (all valid)  →  201 / id + fields"
curlie -X POST "http://localhost:8080/libraries" name="Central Library" phone="+261 33 44 55 77" email="test-create@test.mg" address="Main Street"
echo

echo "── 2) 201 — POST /libraries (another valid)  →  201 / different data"
curlie -X POST "http://localhost:8080/libraries" name="Parc Library" phone="+261 34 98 765 43" email="parc-create@test.mg" address="Parc de la Lecture"
echo

echo "── 3) 409 — POST /libraries (duplicate email)  →  409 / Library with email ... already exists"
curlie -X POST "http://localhost:8080/libraries" name="Central Library" phone="+261 33 44 55 77" email="tech@library.com" address="Main Street"
echo

echo "── 4) 422 — POST /libraries (name blank)  →  422 / name is required"
curlie -X POST "http://localhost:8080/libraries" name="" phone="+261 33 44 55 77" email="no-name@test.mg" address="Main Street"
echo

echo "── 5) 422 — POST /libraries (name with digits)  →  422 / forbidden characters"
curlie -X POST "http://localhost:8080/libraries" name="Library1" phone="+261 33 44 55 77" email="bad-name@test.mg" address="Main Street"
echo

echo "── 6) 422 — POST /libraries (phone blank)  →  422 / Phone number is required"
curlie -X POST "http://localhost:8080/libraries" name="Central Library" phone="" email="no-phone@test.mg" address="Main Street"
echo

echo "── 7) 422 — POST /libraries (phone invalid)  →  422 / Invalid phone format"
curlie -X POST "http://localhost:8080/libraries" name="Central Library" phone="abc" email="bad-phone@test.mg" address="Main Street"
echo

echo "── 8) 422 — POST /libraries (email blank)  →  422 / email is required"
curlie -X POST "http://localhost:8080/libraries" name="Central Library" phone="+261 33 44 55 77" email="" address="Main Street"
echo

echo "── 9) 422 — POST /libraries (email invalid)  →  422 / Invalid email format"
curlie -X POST "http://localhost:8080/libraries" name="Central Library" phone="+261 33 44 55 77" email="not-an-email" address="Main Street"
echo

echo "── 10) 422 — POST /libraries (address blank)  →  422 / address is required"
curlie -X POST "http://localhost:8080/libraries" name="Central Library" phone="+261 33 44 55 77" email="no-address@test.mg" address=""
echo

echo "── 11) 422 — POST /libraries (address with digits)  →  422 / forbidden characters"
curlie -X POST "http://localhost:8080/libraries" name="Central Library" phone="+261 33 44 55 77" email="bad-address@test.mg" address="123 Main St"
echo
