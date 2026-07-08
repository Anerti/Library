-- Seed data for GET /libraries/{libraryId}/analytics/stock/{bookId}
-- Generated with uuidgen
--
-- Scenario:
--   Library has 1 book with 3 copies (one per format).
--   All 3 copies arrived, 1 (PAPERBACK) was sold.
-- Expected: ALL=4, PAPERBACK=1, HARDCOVER=2, POCKET=1

-- Library
INSERT INTO library (id, name, phone, email, address)
VALUES ('39795205-ffbc-4ace-92e4-925a8c413a88', 'Seed Library Analytics', '+261 34 00 000 01', 'seed-analytics@library.mg', '123 Test Avenue')
ON CONFLICT DO NOTHING;

-- Book
INSERT INTO book (id, title, summary, isbn, publisher, published_at, created_at)
VALUES ('bb85ea41-0baa-4392-a874-747be604f868', 'Stock Test Book', 'A book for testing stock analytics', '979-0-00-000000-1', 'Test Publisher', '2024-01-15', CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

-- Customer (for sale)
INSERT INTO users (id, last_name, first_name, birth_date, email, password, phone, role)
VALUES ('8816e5d0-8de4-4abf-8a60-9e26423f0bb4', 'Test', 'Customer', '1990-01-01', 'stock-test@library.mg', 'unused', '+261 00 000 00', 'CUSTOMER')
ON CONFLICT (email) DO NOTHING;

-- Book copies (one per format)
INSERT INTO book_copy (id, price, format, library_id, book_id, status, page_number, updated_at)
VALUES
    ('a0d09e50-dfe9-4a9f-9c8b-fcce206b341f', 15.00, 'PAPERBACK'::book_copy_format, '39795205-ffbc-4ace-92e4-925a8c413a88', 'bb85ea41-0baa-4392-a874-747be604f868', 'AVAILABLE', 200, CURRENT_TIMESTAMP),
    ('396a01ee-af05-4758-89aa-5c12ba21c0fe', 25.00, 'HARDCOVER'::book_copy_format, '39795205-ffbc-4ace-92e4-925a8c413a88', 'bb85ea41-0baa-4392-a874-747be604f868', 'AVAILABLE', 200, CURRENT_TIMESTAMP),
    ('84efb592-1158-4019-8aa2-45fdb9d6d94f', 10.00, 'POCKET'::book_copy_format, '39795205-ffbc-4ace-92e4-925a8c413a88', 'bb85ea41-0baa-4392-a874-747be604f868', 'AVAILABLE', 150, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

-- First arrival (initial stock)
INSERT INTO arrival (id, created_at, arrival_date, library_id)
VALUES ('d1dfdcc5-7164-4ebc-a7e3-dc5ec6ae9d13', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '39795205-ffbc-4ace-92e4-925a8c413a88')
ON CONFLICT DO NOTHING;

INSERT INTO arrival_book_copy (id, arrival_id, book_copy_id, purchase_price, created_at, updated_at)
VALUES
    ('4bc21821-23aa-4263-8314-de31eae01d53', 'd1dfdcc5-7164-4ebc-a7e3-dc5ec6ae9d13', 'a0d09e50-dfe9-4a9f-9c8b-fcce206b341f', 8.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('388a3ddb-8359-4246-a487-01bd622045b1', 'd1dfdcc5-7164-4ebc-a7e3-dc5ec6ae9d13', '396a01ee-af05-4758-89aa-5c12ba21c0fe', 14.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('239aa285-f64c-4815-bfa7-7195b63ca50f', 'd1dfdcc5-7164-4ebc-a7e3-dc5ec6ae9d13', '84efb592-1158-4019-8aa2-45fdb9d6d94f', 5.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('6e671f5c-2c63-4e3e-981d-14775958f12f', 'd1dfdcc5-7164-4ebc-a7e3-dc5ec6ae9d13', 'a0d09e50-dfe9-4a9f-9c8b-fcce206b341f', 8.25, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('5e15055f-54b5-4268-9b23-e96a119cf668', 'd1dfdcc5-7164-4ebc-a7e3-dc5ec6ae9d13', '84efb592-1158-4019-8aa2-45fdb9d6d94f', 5.25, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

-- First sale (PAPERBACK copy sold)
INSERT INTO sale (id, sale_date, user_id, status, library_id, created_at)
VALUES ('ec3b77a7-0b96-442e-a5a9-096c6e5e4ee7', CURRENT_TIMESTAMP, '8816e5d0-8de4-4abf-8a60-9e26423f0bb4', 'SOLD', '39795205-ffbc-4ace-92e4-925a8c413a88', CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

INSERT INTO sale_book_copy (id, book_copy_id, sale_id, price, created_at)
VALUES
    ('66f97ba4-98a6-4cc2-bcc9-f75dbb8fc8f1', 'a0d09e50-dfe9-4a9f-9c8b-fcce206b341f', 'ec3b77a7-0b96-442e-a5a9-096c6e5e4ee7', 15.00, CURRENT_TIMESTAMP),
    ('e85e92dc-51ea-4583-9e3e-4d7407ea4b57', 'a0d09e50-dfe9-4a9f-9c8b-fcce206b341f', 'ec3b77a7-0b96-442e-a5a9-096c6e5e4ee7', 16.00, CURRENT_TIMESTAMP),
    ('dc137745-96a8-49e2-8463-3134f64c99a6', '84efb592-1158-4019-8aa2-45fdb9d6d94f', 'ec3b77a7-0b96-442e-a5a9-096c6e5e4ee7', 10.50, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

-- Second arrival (restock all 3 copies)
INSERT INTO arrival (id, created_at, arrival_date, library_id)
VALUES ('c927d042-76da-4366-a8ae-aa7f2248576f', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '39795205-ffbc-4ace-92e4-925a8c413a88')
ON CONFLICT DO NOTHING;

INSERT INTO arrival_book_copy (id, arrival_id, book_copy_id, purchase_price, created_at, updated_at)
VALUES
    ('b9ebe6cc-74cf-4a12-a8a1-88bc007c6283', 'c927d042-76da-4366-a8ae-aa7f2248576f', 'a0d09e50-dfe9-4a9f-9c8b-fcce206b341f', 8.50, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('1581f946-19aa-41b1-b3c1-767b672a599c', 'c927d042-76da-4366-a8ae-aa7f2248576f', '396a01ee-af05-4758-89aa-5c12ba21c0fe', 14.50, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('fd785a43-3a6a-43a2-9da9-ec98ea8c8ae7', 'c927d042-76da-4366-a8ae-aa7f2248576f', '84efb592-1158-4019-8aa2-45fdb9d6d94f', 5.50, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

-- Second sale (POCKET copy sold)
INSERT INTO sale (id, sale_date, user_id, status, library_id, created_at)
VALUES ('03e1f8c3-b676-4980-b006-5e72eaad7334', CURRENT_TIMESTAMP, '8816e5d0-8de4-4abf-8a60-9e26423f0bb4', 'SOLD', '39795205-ffbc-4ace-92e4-925a8c413a88', CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

INSERT INTO sale_book_copy (id, book_copy_id, sale_id, price, created_at)
VALUES ('ce8ecd62-4c6f-490f-8fd9-591511b55151', '84efb592-1158-4019-8aa2-45fdb9d6d94f', '03e1f8c3-b676-4980-b006-5e72eaad7334', 10.00, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;
