DELETE FROM jobs;

ALTER TABLE jobs AUTO_INCREMENT = 1;

INSERT INTO jobs (start_time, end_time, machine_id, is_dryer, program_id, recipe_id)
VALUES
    ('2024-12-01T08:00:00', '2024-12-01T12:00:00', 1, true, 1, 1),
    ('2024-12-02T09:00:00', '2024-12-02T13:00:00', 2, false, 2, 2),
    ('2024-12-03T10:00:00', '2024-12-03T14:00:00', 3, true, 3, 3),
    ('2024-12-04T14:30:00', '2024-12-04T18:00:00', 1, false, 1, 4),
    ('2024-12-05T15:00:00', '2024-12-05T19:00:00', 2, true, 2, 1),
    ('2024-12-06T16:00:00', '2024-12-06T20:00:00', 3, false, 3, 2);