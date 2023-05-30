CREATE TABLE IF NOT EXISTS reviews_likes
(
    review_id   integer,
    user_id     integer,
    is_positive boolean,
    CONSTRAINT reviews_likes_key PRIMARY KEY (review_id, user_id, is_positive)
);

ALTER TABLE reviews_likes
    ADD FOREIGN KEY (review_id) REFERENCES films (film_id) ON DELETE CASCADE;

ALTER TABLE reviews_likes
    ADD FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE;