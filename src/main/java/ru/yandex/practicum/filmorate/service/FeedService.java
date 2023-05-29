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


    public void addFriend(Long id, Long friendId) {
        log.info("Пользователь {} добавил друга {}", id, friendId);
        Feed feed = Feed.builder()
                .userId(id)
                .entityId(friendId)
                .eventType(FRIEND)
                .operation(ADD)
                .timestamp(new Date().getTime())
                .build();

        feedsStorage.save(feed);

    }


    public void deleteFriend(Long id, Long friendId) {
        log.info("Пользователь {} удалил друга", id);
        Feed feed = Feed.builder()
                .userId(id)
                .entityId(friendId)
                .eventType(FRIEND)
                .operation(REMOVE)
                .timestamp(new Date().getTime())
                .build();

        feedsStorage.save(feed);
    }

    public void addLike(Long filmId, Long userId) {
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

    public void addReview(Long id, Long userId) {
        log.info("Пользователь {} добавил отзыв {}", userId, id);
        Feed feed = Feed.builder()
                .userId(userId)
                .eventType(REVIEW)
                .operation(ADD)
                .eventId(id)
                .timestamp(new Date().getTime())
                .build();

        feedsStorage.save(feed);
    }

    public void removeReview(Long id, Long userId) {
        log.info("Пользователь {} удалил отзыв {}", userId, id);
        Feed feed = Feed.builder()
                .userId(userId)
                .eventType(REVIEW)
                .operation(REMOVE)
                .eventId(id)
                .timestamp(new Date().getTime())
                .build();

        feedsStorage.save(feed);
    }

    public void updateReview(Long id, Long userId) {
        log.info("Пользователь {} обновил отзыв {}", userId, id);
        Feed feed = Feed.builder()
                .userId(userId)
                .eventType(REVIEW)
                .operation(UPDATE)
                .eventId(id)
                .timestamp(new Date().getTime())
                .build();

        feedsStorage.save(feed);
    }

    public List<Feed> getFeedByUserId(Long userId) {
        return feedsStorage.getFeedByUserId(userId);
    }

}
