package ru.yandex.practicum.filmorate.inMemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    protected HashMap<Long, User> userStorage = new HashMap<>();

    @Override
    public void createUser(User user) {
        user.setId(generateNewId());
        userStorage.put(user.getId(), user);
    }

    @Override
    public User updateUser(User user) {
        if (user.getId() == null || !userStorage.containsKey(user.getId())) {
            log.error("id  не может быть null");
            throw new ValidateException("id  не может быть null");
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
}
