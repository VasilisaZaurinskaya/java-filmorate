package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {
    Review findById(Long id);

    List<Review> findAll(Long aLong, Integer integer);

    Review create(Review review);

    Review update(Review review);

    String delete(Long id);

    void addLike(Long reviewId, Long userId);

    void deleteLike(Long reviewId, Long userId);

    void addDislike(Long reviewId, Long userId);

    void deleteDislike(Long reviewId, Long userId);
}
