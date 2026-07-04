-- Generated with: uuidgen
-- Password (plaintext): Str0ng!Passphrase1 → $argon2id$v=19$m=47104,t=3,p=1$5IU3I4BVTD0VP5cnv4pQtw$cLeGXTKYDvCJzeKSufZrME6KtX93WYp/kxiLkF5MykA
INSERT INTO users (id, last_name, first_name, birth_date, email, password, phone, role)
VALUES ('d3c7acc2-b37f-4ac3-ad8b-0707de99da3e', 'Dupont', 'Jeanne', '1993-11-02', 'jeanne.dupont@mail.com',
        '$argon2id$v=19$m=47104,t=3,p=1$5IU3I4BVTD0VP5cnv4pQtw$cLeGXTKYDvCJzeKSufZrME6KtX93WYp/kxiLkF5MykA',
        '+261 32 555 555', 'CUSTOMER'),
       ('b430abf6-da95-4609-a9bf-8af324a6b169', 'Rabe', 'Tiana', '1988-06-18', 'tiana.rabe@mail.com',
        '$argon2id$v=19$m=47104,t=3,p=1$5IU3I4BVTD0VP5cnv4pQtw$cLeGXTKYDvCJzeKSufZrME6KtX93WYp/kxiLkF5MykA',
        '+261 32 666 666', 'CUSTOMER'),
       ('321fc2b5-c534-42a8-b19a-08da5a87c8a3', 'Randria', 'Mialy', '1990-02-28', 'mialy.randria@mail.com',
        '$argon2id$v=19$m=47104,t=3,p=1$5IU3I4BVTD0VP5cnv4pQtw$cLeGXTKYDvCJzeKSufZrME6KtX93WYp/kxiLkF5MykA',
        '+261 32 777 777', 'ADMIN'),
       ('43f5c06a-b0c9-4992-80a1-71c5507a07f1', 'Rakoto', 'Faly', '1985-09-05', 'faly.rakoto@mail.com',
        '$argon2id$v=19$m=47104,t=3,p=1$5IU3I4BVTD0VP5cnv4pQtw$cLeGXTKYDvCJzeKSufZrME6KtX93WYp/kxiLkF5MykA',
        '+261 32 888 888', 'ADMIN')
ON CONFLICT (email) DO NOTHING;
