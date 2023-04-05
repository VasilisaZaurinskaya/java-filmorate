package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
public class UserService {
    protected HashMap<Long, User> userStorage = new HashMap<>();

    public User createUser(User user) {
        user.setId(generateNewId());
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        userStorage.put(user.getId(), user);
        return user;
    }

    public User updateUser(User user) {
        if (user.getId() == null || !userStorage.containsKey(user.getId())) {
            log.error("id  не может быть null");
            throw new ValidateException("id  не может быть null");
        } else {
            userStorage.put(user.getId(), user);
        }
        return user;
    }


    public List<User> getAllUsers() {
        return new ArrayList<>(userStorage.values());
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

    public long generateNewId() {
        long maxId = getMaxId();
        long newId = maxId + 1;
        return newId;
    }
}
