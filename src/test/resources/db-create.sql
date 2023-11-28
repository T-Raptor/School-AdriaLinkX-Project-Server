drop table if exists quotes;
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
    foreign key (observable_id) references observables(id)
);