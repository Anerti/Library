INSERT INTO genre (id, name)
VALUES ('c5c659bb-5670-4409-8e4d-b02678ac8e72', 'ExistingGenreForConflict'),
       ('0be3aa75-2757-4eae-941c-081abbdc32c5', 'GenreForPatchSuccess')
ON CONFLICT DO NOTHING;
