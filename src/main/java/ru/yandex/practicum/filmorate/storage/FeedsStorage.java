package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Feed;

import java.util.List;

public interface FeedsStorage {


    void save(Feed feed);

    List<Feed> getFeedByUserId(Long userId);


}
