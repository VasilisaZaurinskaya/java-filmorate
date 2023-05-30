--Связь режиссёров и фильмов
drop table if exists films_director CASCADE;

CREATE TABLE IF NOT EXISTS films_director
(
    film_id integer,
    director_id integer,
    CONSTRAINT likes_key PRIMARY KEY (film_id, director_id)
);

ALTER TABLE films_director
    ADD FOREIGN KEY (film_id) REFERENCES films (film_id) ON DELETE CASCADE;

ALTER TABLE films_director
    ADD FOREIGN KEY (director_id) REFERENCES directors (director_id) ON DELETE CASCADE;