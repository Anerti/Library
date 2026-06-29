#! /usr/bin/env bash
# Test script for POST /auth/register endpoint

# Test 1 + 3: create → duplicate (self-contained conflict)
echo "── 1) 201 — POST /auth/register (all valid)  →  201 / Marie Dupont"
curlie POST "http://localhost:8080/auth/register" lastName="Dupont" firstName="Marie" birthDate="1995-03-10" email="marie.register@mail.com" password="Str0ng!Passphrase" phone="+26123456789"
echo

echo "── 2) 201 — POST /auth/register (no phone)  →  201 / Jean Martin"
curlie POST "http://localhost:8080/auth/register" lastName="Martin" firstName="Jean" birthDate="2000-07-15" email="jean.register@mail.com" password="Secur3#Phrase"
echo

echo "── 3) 409 — POST /auth/register (duplicate email from test 1)  →  409 / already used"
curlie POST "http://localhost:8080/auth/register" lastName="Dupont" firstName="Marie" birthDate="1995-03-10" email="marie.register@mail.com" password="Str0ng!Passphrase" phone="+26123456789"
echo

echo "── 4) 422 — POST /auth/register (lastName null)  →  422 / lastName is required"
curlie POST "http://localhost:8080/auth/register" firstName="Marie" birthDate="1995-03-10" email="test4@mail.com" password="Str0ng!Passphrase"
echo

echo "── 5) 422 — POST /auth/register (lastName blank)  →  422 / lastName is required"
curlie POST "http://localhost:8080/auth/register" lastName="" firstName="Marie" birthDate="1995-03-10" email="test5@mail.com" password="Str0ng!Passphrase"
echo

echo "── 6) 422 — POST /auth/register (lastName with digits)  →  422 / forbidden characters"
curlie POST "http://localhost:8080/auth/register" lastName="Dupont123" firstName="Marie" birthDate="1995-03-10" email="test6@mail.com" password="Str0ng!Passphrase"
echo

echo "── 7) 422 — POST /auth/register (lastName too long)  →  422 / cannot be longer than 100"
curlie POST "http://localhost:8080/auth/register" lastName="$(printf 'D%.0s' {1..101})" firstName="Marie" birthDate="1995-03-10" email="test7@mail.com" password="Str0ng!Passphrase"
echo

echo "── 8) 422 — POST /auth/register (firstName null)  →  422 / firstName is required"
curlie POST "http://localhost:8080/auth/register" lastName="Dupont" birthDate="1995-03-10" email="test8@mail.com" password="Str0ng!Passphrase"
echo

echo "── 9) 422 — POST /auth/register (firstName blank)  →  422 / firstName is required"
curlie POST "http://localhost:8080/auth/register" lastName="Dupont" firstName="" birthDate="1995-03-10" email="test9@mail.com" password="Str0ng!Passphrase"
echo

echo "── 10) 422 — POST /auth/register (firstName with digits)  →  422 / forbidden characters"
curlie POST "http://localhost:8080/auth/register" lastName="Dupont" firstName="Marie123" birthDate="1995-03-10" email="test10@mail.com" password="Str0ng!Passphrase"
echo

echo "── 11) 422 — POST /auth/register (firstName too long)  →  422 / cannot be longer than 100"
curlie POST "http://localhost:8080/auth/register" lastName="Dupont" firstName="$(printf 'M%.0s' {1..101})" birthDate="1995-03-10" email="test11@mail.com" password="Str0ng!Passphrase"
echo

echo "── 12) 422 — POST /auth/register (email null)  →  422 / email is required"
curlie POST "http://localhost:8080/auth/register" lastName="Dupont" firstName="Marie" birthDate="1995-03-10" password="Str0ng!Passphrase"
echo

echo "── 13) 422 — POST /auth/register (email blank)  →  422 / email is required"
curlie POST "http://localhost:8080/auth/register" lastName="Dupont" firstName="Marie" birthDate="1995-03-10" email="" password="Str0ng!Passphrase"
echo

echo "── 14) 422 — POST /auth/register (invalid email format)  →  422 / Invalid email format"
curlie POST "http://localhost:8080/auth/register" lastName="Dupont" firstName="Marie" birthDate="1995-03-10" email="not-an-email" password="Str0ng!Passphrase"
echo

echo "── 15) 422 — POST /auth/register (email forbidden chars)  →  422 / Invalid input for email"
curlie POST "http://localhost:8080/auth/register" lastName="Dupont" firstName="Marie" birthDate="1995-03-10" email="marie @mail.com" password="Str0ng!Passphrase"
echo

echo "── 16) 422 — POST /auth/register (email too long)  →  422 / cannot be longer than 100"
curlie POST "http://localhost:8080/auth/register" lastName="Dupont" firstName="Marie" birthDate="1995-03-10" email="$(printf 'm%.0s' {1..92})@mail.com" password="Str0ng!Passphrase"
echo

echo "── 17) 422 — POST /auth/register (password null)  →  422 / password is required"
curlie POST "http://localhost:8080/auth/register" lastName="Dupont" firstName="Marie" birthDate="1995-03-10" email="test17@mail.com"
echo

echo "── 18) 422 — POST /auth/register (password blank)  →  422 / password is required"
curlie POST "http://localhost:8080/auth/register" lastName="Dupont" firstName="Marie" birthDate="1995-03-10" email="test18@mail.com" password=""
echo

echo "── 19) 422 — POST /auth/register (password too short)  →  422 / at least 12 characters"
curlie POST "http://localhost:8080/auth/register" lastName="Dupont" firstName="Marie" birthDate="1995-03-10" email="test19@mail.com" password="Short1!x"
echo

echo "── 20) 422 — POST /auth/register (password no uppercase)  →  422 / uppercase"
curlie POST "http://localhost:8080/auth/register" lastName="Dupont" firstName="Marie" birthDate="1995-03-10" email="test20@mail.com" password="lowercase1!phrase"
echo

echo "── 21) 422 — POST /auth/register (password no lowercase)  →  422 / lowercase"
curlie POST "http://localhost:8080/auth/register" lastName="Dupont" firstName="Marie" birthDate="1995-03-10" email="test21@mail.com" password="UPPERCASE1!PHRASE"
echo

echo "── 22) 422 — POST /auth/register (password no digit)  →  422 / digits"
curlie POST "http://localhost:8080/auth/register" lastName="Dupont" firstName="Marie" birthDate="1995-03-10" email="test22@mail.com" password="NoDigit!Passphrase"
echo

echo "── 23) 422 — POST /auth/register (password no special)  →  422 / special character"
curlie POST "http://localhost:8080/auth/register" lastName="Dupont" firstName="Marie" birthDate="1995-03-10" email="test23@mail.com" password="NoSpecialChar1Phrase"
echo

echo "── 24) 422 — POST /auth/register (birthDate null)  →  422 / BirthDate is required"
curlie POST "http://localhost:8080/auth/register" lastName="Dupont" firstName="Marie" email="test24@mail.com" password="Str0ng!Passphrase"
echo

echo "── 25) 422 — POST /auth/register (birthDate future)  →  422 / cannot be in the future"
curlie POST "http://localhost:8080/auth/register" lastName="Dupont" firstName="Marie" birthDate="2099-01-01" email="test25@mail.com" password="Str0ng!Passphrase"
echo

echo "── 26) 422 — POST /auth/register (age < 12)  →  422 / at least 12 years old"
curlie POST "http://localhost:8080/auth/register" lastName="Dupont" firstName="Marie" birthDate="2020-06-01" email="test26@mail.com" password="Str0ng!Passphrase"
echo
