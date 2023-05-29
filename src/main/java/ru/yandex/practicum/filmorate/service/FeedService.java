package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.storage.FeedsStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;


import java.util.Date;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class FeedService {
    public static final String FRIEND = "FRIEND";
    public static final String LIKE = "LIKE";
    public static final String REVIEW = "REVIEW";

    public static final String ADD = "ADD";
    public static final String REMOVE = "REMOVE";

    public static final String UPDATE = "UPDATE";

    private final FeedsStorage feedsStorage;
    private final UserStorage userStorage;


    public void addFriend(Long userId, Long friendId) {
        log.info("Пользователь {} добавил друга {}", userId, friendId);
        Feed feed = Feed.builder()
                .userId(userId)
                .entityId(friendId)
                .eventType(FRIEND)
                .operation(ADD)
                .timestamp(new Date().getTime())
                .build();

        feedsStorage.save(feed);

    }


    public void deleteFriend(Long userId, Long friendId) {
        log.info("Пользователь {} удалил друга", userId);
        Feed feed = Feed.builder()
                .userId(userId)
                .entityId(friendId)
                .eventType(FRIEND)
                .operation(REMOVE)
                .timestamp(new Date().getTime())
                .build();

        feedsStorage.save(feed);
    }

    public void addLike(Long userId, Long filmId) {
        log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);
        Feed feed = Feed.builder()
                .userId(userId)
                .eventId(filmId)
                .eventType(LIKE)
                .operation(ADD)
                .timestamp(new Date().getTime())
                .build();

        feedsStorage.save(feed);
    }

    public void removeLike(Long userId, Long filmId) {
        log.info("Пользователь {} удалил лайк у фильма {}", userId, filmId);
        Feed feed = Feed.builder()
                .userId(userId)
                .eventType(LIKE)
                .operation(REMOVE)
                .entityId(filmId)
                .timestamp(new Date().getTime())
                .build();

        feedsStorage.save(feed);
    }

    public void addReview(Long userId, Long reviewId) {
        log.info("Пользователь {} добавил отзыв {}", userId, reviewId);
        Feed feed = Feed.builder()
                .userId(userId)
                .eventType(REVIEW)
                .operation(ADD)
                .eventId(reviewId)
                .timestamp(new Date().getTime())
                .build();

        feedsStorage.save(feed);
    }

    public void removeReview(Long reviewId, Long userId) {
        log.info("Пользователь {} удалил отзыв {}", userId, reviewId);
        Feed feed = Feed.builder()
                .userId(userId)
                .eventType(REVIEW)
                .operation(REMOVE)
                .eventId(reviewId)
                .timestamp(new Date().getTime())
                .build();

        feedsStorage.save(feed);
    }

    public void updateReview(Long userId, Long reviewId) {
        log.info("Пользователь {} обновил отзыв {}", userId, reviewId);
        Feed feed = Feed.builder()
                .userId(userId)
                .eventType(REVIEW)
                .operation(UPDATE)
                .eventId(reviewId)
                .timestamp(new Date().getTime())
                .build();

        feedsStorage.save(feed);
    }

    public List<Feed> getFeedByUserId(Long userId) {
        return feedsStorage.getFeedByUserId(userId);
    }

}
