INSERT INTO genre (id, name)
VALUES ('258d3b83-4a83-4138-8070-73c94b92cf86', 'Fantasy'),
       ('b9349104-765d-4965-8100-de8d265b6ef7', 'Science Fiction'),
       ('37149c8b-5691-450c-b753-78cd9867a4a3', 'Mystery'),
       ('a1f03fe3-74e3-4d0e-be9b-e087d95586a7', 'Romance'),
       ('37c83c33-01ad-4059-8d95-9d15c71f68a0', 'Thriller'),
       ('1660742c-0717-48d9-8d79-08cee03d85a9', 'Horror'),
       ('9f9cb019-29ab-4742-a85d-6074a15f1cb0', 'Historical Fiction'),
       ('fcfba7ab-9276-47d9-ad8e-208785a70aaa', 'Biography'),
       ('c5254d87-a617-4f19-9ad4-204dfbee7ef4', 'Non-Fiction'),
       ('5686a96d-ac46-4d4b-9674-724223371ffa', 'Poetry') ON CONFLICT DO NOTHING;
