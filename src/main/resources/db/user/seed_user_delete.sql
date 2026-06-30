-- Generated with: uuidgen
-- Password (plaintext): Str0ng!Passphrase1 → $argon2id$v=19$m=47104,t=3,p=1$...
INSERT INTO users (id, last_name, first_name, birth_date, email, password, phone, role)
VALUES ('e5477ed7-046b-4974-983b-4fa0702991d8', 'Doe', 'John', '1990-05-15', 'john.doe@mail.com',
        '$argon2id$v=19$m=47104,t=3,p=1$5IU3I4BVTD0VP5cnv4pQtw$cLeGXTKYDvCJzeKSufZrME6KtX93WYp/kxiLkF5MykA',
        '+261 32 123 456', 'CUSTOMER'),
       ('31263281-ab3c-4c40-82d8-219f25286519', 'Target', 'Customer', '2000-01-01', 'target.customer@mail.com',
        '$argon2id$v=19$m=47104,t=3,p=1$5IU3I4BVTD0VP5cnv4pQtw$cLeGXTKYDvCJzeKSufZrME6KtX93WYp/kxiLkF5MykA',
        '+261 32 111 111', 'CUSTOMER'),
       ('ed0084ca-6577-4b64-b745-e2f3dfc6137a', 'Target', 'Admin', '2000-01-01', 'target.admin@mail.com',
        '$argon2id$v=19$m=47104,t=3,p=1$5IU3I4BVTD0VP5cnv4pQtw$cLeGXTKYDvCJzeKSufZrME6KtX93WYp/kxiLkF5MykA',
        '+261 32 222 222', 'ADMIN')
ON CONFLICT (email) DO NOTHING;
