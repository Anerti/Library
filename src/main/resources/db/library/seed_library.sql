INSERT INTO library (id, name, phone, email, address) VALUES
    ('43af5aff-055d-4af6-a708-4d165ad7abac', 'Bibliothèque Municipale',       '+261 34 12 345 67', 'contact@biblio-municipale.mg',   '15 Avenue de l''Indépendance, Antananarivo'),
    ('1e9a43cd-2e6a-409d-8b5a-d3f57a2036f0', 'Tech Library',                 '+261 32 01 234 56', 'tech@library.com',              '456 Avenue Technologique'),
    ('c70a6c6b-6145-40db-b230-031db0fbfd66', 'Librairie Générale',           '+261 33 98 765 43', 'info@librairie-generale.mg',    '22 Rue des Livres, Antananarivo'),
    ('227b8fbc-f13f-4cfc-9774-5f6ddc1328f0', 'St. Martin''s Library',        '+261 20 11 223 34', 'saint@martin-library.org',      '1'' Rue Saint-Martin'),
    ('2639ad05-3715-4bb1-9edb-26ff8030f9fe', 'Médiathèque Centrale',         '+261 34 56 789 01', 'mediatheque@centrale.mg',       '8 Place de la Culture'),
    ('8c0bf175-f484-46f6-8fbf-de369fde450f', 'Bibliothèque Universitaire',   '+261 32 55 44 332', 'uni@bibliotheque-univ.mg',      'Campus Universitaire BP 123'),

    -- Additional entries for richer test coverage
    ('051e1712-c132-4c4e-9909-39306cfd525f', 'Ankatso Library',              '+261 34 00 111 22', 'ankatso@library.mg',            'Ankatso Campus, Tana'),
    ('61e5dc1b-602b-4d98-a3b5-f05ab88628bf', 'Mahamasina Biblio',            '+261 20 22 333 44', 'mahamasina@book.mg',            'Mahamasina, Antananarivo'),
    ('473f4662-da67-4114-ac09-704a85fecb99', 'Numérique Lab',               '+261 33 11 222 33', 'lab@numerique.mg',              'Lot II J 77, Analakely'),
    ('83658963-0554-48c6-acaa-f2ce607ed82f', 'Centre de Documentation',      '+261 34 78 910 11', 'doc@centre-doc.mg',             '67 Ha, Antananarivo'),
    ('44fa87b1-bcf5-47e4-befe-b6d6d68566fc', 'Digitale Bibliothek',         '+261 32 44 556 77', 'info@digital-bib.mg',           'Rue de la Réforme'),
    ('b9deef47-28fc-415a-b859-580446374f2a', 'Bibliothèque du Jardin',       '+261 20 55 667 88', 'jardin@biblio.mg',              'Parc de la Coulée Verte'),
    ('a22daec1-0460-4397-b255-e10889521618', 'Knowledge Hub',               '+261 33 88 990 01', 'hub@knowledge.org',             'Rue de la Science'),
    ('79928633-dc65-4d58-acec-dac19cd5e975', 'Center for Reading',           '+261 34 99 001 22', 'reading@center.mg',             'Boulevard de l''Europe'),
    ('a31a79cd-dbe7-40f9-a821-cbddd5cd892f', 'Réseau des Bibliothèques',    '+261 32 77 889 00', 'reseau@biblio-network.mg',      'Réseau Tana Est')
ON CONFLICT DO NOTHING;
