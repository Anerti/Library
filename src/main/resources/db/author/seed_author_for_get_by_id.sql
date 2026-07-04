INSERT INTO author (id, first_name, last_name)
VALUES ('81bfef1d-f921-4c1c-b3fd-77258c269ad3', 'Franz', 'Kafka'),
       ('5fb6d6fe-9c70-4818-b500-6e7aabeb276d', 'Virginia', 'Woolf'),
       ('595e6446-cac7-43c0-b737-8e6fb0e87c22', 'Gabriel', 'García Márquez')
ON CONFLICT DO NOTHING;
