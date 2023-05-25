package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.storage.FeedsStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;


import java.util.List;

@Service
@Slf4j
public class FeedService {
    public static final String FRIEND = "FRIEND";
    public static final String LIKE = "LIKE";
    public static final String REVIEW = "REVIEW";

    public static final String ADD = "ADD";
    public static final String DELETE = "DELETE";

    public static final String UPDATE = "UPDATE";

    private final FeedsStorage feedsStorage;
    private final UserStorage userStorage;

    @Autowired
    public FeedService(FeedsStorage feedsStorage, UserStorage userStorage) {
        this.feedsStorage = feedsStorage;
        this.userStorage = userStorage;
    }


    public void addFriend(Long id, Long friendId) {
        log.info("Пользователь {} добавил друга", id);
        Feed feed = Feed.builder()
                .entityId(id)
                .eventType(FRIEND)
                .operation(ADD)
                .build();

        feedsStorage.save(feed);

    }


    public void deleteFriend(Long id, Long friendId) {
        log.info("Пользователь {} удалил друга", id);
        Feed feed = Feed.builder()
                .entityId(id)
                .eventType(FRIEND)
                .operation(DELETE)
                .build();

        feedsStorage.save(feed);
    }

    public void addLike(Long filmId, Long userId) {
        log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);
        Feed feed = Feed.builder()
                .eventId(userId)
                .eventType(LIKE)
                .operation(ADD)
                .build();

        feedsStorage.save(feed);
    }

    public void removeLike(Long userId, Long filmId) {
        log.info("Пользователь {} удалил лайк у фильма {}", userId, filmId  );
        Feed feed = Feed.builder()
                .userId(userId)
                .eventType(LIKE)
                .operation(DELETE)
                .entityId(filmId)
                .build();

        feedsStorage.save(feed);
    }
    public List<Feed> getFeedByUserId(Long userId){
        return feedsStorage.getFeedByUserId(userId);
    }

}
