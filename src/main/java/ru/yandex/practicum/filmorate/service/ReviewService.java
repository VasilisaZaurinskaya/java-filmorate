package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.FeedsStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final FeedService feedService;



    public List<Review> findAll(Optional<Long> filmId, Optional<Integer> count) {
        return reviewStorage.findAll(filmId.orElse(null), count.orElse(10));
    }

    public Review findById(Long id) {
        if (id < 0) {
            throw new NotFoundException("Нет отзыва с id = " + id);
        }

        return reviewStorage.findById(id);
    }

    public Review create(Review review) {
        if (review.getUserId() < 0) {
            throw new NotFoundException("Нет такого пользователя");
        }

        if (review.getFilmId() < 0) {
            throw new NotFoundException("Нет такого фильма");
        }
        feedService.addReview(review.getReviewId(),review.getUserId());
        return reviewStorage.create(review);
    }

    public Review update(Review review) {
        feedService.updateReview(review.getReviewId(),review.getUserId());
        return reviewStorage.update(review);

    }

    public String delete(Long id) {

        return reviewStorage.delete(id);
    }

    public void addLike(Long reviewId, Long userId) {
        reviewStorage.addLike(reviewId, userId);
    }

    public void deleteLike(Long reviewId, Long userId) {
        reviewStorage.deleteLike(reviewId, userId);
    }

    public void addDislike(Long reviewId, Long userId) {
        reviewStorage.addDislike(reviewId, userId);
    }

    public void deleteDislike(Long reviewId, Long userId) {
        reviewStorage.deleteDislike(reviewId, userId);
    }
}
