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

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    protected HashMap<Long, User> userStorage = new HashMap<>();

    @Override
    public User createUser(User user) {
        user.setId(generateNewId());
        userStorage.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
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
    public List<User> getFriendList(Long userId) {
        List<User> friendList = new ArrayList<>();
        User user = getUserbyId(userId);
        for (Long friendId : user.getFriends()) {
            User friendUser = getUserbyId(friendId);
            Friend friend = new Friend();
            friend.setFriend(friendUser);
            friend.setFriendshipStatus("подтверждённая");
            friendList.add(friendUser);
        }
        return friendList;
    }

    @Override
    public List<User> getMitualFriends(Long id, Long otherId) {

        List<Long> sharedFriendsIds = new ArrayList<>();
        List<User> sharedFriendsList = new ArrayList<>();

        User user = getUserbyId(id);
        User friend = getUserbyId(otherId);

        sharedFriendsIds.addAll(user.getFriends());
        sharedFriendsIds.retainAll(friend.getFriends());

        for (Long sharedFriendId : sharedFriendsIds) {
            sharedFriendsList.add(getUserbyId(sharedFriendId));
        }
        return sharedFriendsList;
    }

    @Override
    public void createFriend(User user, User friend) {
        throw new UnsupportedOperationException();
    }


    @Override
    public void deleteFriend(Long id, Long friendId) {
        User user = getUserbyId(id);
        User userFriend = getUserbyId(friendId);

        if (user == null) {
            log.error("Не найден пользователь с id = {}", id);
            throw new ValidateException("Не найден пользователь с указанным id");
        }
        if (userFriend == null) {
            log.error("Не найден пользователь с id = {}", friendId);
            throw new ValidateException("Не найден пользователь с указанным id");
        }

        user.getFriends().remove(friendId);
        userFriend.getFriends().remove(id);

        updateUser(user);
        updateUser(userFriend);

    }

}
