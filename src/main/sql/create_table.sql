drop database logistism;
create database logistism;

use logistism;
create table location
(
    id       int not null primary key auto_increment,
    name     varchar(50),
    province varchar(50),
    city     varchar(50),
    district varchar(50),
    street   varchar(100),
    address  varchar(100) # detailed address
);



create table repository
(
    id          int not null primary key auto_increment,
    name        varchar(50),
    location_id int references location
);


create table shipment_schedule
(
    shipment_id     int primary key,
    description     varchar(50),
    transportation  varchar(50),
    source_id       int references repository (id),
    destination_id  int references repository (id),
    time_cost       int,      # seconds
    repetition_type char(10), # week, day, hour, minute(only for test)
    repetition_time timestamp default 0 # the time of repetition
);

create table shipment
(
    id                   int not null primary key auto_increment,
    shipment_schedule_id int,
    description          varchar(50),
    transportation       varchar(50),
    source_id            int references repository (id),
    destination_id       int references repository (id),
    departure_time       timestamp default 0,
    arrival_time         timestamp default 0,
    state                smallint default 0 # 0: not departured, 1: departured, 2: arrived,
);

create table user
(
    username varchar(50) primary key not null, # unique username
    password text(256)               not null, # SHA2(_,  256)
    nickname char(50)                not null
);

drop table if exists manager;
create table manager
(
    username varchar(50) primary key references user (username),
    level    int
);
drop table if exists customer;
create table customer
(
    username varchar(50) primary key references user (username)
);

drop table if exists postman;
create table postman
(
    username  varchar(50) primary key references user (username),
    real_name varchar(50),
    phone     varchar(50)
#     citizen_id varchar(32) # 身份证号
);
create table customer_endpoint
(
    customer    varchar(50) references customer (username),
    endpoint_id int references endpoint (id),
    priority    smallint default 1
);
drop table if exists endpoint;
create table endpoint
(
    id          int not null primary key auto_increment,
    location    int references location (id),
    name        varchar(50), # the name of receiver/deliver
    phone       varchar(50), # the phone number of receiver/deliver
    description varchar(100)
);

create table `order`
(
    id            int       not null primary key auto_increment,
    item_name     varchar(50),
    customer      varchar(50) references customer (username),
    source        int references endpoint (id), # the sender
    destination   int references endpoint (id), # the receiver
    creation_date timestamp default 0,
    state         smallint,                     # 0: created but not retrieved, 1: on delivery, 2: finished
    remark        varchar(100)
);
drop table if exists storage;
create table storage
(
    `order` int primary key references `order` (id),
    ref_id  int,
    type    smallint # 0: repo, 1: transportation
);
drop table if exists shipping_plan;
create table shipping_plan
(
    id          int not null primary key auto_increment,
    shipment_id int references shipment (id),
    order_id    int references `order` (id)
);

create table transfer_log
(
    id         int not null primary key auto_increment,
    repository int references repository (id),
    shipment   int references shipment (id),
    order_id   int references `order` (id),
    type       smallint, # 0 : store, 1: retrieve
    time       timestamp
);


drop table if exists posting_plan;
create table posting_plan # retrieving & delivery
(
    id         int      not null primary key auto_increment,
    order_id   int      not null references `order` (id),
    postman    varchar(50) references postman (username),
    repository int      not null references repository (id),
    type       smallint not null # 0: retrieving, 1: delivery
);
drop table if exists posting_log;
create table posting_log # retrieving & delivery
(
    id       int not null primary key auto_increment,
    order_id int references `order` (id),
    postman  varchar(50) references postman (username),
    time     timestamp ,
    type     smallint # 0: retrieving, 1: delivery
);
drop table if exists postman_repo;
create table postman_repo # describe which postman belongs to which repository
(
    id int primary key auto_increment,
    postman    varchar(50) references postman (username),
    repository int references repository (id),
    unique (postman, repository)
);