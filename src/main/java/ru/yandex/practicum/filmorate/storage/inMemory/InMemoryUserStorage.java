package ru.yandex.practicum.filmorate.storage.inMemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    protected HashMap<Long, Optional<User>> userStorage = new HashMap<>();

    @Override
    public Optional<User> createUser(Optional<User> user) {
        user.setId(generateNewId());
        userStorage.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> updateUser(Optional<User> user) {
        if (user.getId() == null) {
            log.error("id  не может быть null");
            throw new ValidateException("id  не может быть null");
        } else if (!userStorage.containsKey(user.getId())) {
            log.error("Пользователь с id = {} не найден", user.getId());
            throw new NotFoundException("Пользователь не найден");
        } else {
            userStorage.put(user.getId(), user);
        }
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(userStorage.values());
    }

    @Override
    public User getUserbyId(Long id) {
        return userStorage.get(id);
    }

    public long generateNewId() {
        long maxId = getMaxId();
        long newId = maxId + 1;
        return newId;
    }

    private long getMaxId() {
        long maxId = 0;
        for (long id : userStorage.keySet()) {
            if (id > maxId) {
                maxId = id;
            }
        }
        return maxId;
    }

    @Override
    public List<Friend> getFriendList(Long userId) {
        List<Friend> friendList = new ArrayList<>();
        User user = getUserbyId(userId);
        for (Long friendId : user.getFriends()) {
            User friendUser = getUserbyId(friendId);
            Friend friend = new Friend();
            friend.setUser(friendUser);
            friend.setFriendshipStatus("подтверждённая");
            friendList.add(friend);
        }
        return friendList;
    }
}
