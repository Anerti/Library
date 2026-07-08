INSERT INTO author (id, first_name, last_name)
VALUES ('0336c58f-664b-40da-96d0-cac2888cd00a', 'George', 'Orwell'),
       ('f8a4dddb-5f19-4961-8f71-f2cc6aa8ec36', 'Jane', 'Austen') ON CONFLICT DO NOTHING;
