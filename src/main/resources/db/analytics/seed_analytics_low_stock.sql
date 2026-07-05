-- Seed data for GET /libraries/{libraryId}/analytics/low-stock
-- Generated with uuidgen
--
-- Self-contained: all required entities (library, book, author, genre,
-- book_copy, users, arrival, sale) are created in this script.
-- No dependency on other seed scripts.
--
-- Stock scenario (arrivals - sold sales):
--   Book1 "The Crystal Compass"
--     PAPERBACK: 1 arrival, 0 sold = 1  (low)
--     HARDCOVER: 3 arrivals, 0 sold = 3  (low, = default threshold)
--     POCKET:    1 arrival, 1 sold = 0  (low)
--   Book2 "Quantum Horizons"
--     PAPERBACK: 2 arrivals, 0 sold = 2  (low)
--     HARDCOVER: 4 arrivals, 0 sold = 4  (not low)
--     POCKET:    1 arrival, 1 sold = 0  (low)
--   Book3 "The Silent Witness"
--     PAPERBACK: 2 arrivals, 0 sold = 2  (low)
--     HARDCOVER: 3 arrivals, 0 sold = 3  (low, = default threshold)
--     POCKET:    2 arrivals, 0 sold = 2  (low)

-- Library
INSERT INTO library (id, name, phone, email, address)
VALUES ('0391f892-a6fc-41d0-8253-925b76bd72a0', 'Low Stock Analytics Library', '+261 34 99 888 77', 'low-stock@analytics.mg', '42 Test Boulevard')
ON CONFLICT DO NOTHING;

-- Genres (re-use UUIDs from seed_genre_for_list.sql to stay consistent)
INSERT INTO genre (id, name)
VALUES
    ('258d3b83-4a83-4138-8070-73c94b92cf86', 'Fantasy'),
    ('b9349104-765d-4965-8100-de8d265b6ef7', 'Science Fiction'),
    ('37149c8b-5691-450c-b753-78cd9867a4a3', 'Mystery')
ON CONFLICT (name) DO NOTHING;

-- Authors
INSERT INTO author (id, first_name, last_name)
VALUES
    ('b9bfe13b-318a-4721-af70-3e1650a5edbb', 'Elena', 'Harper'),
    ('c19c6f39-5f48-4b75-a59e-5e329809114b', 'James', 'Moriarty'),
    ('c9883920-1186-4cdb-98e3-60fc50928a0e', 'Lin', 'Wei')
ON CONFLICT ON CONSTRAINT uq_author_name DO NOTHING;

-- Books
INSERT INTO book (id, title, summary, isbn, publisher, published_at, created_at)
VALUES
    ('1a1019e7-7c60-47d1-b52a-88dfff652e42', 'The Crystal Compass', 'A young mage discovers a compass that points to alternate dimensions', '978-0-00-000001-1', 'Fantasy Press', '2023-03-15', CURRENT_TIMESTAMP),
    ('edc27d44-b8da-4c1b-ba7c-a64e994b46ca', 'Quantum Horizons', 'A physicist unravels the secrets of parallel universes', '978-0-00-000002-2', 'Sci-Fi Publishing', '2024-01-20', CURRENT_TIMESTAMP),
    ('3dbe25a8-86f5-41fd-adec-21e6626c86e2', 'The Silent Witness', 'A detective races to solve a murder before the killer strikes again', '978-0-00-000003-3', 'Crime Books', '2023-09-10', CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

-- Author-book links
INSERT INTO author_book (book_id, author_id)
VALUES
    ('1a1019e7-7c60-47d1-b52a-88dfff652e42', 'b9bfe13b-318a-4721-af70-3e1650a5edbb'),
    ('edc27d44-b8da-4c1b-ba7c-a64e994b46ca', 'c19c6f39-5f48-4b75-a59e-5e329809114b'),
    ('3dbe25a8-86f5-41fd-adec-21e6626c86e2', 'c9883920-1186-4cdb-98e3-60fc50928a0e')
ON CONFLICT DO NOTHING;

-- Book-genre links
INSERT INTO book_genre (book_id, genre_id)
VALUES
    ('1a1019e7-7c60-47d1-b52a-88dfff652e42', '258d3b83-4a83-4138-8070-73c94b92cf86'),
    ('edc27d44-b8da-4c1b-ba7c-a64e994b46ca', 'b9349104-765d-4965-8100-de8d265b6ef7'),
    ('3dbe25a8-86f5-41fd-adec-21e6626c86e2', '37149c8b-5691-450c-b753-78cd9867a4a3')
ON CONFLICT DO NOTHING;

-- Book copies (3 per book, one per format)
INSERT INTO book_copy (id, price, format, library_id, book_id, status, page_number, updated_at)
VALUES
    ('9c66752f-3d05-47d5-a4f9-1db85309cfa4', 12.99, 'PAPERBACK'::book_copy_format, '0391f892-a6fc-41d0-8253-925b76bd72a0', '1a1019e7-7c60-47d1-b52a-88dfff652e42', 'AVAILABLE', 250, CURRENT_TIMESTAMP),
    ('52fd7943-5d10-4263-a7c4-a49b934babfd', 24.99, 'HARDCOVER'::book_copy_format, '0391f892-a6fc-41d0-8253-925b76bd72a0', '1a1019e7-7c60-47d1-b52a-88dfff652e42', 'AVAILABLE', 250, CURRENT_TIMESTAMP),
    ('abbecf98-b8e0-43f2-8b1e-3c832dbc8eba', 8.99,  'POCKET'::book_copy_format,    '0391f892-a6fc-41d0-8253-925b76bd72a0', '1a1019e7-7c60-47d1-b52a-88dfff652e42', 'AVAILABLE', 250, CURRENT_TIMESTAMP),
    ('5d1d867e-7811-4bf5-beb8-7c0759cd2890', 14.99, 'PAPERBACK'::book_copy_format, '0391f892-a6fc-41d0-8253-925b76bd72a0', 'edc27d44-b8da-4c1b-ba7c-a64e994b46ca', 'AVAILABLE', 320, CURRENT_TIMESTAMP),
    ('a1a52827-d9d3-4c0a-b228-d9855be8f14a', 28.99, 'HARDCOVER'::book_copy_format, '0391f892-a6fc-41d0-8253-925b76bd72a0', 'edc27d44-b8da-4c1b-ba7c-a64e994b46ca', 'AVAILABLE', 320, CURRENT_TIMESTAMP),
    ('3b11dd17-4c56-4092-b9aa-8c6fb5df59bb', 9.99,  'POCKET'::book_copy_format,    '0391f892-a6fc-41d0-8253-925b76bd72a0', 'edc27d44-b8da-4c1b-ba7c-a64e994b46ca', 'AVAILABLE', 320, CURRENT_TIMESTAMP),
    ('19f05984-f534-4695-bdad-19c8b393be86', 13.99, 'PAPERBACK'::book_copy_format, '0391f892-a6fc-41d0-8253-925b76bd72a0', '3dbe25a8-86f5-41fd-adec-21e6626c86e2', 'AVAILABLE', 280, CURRENT_TIMESTAMP),
    ('86f44972-63fa-4eed-901c-d114da48a336', 26.99, 'HARDCOVER'::book_copy_format, '0391f892-a6fc-41d0-8253-925b76bd72a0', '3dbe25a8-86f5-41fd-adec-21e6626c86e2', 'AVAILABLE', 280, CURRENT_TIMESTAMP),
    ('b1312162-3c1e-4605-8d95-e65fa9b605ca', 7.99,  'POCKET'::book_copy_format,    '0391f892-a6fc-41d0-8253-925b76bd72a0', '3dbe25a8-86f5-41fd-adec-21e6626c86e2', 'AVAILABLE', 280, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

-- Users (admin + customer, needed for sales)
INSERT INTO users (id, last_name, first_name, birth_date, email, password, phone, role)
VALUES
    ('03cc54b9-2ea5-4b36-aed8-7fbdee0f01cf', 'Admin', 'Jean', '1985-06-15', 'admin@library.mg', 'unused', '+261 34 00 000 99', 'ADMIN'),
    ('fad245e0-3c48-4d63-8ba7-37fea8634a79', 'Reader', 'Alice', '1992-11-03', 'alice@reader.mg', 'unused', '+261 32 00 000 88', 'CUSTOMER')
ON CONFLICT (email) DO NOTHING;

-- ============================================================
-- Arrivals (stock inflows)
-- ============================================================

-- Arrival 1: initial stocking of all 9 copies
INSERT INTO arrival (id, created_at, arrival_date, library_id)
VALUES ('749f1f7c-cbaa-4d2b-93af-62d2eb609952', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '0391f892-a6fc-41d0-8253-925b76bd72a0')
ON CONFLICT DO NOTHING;

INSERT INTO arrival_book_copy (id, arrival_id, book_copy_id, purchase_price, created_at, updated_at)
VALUES
    ('94349427-62e9-444d-81ef-a685d51d5508', '749f1f7c-cbaa-4d2b-93af-62d2eb609952', '9c66752f-3d05-47d5-a4f9-1db85309cfa4', 7.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('39e3c1ff-5d6e-4407-a04b-285c52d44c9c', '749f1f7c-cbaa-4d2b-93af-62d2eb609952', '52fd7943-5d10-4263-a7c4-a49b934babfd', 14.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('d5c10a18-3630-4fbe-834d-8f50c9e754e5', '749f1f7c-cbaa-4d2b-93af-62d2eb609952', 'abbecf98-b8e0-43f2-8b1e-3c832dbc8eba', 4.50, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('390b31b9-ccf5-46b5-ba80-7e3e5c4855da', '749f1f7c-cbaa-4d2b-93af-62d2eb609952', '5d1d867e-7811-4bf5-beb8-7c0759cd2890', 8.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('b4a1b89e-9cca-4efc-892c-13333b2e9ce1', '749f1f7c-cbaa-4d2b-93af-62d2eb609952', 'a1a52827-d9d3-4c0a-b228-d9855be8f14a', 16.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('0bd4c13f-517a-4fcb-8557-f1ac9909fe85', '749f1f7c-cbaa-4d2b-93af-62d2eb609952', '3b11dd17-4c56-4092-b9aa-8c6fb5df59bb', 5.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('7ce80708-a2f3-43e9-9e6e-3d11b9836284', '749f1f7c-cbaa-4d2b-93af-62d2eb609952', '19f05984-f534-4695-bdad-19c8b393be86', 7.50, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('a9a7d7f8-7f98-4405-9ead-8d0934d421c0', '749f1f7c-cbaa-4d2b-93af-62d2eb609952', '86f44972-63fa-4eed-901c-d114da48a336', 15.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('38cf33e9-789d-42ab-8ee6-3638ee5e403d', '749f1f7c-cbaa-4d2b-93af-62d2eb609952', 'b1312162-3c1e-4605-8d95-e65fa9b605ca', 4.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

-- Arrival 2: extra stock for copies needing > 1
INSERT INTO arrival (id, created_at, arrival_date, library_id)
VALUES ('78dc3180-80c3-4d96-ad0f-c440dffd75c9', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '0391f892-a6fc-41d0-8253-925b76bd72a0')
ON CONFLICT DO NOTHING;

INSERT INTO arrival_book_copy (id, arrival_id, book_copy_id, purchase_price, created_at, updated_at)
VALUES
    ('83435c35-f8f2-47ee-84a7-76fe0522f363', '78dc3180-80c3-4d96-ad0f-c440dffd75c9', '52fd7943-5d10-4263-a7c4-a49b934babfd', 14.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('e48a0c30-11b8-4094-8eff-221292b05ca2', '78dc3180-80c3-4d96-ad0f-c440dffd75c9', '5d1d867e-7811-4bf5-beb8-7c0759cd2890', 8.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('53d9735a-8251-4a81-9811-97c8de0bcba6', '78dc3180-80c3-4d96-ad0f-c440dffd75c9', 'a1a52827-d9d3-4c0a-b228-d9855be8f14a', 16.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('39905624-b00a-45f8-964f-c55fba71d2a9', '78dc3180-80c3-4d96-ad0f-c440dffd75c9', '19f05984-f534-4695-bdad-19c8b393be86', 7.50, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('8d7fbf1f-1518-44b4-ba87-67c524c65472', '78dc3180-80c3-4d96-ad0f-c440dffd75c9', '86f44972-63fa-4eed-901c-d114da48a336', 15.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('a636695a-ebb6-412e-ac3b-0cb392c918d8', '78dc3180-80c3-4d96-ad0f-c440dffd75c9', 'b1312162-3c1e-4605-8d95-e65fa9b605ca', 4.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

-- Arrival 3: extra stock for HARDCOVER copies (book 1, 2, 3)
INSERT INTO arrival (id, created_at, arrival_date, library_id)
VALUES ('9272222a-16f4-4378-b94b-432af0918c08', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '0391f892-a6fc-41d0-8253-925b76bd72a0')
ON CONFLICT DO NOTHING;

INSERT INTO arrival_book_copy (id, arrival_id, book_copy_id, purchase_price, created_at, updated_at)
VALUES
    ('8b150878-81b1-44f4-99d1-a092c156c27b', '9272222a-16f4-4378-b94b-432af0918c08', '52fd7943-5d10-4263-a7c4-a49b934babfd', 14.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('ada814ae-5ecd-4e03-be29-aab985414e06', '9272222a-16f4-4378-b94b-432af0918c08', 'a1a52827-d9d3-4c0a-b228-d9855be8f14a', 16.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('468f02c2-c656-4201-a508-2a6b9d635310', '9272222a-16f4-4378-b94b-432af0918c08', '86f44972-63fa-4eed-901c-d114da48a336', 15.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

-- Arrival 4: extra stock for Book2 HARDCOVER only
INSERT INTO arrival (id, created_at, arrival_date, library_id)
VALUES ('c3b8199b-596d-4c8c-96ad-b954ac7b624a', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '0391f892-a6fc-41d0-8253-925b76bd72a0')
ON CONFLICT DO NOTHING;

INSERT INTO arrival_book_copy (id, arrival_id, book_copy_id, purchase_price, created_at, updated_at)
VALUES ('b53068a4-21bd-4ea2-a524-ab3047cdc183', 'c3b8199b-596d-4c8c-96ad-b954ac7b624a', 'a1a52827-d9d3-4c0a-b228-d9855be8f14a', 16.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

-- ============================================================
-- Sales (stock outflows — only POCKET copies sold)
-- ============================================================

INSERT INTO sale (id, sale_date, user_id, status, library_id, created_at)
VALUES ('4bfb2eb1-354a-4e6d-a64a-c933730d3621', CURRENT_TIMESTAMP, 'fad245e0-3c48-4d63-8ba7-37fea8634a79', 'SOLD', '0391f892-a6fc-41d0-8253-925b76bd72a0', CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

INSERT INTO sale_book_copy (id, book_copy_id, sale_id, price, created_at)
VALUES
    ('92554740-2643-4c53-8336-2f6881e4033a', 'abbecf98-b8e0-43f2-8b1e-3c832dbc8eba', '4bfb2eb1-354a-4e6d-a64a-c933730d3621', 8.99, CURRENT_TIMESTAMP),
    ('ca22dc86-3afa-4212-9313-2dbba543eff6', '3b11dd17-4c56-4092-b9aa-8c6fb5df59bb', '4bfb2eb1-354a-4e6d-a64a-c933730d3621', 9.99, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;
