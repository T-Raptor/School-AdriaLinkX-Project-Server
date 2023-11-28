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
    station1 varchar(255),
    station2 varchar(255),
    primary key (observable_id),
    foreign key (station1) references stations(name),
    foreign key (station2) references stations(name),
    unique key (station1, station2)
);