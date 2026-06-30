-- Generated with: uuidgen
-- Password (plaintext): password123 → $argon2id$v=19$m=47104,t=3,p=1$...
INSERT INTO users (id, last_name, first_name, birth_date, email, password, phone, role)
VALUES ('e5477ed7-046b-4974-983b-4fa0702991d8', 'Doe', 'John', '1990-05-15', 'john.doe@mail.com',
        '$argon2id$v=19$m=47104,t=3,p=1$5IU3I4BVTD0VP5cnv4pQtw$cLeGXTKYDvCJzeKSufZrME6KtX93WYp/kxiLkF5MykA',
        '+261 32 123 456', 'CUSTOMER')
ON CONFLICT (email) DO NOTHING;
