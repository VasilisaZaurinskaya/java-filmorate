package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User createUser(User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        userStorage.createUser(user);
        return user;
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }


    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public void addFriend(Long id, Long friendId) {

        User user = userStorage.getUserbyId(id);
        User userFriend = userStorage.getUserbyId(friendId);

        if (user == null) {
            log.error("Не найден пользователь с id = {}", id);
            throw new ValidateException("Не найден пользователь с указанным id");
        }
        if (userFriend == null) {
            log.error("Не найден пользователь с id = {}", friendId);
            throw new ValidateException("Не найден пользователь с указанным id");
        }

        user.getFriends().add(friendId);
        userFriend.getFriends().add(id);

        userStorage.updateUser(user);
        userStorage.updateUser(userFriend);

    }

    public void deleteFriend(Long id, Long friendId) {

        User user = userStorage.getUserbyId(id);
        User userFriend = userStorage.getUserbyId(friendId);

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

        userStorage.updateUser(user);
        userStorage.updateUser(userFriend);

    }

    public List<User> getFriendList(Long userId) {
        List<User> friendList = null;
        User user = userStorage.getUserbyId(userId);
        for (Long friendId : user.getFriends()) {
            User friend = userStorage.getUserbyId(friendId);
            friendList.add(friend);
        }
        return friendList;
    }

    public List<User> getMitualFriends(Long id, Long friendId) {

        List<User> sharedFriendsList = null;
        User user = userStorage.getUserbyId(id);
        User friend = userStorage.getUserbyId(friendId);
        for (Long friendForList : user.getFriends()) {
            if (friend.getFriends().contains(friendForList)) {
                User sharedFriend = userStorage.getUserbyId(friendForList);
                sharedFriendsList.add(sharedFriend);
            }

        }
        return sharedFriendsList;
    }

}
