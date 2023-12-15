INSERT INTO quotes VALUES (0, 'You must accept that you might fail.');
INSERT INTO quotes VALUES (1, 'Everyone enjoys doing the kind of work for which he is best suited.');

INSERT INTO observables VALUES (1);
INSERT INTO stations VALUES (SCOPE_IDENTITY(), 'Adria', 50.85292760248162, 4.351725442466426);

INSERT INTO observables VALUES (2);
INSERT INTO stations VALUES (SCOPE_IDENTITY(), 'Bdria', 47.03051037331985, 2.286659149568905);

INSERT INTO observables VALUES (3);
INSERT INTO stations VALUES (SCOPE_IDENTITY(), 'Cdria', 49.817127661174844, 14.63993873625563);

INSERT INTO observables VALUES (4);
INSERT INTO tracks VALUES (SCOPE_IDENTITY(), 1, 2);

INSERT INTO observables VALUES (5);
INSERT INTO tracks VALUES (SCOPE_IDENTITY(), 2, 3);

INSERT INTO observables VALUES (6);
INSERT INTO tracks VALUES (SCOPE_IDENTITY(), 1, 3);

INSERT INTO observables VALUES (7);
INSERT INTO reservations VALUES (SCOPE_IDENTITY(), '2022-05-08 14:30:00', '2022-05-08 18:30:00', 'Hoogle');
INSERT INTO reservation_tracks VALUES (SCOPE_IDENTITY(), 4);
INSERT INTO reservation_tracks VALUES (SCOPE_IDENTITY(), 5);

INSERT INTO observables VALUES (8);
INSERT INTO reservations VALUES (SCOPE_IDENTITY(), '2022-05-12 9:30:00', '2022-05-09 11:30:00', 'Macrosoft');
INSERT INTO reservation_tracks VALUES (SCOPE_IDENTITY(), 6);

INSERT INTO observables VALUES (9);
INSERT INTO shuttles VALUES (SCOPE_IDENTITY(), 'AEFF-B217-A4FB');

INSERT INTO observables VALUES (10);
INSERT INTO shuttles VALUES (SCOPE_IDENTITY(), 'A21F-2936-24AC');

INSERT INTO events (target, moment, class, reason) VALUES (4, '2022-04-12 9:30:10', 'WARN', 'Moose sighted');
INSERT INTO events (target, moment, class, reason) VALUES (5, '2022-05-12 9:40:09', 'BREAK', 'Giant rock');

INSERT INTO events (target, moment, class) VALUES (9, '2022-05-14 10:30:21', 'MOVE');
INSERT INTO events_local VALUES (SCOPE_IDENTITY(), 3.583005176613367, 49.366870654264495);

INSERT INTO events (target, moment, class) VALUES (10, '2022-05-17 10:30:21', 'MOVE');
INSERT INTO events_local VALUES (SCOPE_IDENTITY(), 13.583005176613367, 49.366870654264495);