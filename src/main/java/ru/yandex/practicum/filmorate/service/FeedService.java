package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.storage.FeedsStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;


import java.util.Date;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class FeedService {

    private final FeedsStorage feedsStorage;
    private final UserStorage userStorage;


    public void addFriend(Long userId, Long friendId) {
        log.info("Пользователь {} добавил друга {}", userId, friendId);
        Feed feed = Feed.builder()
                .userId(userId)
                .entityId(friendId)
                .eventType(EventType.FRIEND.name())
                .operation(Operation.ADD.name())
                .timestamp(new Date().getTime())
                .build();

        feedsStorage.save(feed);

    }


    public void deleteFriend(Long userId, Long friendId) {
        log.info("Пользователь {} удалил друга", userId);
        Feed feed = Feed.builder()
                .userId(userId)
                .entityId(friendId)
                .eventType(EventType.FRIEND.name())
                .operation(Operation.REMOVE.name())
                .timestamp(new Date().getTime())
                .build();

        feedsStorage.save(feed);
    }

    public void addLike(Long userId, Long filmId) {
        log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);
        Feed feed = Feed.builder()
                .userId(userId)
                .entityId(filmId)
                .eventType(EventType.LIKE.name())
                .operation(Operation.ADD.name())
                .timestamp(new Date().getTime())
                .build();

        feedsStorage.save(feed);
    }

    public void removeLike(Long userId, Long filmId) {
        log.info("Пользователь {} удалил лайк у фильма {}", userId, filmId);
        Feed feed = Feed.builder()
                .userId(userId)
                .eventType(EventType.LIKE.name())
                .operation(Operation.REMOVE.name())
                .entityId(filmId)
                .timestamp(new Date().getTime())
                .build();

        feedsStorage.save(feed);
    }

    public void addReview(Long userId, Long reviewId) {
        log.info("Пользователь {} добавил отзыв {}", userId, reviewId);
        Feed feed = Feed.builder()
                .userId(userId)
                .entityId(reviewId)
                .eventType(EventType.REVIEW.name())
                .operation(Operation.ADD.name())
                .timestamp(new Date().getTime())
                .build();

        feedsStorage.save(feed);
    }


    public void updateReview(Long userId, Long reviewId) {
        log.info("Пользователь {} обновил отзыв {}", userId, reviewId);
        Feed feed = Feed.builder()
                .userId(userId)
                .entityId(reviewId)
                .eventType(EventType.REVIEW.name())
                .operation(Operation.UPDATE.name())
                .timestamp(new Date().getTime())
                .build();

        feedsStorage.save(feed);
    }

    public void removeReview(Long reviewId, Long userId) {
        log.info("Пользователь {} удалил отзыв {}", userId, reviewId);
        Feed feed = Feed.builder()
                .userId(userId)
                .entityId(reviewId)
                .eventType(EventType.REVIEW.name())
                .operation(Operation.REMOVE.name())
                .timestamp(new Date().getTime())
                .build();

        feedsStorage.save(feed);
    }

    public List<Feed> getFeedByUserId(Long userId) {
        return feedsStorage.getFeedByUserId(userId);
    }

}
