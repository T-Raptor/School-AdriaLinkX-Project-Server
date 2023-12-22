create table observables
(
    id  int auto_increment,
    subtype varchar(50),
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
    company VARCHAR(255),
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

create table shuttles
(
    observable_id int,
    serial VARCHAR(25),
    primary key (observable_id),
    foreign key (observable_id) references observables(id)
);

create table events
(
    id int auto_increment not null,
    target int not null,
    moment timestamp not null,
    class varchar(50) not null,
    reason varchar(255) null,

    local boolean not null default false,
    latitude double null,
    longitude double null,

    primary key (id),
    foreign key (target) references observables(id)
);

create table notifications
(
    event int not null,
    company varchar(255) not null,
    read boolean not null default false,
    primary key (event, company),
    foreign key (event) references events(id)
);