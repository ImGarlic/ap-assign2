-- we don't know how to generate root <with-no-name> (class Root) :(
create table User
(
    id         INTEGER           not null
        constraint key_name
            primary key autoincrement,
    user_name  text(0, 255)      not null
        unique,
    first_name text(0, 255)      not null,
    last_name  text(0, 255)      not null,
    password   text(0, 255)      not null,
    VIP        INTEGER default 0 not null
);

create table Post
(
    id        INTEGER      not null
        constraint key_name
            primary key autoincrement,
    author    text(0, 255) not null,
    content   text(0, 255) not null,
    likes     INTEGER      not null,
    shares    INTEGER      not null,
    timestamp INTEGER      not null,
    user      INTEGER      not null
        constraint FK_User_Id
            references User
            on delete cascade
);

