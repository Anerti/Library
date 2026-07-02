-- Generated with: uuidgen
-- Password (plaintext): Str0ng!Passphrase1 → $argon2id$v=19$m=47104,t=3,p=1$5IU3I4BVTD0VP5cnv4pQtw$cLeGXTKYDvCJzeKSufZrME6KtX93WYp/kxiLkF5MykA
INSERT INTO users (id, last_name, first_name, birth_date, email, password, phone, role)
VALUES ('943781cb-8237-46e1-9da5-59d3c11e972f', 'Reader', 'Alice', '1992-07-21', 'alice.reader@mail.com',
        '$argon2id$v=19$m=47104,t=3,p=1$5IU3I4BVTD0VP5cnv4pQtw$cLeGXTKYDvCJzeKSufZrME6KtX93WYp/kxiLkF5MykA',
        '+261 32 333 333', 'CUSTOMER'),
       ('383d233b-06cb-4383-a18e-7be27adaca37', 'Moderator', 'Bob', '1985-03-14', 'bob.moderator@mail.com',
        '$argon2id$v=19$m=47104,t=3,p=1$5IU3I4BVTD0VP5cnv4pQtw$cLeGXTKYDvCJzeKSufZrME6KtX93WYp/kxiLkF5MykA',
        '+261 32 444 444', 'ADMIN') ON CONFLICT (email) DO NOTHING;
