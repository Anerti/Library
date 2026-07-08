-- Passwords (plaintext): marie / Admin both use 'Str0ng!Passphrase1'
INSERT INTO users (id, last_name, first_name, birth_date, email, password, phone, role)
VALUES ('fcd16cd1-0f2b-460e-a6c2-3dece9e89d22', 'Dupont', 'Marie', '1995-03-10', 'marie@mail.com',
        '$argon2id$v=19$m=47104,t=3,p=1$5IU3I4BVTD0VP5cnv4pQtw$cLeGXTKYDvCJzeKSufZrME6KtX93WYp/kxiLkF5MykA',
        '+261 32 456 789', 'CUSTOMER'),
       ('ebd9a337-8ae6-4ad7-be38-151d81bc27c9', 'Admin', 'System', '1990-01-01', 'admin@library.com',
        '$argon2id$v=19$m=47104,t=3,p=1$hXReI5CfF4p7qdLd92rrOw$CIoYOwttXCqgieOh2Pca4SO5DhTL9N8SsgvpcxUfnxo',
        '+261 20 123 456', 'ADMIN') ON CONFLICT (email) DO NOTHING;
