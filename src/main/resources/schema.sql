drop table filmes, users, friends, likes, genres, status_friendship;

CREATE TABLE IF NOT EXISTS filmes
(
    film_id      integer PRIMARY KEY,
    name         varchar,
    description  varchar,
    release_date date,
    duration     integer,
    genre_id     integer,
    mpa_rating   varchar
);

CREATE TABLE IF NOT EXISTS users
(
    user_id  integer PRIMARY KEY,
    email    varchar,
    login    varchar,
    name     varchar,
    birthday date
);

CREATE TABLE IF NOT EXISTS friends
(
    friend_id  integer PRIMARY KEY,
    user_id    integer,
    frienship  integer
);

CREATE TABLE IF NOT EXISTS likes
(
    user_id integer,
    film_id integer
);

CREATE TABLE IF NOT EXISTS genres
(
    genre_id integer PRIMARY KEY,
    name     varchar
);

CREATE TABLE IF NOT EXISTS status_friendship
(
    status_friendship_id integer PRIMARY KEY,
    status               varchar
);


ALTER TABLE friends
    ADD FOREIGN KEY (user_id) REFERENCES users (user_id);

ALTER TABLE users
    ADD FOREIGN KEY (user_id) REFERENCES friends (friend_id);

ALTER TABLE filmes
    ADD FOREIGN KEY (genre_id) REFERENCES genres (genre_id);

ALTER TABLE likes
    ADD FOREIGN KEY (user_id) REFERENCES users (user_id);

ALTER TABLE likes
    ADD FOREIGN KEY (film_id) REFERENCES filmes (film_id);

ALTER TABLE friends
    ADD FOREIGN KEY (frienship) REFERENCES status_friendship (status_friendship_id);