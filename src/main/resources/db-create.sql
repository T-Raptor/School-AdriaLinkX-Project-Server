create table quotes
(
    id    int auto_increment,
    quote varchar(255)
);

create table observables
(
    id  int auto_increment,
    primary key (id)
);

create table stations
(
    observable_id int,
    name varchar(255),
    latitude double,
    longitude double,
    primary key (observable_id),
    foreign key (observable_id) references observables(id),
    unique key (name)
);

create table tracks
(
    observable_id int,
    station1 int,
    station2 int,
    primary key (observable_id),
    foreign key (observable_id) references observables(id),
    foreign key (station1) references stations(observable_id),
    foreign key (station2) references stations(observable_id),
    unique key (station1, station2)
);

create table reservations
(
    observable_id int,
    period_start timestamp,
    period_stop timestamp,
    primary key (observable_id),
    foreign key (observable_id) references observables(id)
);

create table reservation_tracks
(
    reservation int,
    track int,
    primary key (reservation, track),
    foreign key (reservation) references reservations(observable_id),
    foreign key (track) references tracks(observable_id)
);