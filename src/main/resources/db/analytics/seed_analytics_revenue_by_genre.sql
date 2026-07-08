-- Seed data for GET /libraries/{libraryId}/analytics/revenue/by-genre
-- Generated with uuidgen
--
-- Self-contained: all required entities (library, genre, book, author,
-- book_copy, users, sale) are created in this script.
--
-- Revenue scenario (SOLD sales per genre):
--   Revenue Fantasy (The Dragon's Lair)
--     Sale 1 (Jan 2026): 1 copy × $12.99
--     Sale 3 (Jun 2026): 1 copy × $12.99  → total = $25.98, 2 sold
--   Revenue Science Fiction (Starship Odyssey)
--     Sale 1 (Jan 2026): 1 copy × $14.99   → total = $14.99, 1 sold
--   Revenue Mystery (The Hidden Clue)
--     Sale 2 (Mar 2026): 1 copy × $11.99   → total = $11.99, 1 sold
--   Romance (existing genre, no sales)      → not returned
--
-- Expected revenue by genre (DESC): Revenue Fantasy > Revenue Science Fiction > Revenue Mystery
-- Expected with date filter [2026-01-01, 2026-01-31]: Revenue Fantasy ($12.99) + Revenue Science Fiction ($14.99)

-- Library
INSERT INTO library (id, name, phone, email, address)
VALUES ('d86fc243-e0a7-4112-974e-f00e06efcd2f', 'Revenue Analytics Library', '+261 34 99 777 66',
        'revenue@analytics.mg', '99 Revenue Street')
ON CONFLICT DO NOTHING;

-- Genres
INSERT INTO genre (id, name)
VALUES
    ('1cdf79ba-90ea-494b-b475-ef5b5db14633', 'Revenue Fantasy'),
    ('d93f52b0-982b-4fa3-93f8-eac096d8e717', 'Revenue Science Fiction'),
    ('eb602742-e7ff-4f26-ad25-26e9b97ed4ba', 'Revenue Mystery')
ON CONFLICT (name) DO NOTHING;

-- Authors
INSERT INTO author (id, first_name, last_name)
VALUES
    ('20f8e36f-4e0c-4d45-a9ce-01488ee3e8a0', 'Morgan', 'Blackwood'),
    ('59ef720e-4f9b-47d2-80b1-5f4d90abd5ed', 'Nadia', 'Kovacs'),
    ('16c75c69-df22-4369-8033-063e73227724', 'Thomas', 'Rivers')
ON CONFLICT ON CONSTRAINT uq_author_name DO NOTHING;

-- Books
INSERT INTO book (id, title, summary, isbn, publisher, published_at, created_at)
VALUES
    ('25492418-ecb7-42e6-9578-30661cbd5116', 'The Dragon''s Lair',
     'An epic fantasy about dragons and ancient magic', '978-0-00-100001-1',
     'Fantasy Press', '2024-03-15', CURRENT_TIMESTAMP),
    ('a13d572a-bf60-43da-b023-5e3d4eb9b92f', 'Starship Odyssey',
     'A space crew voyages beyond known galaxies', '978-0-00-100002-2',
     'Sci-Fi Publishing', '2024-06-20', CURRENT_TIMESTAMP),
    ('0192a522-534c-4c44-ab64-945295c2c7b1', 'The Hidden Clue',
     'A detective unravels a decades-old conspiracy', '978-0-00-100003-3',
     'Crime Books', '2024-09-10', CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

-- Author-book links
INSERT INTO author_book (book_id, author_id)
VALUES
    ('25492418-ecb7-42e6-9578-30661cbd5116', '20f8e36f-4e0c-4d45-a9ce-01488ee3e8a0'),
    ('a13d572a-bf60-43da-b023-5e3d4eb9b92f', '59ef720e-4f9b-47d2-80b1-5f4d90abd5ed'),
    ('0192a522-534c-4c44-ab64-945295c2c7b1', '16c75c69-df22-4369-8033-063e73227724')
ON CONFLICT DO NOTHING;

-- Book-genre links
INSERT INTO book_genre (book_id, genre_id)
VALUES
    ('25492418-ecb7-42e6-9578-30661cbd5116', '1cdf79ba-90ea-494b-b475-ef5b5db14633'),
    ('a13d572a-bf60-43da-b023-5e3d4eb9b92f', 'd93f52b0-982b-4fa3-93f8-eac096d8e717'),
    ('0192a522-534c-4c44-ab64-945295c2c7b1', 'eb602742-e7ff-4f26-ad25-26e9b97ed4ba')
ON CONFLICT DO NOTHING;

-- Book copies (one copy per book)
INSERT INTO book_copy (id, price, format, library_id, book_id, status, page_number, updated_at)
VALUES
    ('9739f81f-f886-4aad-843a-bd81cb998bc4', 12.99, 'PAPERBACK'::book_copy_format,
     'd86fc243-e0a7-4112-974e-f00e06efcd2f', '25492418-ecb7-42e6-9578-30661cbd5116',
     'AVAILABLE', 350, CURRENT_TIMESTAMP),
    ('57122a10-f07a-4038-83a8-3fa6f8ca5fa6', 14.99, 'PAPERBACK'::book_copy_format,
     'd86fc243-e0a7-4112-974e-f00e06efcd2f', 'a13d572a-bf60-43da-b023-5e3d4eb9b92f',
     'AVAILABLE', 420, CURRENT_TIMESTAMP),
    ('e3d30277-e72b-43a5-8a0e-8511cb299db9', 11.99, 'PAPERBACK'::book_copy_format,
     'd86fc243-e0a7-4112-974e-f00e06efcd2f', '0192a522-534c-4c44-ab64-945295c2c7b1',
     'AVAILABLE', 280, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

-- Customer (for sale references)
INSERT INTO users (id, last_name, first_name, birth_date, email, password, phone, role)
VALUES ('482ceb81-f8e5-46cc-9441-80e923215c5e', 'Reader', 'Sam', '1990-07-15',
        'sam.revenue@reader.mg', 'unused', '+261 32 00 111 22', 'CUSTOMER')
ON CONFLICT (email) DO NOTHING;

-- ============================================================
-- Sales (only SOLD status counts toward revenue)
-- ============================================================

-- Sale 1: Jan 2026 — Fantasy + Science Fiction
INSERT INTO sale (id, sale_date, user_id, status, library_id, created_at)
VALUES ('376b7ba4-742c-4c75-b758-dd4326c0a601',
        '2026-01-15 10:30:00+00',
        '482ceb81-f8e5-46cc-9441-80e923215c5e',
        'SOLD',
        'd86fc243-e0a7-4112-974e-f00e06efcd2f',
        CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

INSERT INTO sale_book_copy (id, book_copy_id, sale_id, price, created_at)
VALUES
    ('75b0077e-5fe3-4a49-87ec-0d90ce741687',
     '9739f81f-f886-4aad-843a-bd81cb998bc4',
     '376b7ba4-742c-4c75-b758-dd4326c0a601',
     12.99, CURRENT_TIMESTAMP),
    ('6090bcf9-a6e6-433f-81ae-43d3bbe6f5c2',
     '57122a10-f07a-4038-83a8-3fa6f8ca5fa6',
     '376b7ba4-742c-4c75-b758-dd4326c0a601',
     14.99, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

-- Sale 2: Mar 2026 — Mystery only
INSERT INTO sale (id, sale_date, user_id, status, library_id, created_at)
VALUES ('d46028c4-c886-402d-aa3b-03ba7e7122ef',
        '2026-03-20 14:00:00+00',
        '482ceb81-f8e5-46cc-9441-80e923215c5e',
        'SOLD',
        'd86fc243-e0a7-4112-974e-f00e06efcd2f',
        CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

INSERT INTO sale_book_copy (id, book_copy_id, sale_id, price, created_at)
VALUES ('9e1c8b29-5cfa-4855-8625-28e37bc26a6c',
        'e3d30277-e72b-43a5-8a0e-8511cb299db9',
        'd46028c4-c886-402d-aa3b-03ba7e7122ef',
        11.99, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

-- Sale 3: Jun 2026 — Fantasy (second copy, same book)
INSERT INTO sale (id, sale_date, user_id, status, library_id, created_at)
VALUES ('d29935c5-b0bf-4463-8400-0ece12c45cef',
        '2026-06-10 09:00:00+00',
        '482ceb81-f8e5-46cc-9441-80e923215c5e',
        'SOLD',
        'd86fc243-e0a7-4112-974e-f00e06efcd2f',
        CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

INSERT INTO sale_book_copy (id, book_copy_id, sale_id, price, created_at)
VALUES ('16b50ebc-fc0e-42f3-b7ed-200fb13d4b0d',
        '9739f81f-f886-4aad-843a-bd81cb998bc4',
        'd29935c5-b0bf-4463-8400-0ece12c45cef',
        12.99, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;
