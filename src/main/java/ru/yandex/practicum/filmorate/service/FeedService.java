package ru.yandex.practicum.filmorate.service;

import liquibase.pro.packaged.F;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FeedsStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
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


    public void addFriend(User user, User userFriend) {
        log.info("Пользователь {} добавил друга", user);
        Feed feed = Feed.builder()
                .entityId(user.getId())
                .eventType(FRIEND)
                .operation(ADD)
                .build();

        feedsStorage.save(feed);

    }


    public void deleteFriend(User user, User userFriend) {
        log.info("Пользователь {} удалил друга", user);
        Feed feed = Feed.builder()
                .entityId(user.getId())
                .eventType(FRIEND)
                .operation(DELETE)
                .build();

        feedsStorage.save(feed);
    }

    public void addLike(User user, Film film) {
        log.info("Пользователь {} поставил лайк фильму {}", user, film);
        Feed feed = Feed.builder()
                .eventId(user.getId())
                .eventType(LIKE)
                .operation(ADD)
                .build();

        feedsStorage.save(feed);
    }

    public void deleteLike(User user, Film film) {
        log.info("Пользователь {} удалил лайк у фильма {}", user, film);
        Feed feed = Feed.builder()
                .userId(user.getId())
                .eventType(LIKE)
                .operation(DELETE)
                .entityId(film.getId())
                .build();

        feedsStorage.save(feed);
    }
    public List<Feed> getFeedByUserId(Long userId){
        return feedsStorage.getFeedByUserId(userId);
    }
}
