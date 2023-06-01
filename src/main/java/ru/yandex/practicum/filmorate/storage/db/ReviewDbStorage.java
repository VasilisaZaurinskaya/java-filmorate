package ru.yandex.practicum.filmorate.storage.db;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
@Primary
@AllArgsConstructor
public class ReviewDbStorage implements ReviewStorage {
    public static final String REVIEWS = "reviews";
    private final JdbcTemplate jdbcTemplate;


    @Override
    public List<Review> findAll(Long filmId, Integer count) {
        log.info("Получения списка отзывов для фильма id = {}, с количеством вывода count = {}", filmId, count);

        String sqlQuery = String.format(
                "SELECT r.review_id, " +
                        "r.content, " +
                        "r.is_positive, " +
                        "r.user_id, " +
                        "r.film_id, " +
                        "COALESCE(c.positive_count - c.negative_count, 0) AS useful " +
                        "FROM " + REVIEWS + " AS r " +
                        "LEFT OUTER JOIN (" +
                        "SELECT rl.review_id, " +
                        "SUM(case when rl.is_positive = true then 1 else 0 end)  AS positive_count, " +
                        "SUM(case when rl.is_positive = false then 1 else 0 end) AS negative_count " +
                        "FROM " + REVIEWS + "_likes AS rl " +
                        "GROUP BY rl.review_id " +
                        ") AS c ON c.review_id = r.review_id " +
                        "%s " +
                        "ORDER BY useful DESC " +
                        "LIMIT ?",
                filmId == null ? "" : "WHERE r.film_id = " + filmId
        );


        return jdbcTemplate.query(sqlQuery, this::mapToReviewList, count);
    }

    @Override
    public Review findById(Long id) {
        log.info("Получения отзыва для фильма id = {}", id);

        String sqlQuery = (
                "SELECT r.review_id, " +
                        "r.content, " +
                        "r.is_positive, " +
                        "r.user_id, " +
                        "r.film_id, " +
                        "COALESCE(c.positive_count - c.negative_count, 0) AS useful " +
                        "FROM " + REVIEWS + " AS r " +
                        "LEFT OUTER JOIN (" +
                        "SELECT rl.review_id, " +
                        "SUM(case when rl.is_positive = true then 1 else 0 end)  AS positive_count, " +
                        "SUM(case when rl.is_positive = false then 1 else 0 end) AS negative_count " +
                        "FROM " + REVIEWS + "_likes AS rl " +
                        "GROUP BY rl.review_id " +
                        ") AS c ON c.review_id = r.review_id " +
                        "WHERE r.review_id = ? " +
                        "ORDER BY useful DESC "

        );

        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapToReviewList, id);
        } catch (EmptyResultDataAccessException e) {
            log.info("Отзыв не найден");
            throw new NotFoundException("Отзыв не найден");
        }
    }

    @Override
    public Review create(Review review) {
        log.info("Создание отзыва: {}", review);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sqlQuery = "INSERT INTO " + REVIEWS + " (content, is_positive, user_id, film_id, useful) VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, review.getContent());
            ps.setBoolean(2, review.getIsPositive());
            ps.setLong(3, review.getUserId());
            ps.setLong(4, review.getFilmId());
            ps.setLong(5, 0);

            return ps;
        }, keyHolder);

        Long id = Objects.requireNonNull(keyHolder.getKey()).longValue();

        review.setReviewId(id);

        return review;
    }

    @Override
    public Review update(Review review) {
        log.info("Обновление отзыва с id = {}", review.getReviewId());

        String sqlQuery = "UPDATE " + REVIEWS + " SET content = ?, is_positive = ? WHERE review_id = ?";

        jdbcTemplate.update(
                sqlQuery,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId()
        );

        return findById(review.getReviewId());
    }

    @Override
    public String delete(Long id) {
        log.info("Удаление отзыва с id = {}", id);

        String sqlQuery = "DELETE FROM " + REVIEWS + " WHERE review_id = ?";

        return jdbcTemplate.update(sqlQuery, id) > 0 ? "Отзыв удален" : "Не чего удалять!";
    }

    @Override
    public void addLike(Long reviewId, Long userId) {
        log.info("Добавление лайка отзыву с id = {}, пользователем с id = {}", reviewId, userId);
        String sqlQuery = "INSERT INTO " + REVIEWS + "_likes (review_id, user_id, is_positive) VALUES (?, ?, true)";

        jdbcTemplate.update(sqlQuery, reviewId, userId);
    }

    @Override
    public void deleteLike(Long reviewId, Long userId) {
        log.info("Удаление лайка отзыву с id = {}, пользователем с id = {}", reviewId, userId);
        String sqlQuery = "DELETE FROM " + REVIEWS + "_likes WHERE review_id = ? AND user_id = ? AND is_positive = true";

        jdbcTemplate.update(sqlQuery, reviewId, userId);
    }

    @Override
    public void addDislike(Long reviewId, Long userId) {
        log.info("Добавление дизлайка отзыву с id = {}, пользователем с id = {}", reviewId, userId);
        String sqlQuery = "INSERT INTO " + REVIEWS + "_likes (review_id, user_id, is_positive) VALUES (?, ?, false)";

        jdbcTemplate.update(sqlQuery, reviewId, userId);
    }

    @Override
    public void deleteDislike(Long reviewId, Long userId) {
        log.info("Удаление дизлайка отзыву с id = {}, пользователем с id = {}", reviewId, userId);
        String sqlQuery = "DELETE FROM " + REVIEWS + "_likes WHERE review_id = ? AND user_id = ? AND is_positive = false";

        jdbcTemplate.update(sqlQuery, reviewId, userId);
    }

    private Review mapToReviewList(ResultSet resultSet, int rowNum) throws SQLException {
        return mapToReview(resultSet);
    }

    private Review mapToReview(ResultSet resultSet) throws SQLException {
        return Review
                .builder()
                .reviewId(resultSet.getLong("review_id"))
                .isPositive(resultSet.getBoolean("is_positive"))
                .content(resultSet.getString("content"))
                .filmId(resultSet.getLong("film_id"))
                .userId(resultSet.getLong("user_id"))
                .useful(resultSet.getInt("useful"))
                .build();
    }
}
