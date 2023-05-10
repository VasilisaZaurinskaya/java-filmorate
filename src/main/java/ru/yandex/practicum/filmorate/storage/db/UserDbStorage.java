package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User createUser(User user) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(
                "insert into user(" +
                        "email, " +
                        "login, " +
                        "name, " +
                        "birthday)" +
                        "VALUES ( ?,? , ? ,?)",
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
        return user;
    }

    @Override
    public User updateUser(User user) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(
                "update user set(" +
                        "email, " +
                        "login, " +
                        "name, " +
                        "birthday)" +
                        "VALUES ( ?,? , ? ,?), where id=?",
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select id, email, login, birthday from user");
        ArrayList<User> users = new ArrayList<User>();
        while (userRows.next()) {
            User user = new User();
            user.setId(userRows.getLong("id"));
            user.setEmail(userRows.getString("email"));
            user.setLogin(userRows.getString("login"));
            user.setBirthday(userRows.getDate("birthday").toLocalDate());
            users.add(user);

        }
        return users;

    }

    @Override
    public User getUserbyId(Long id) {

        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select* from user where id = ?", id);

        if (userRows.next()) {

            User user = new User();
            user.setId(userRows.getLong("id"));
            user.setEmail(userRows.getString("email"));
            user.setLogin(userRows.getString("login"));
            user.setBirthday(userRows.getDate("birthday").toLocalDate());

            log.info("Найден пользователь: {} {}", user.getId(), user.getName());

            return user;
        } else {
            log.info("Пользователь с идентификатором {} не найден.", id);
            return null;
        }
    }

    @Override
    public List<Friend> getFriendList(Long userId) {
        String sql = "select* from friends as f left join user as u on f.friends_is = u.user_id where f.user_id=?";
        SqlRowSet friendRows = jdbcTemplate.queryForRowSet(sql, userId);
        ArrayList<Friend> friends = new ArrayList<Friend>();
        while (friendRows.next()) {
            User user = new User();
            user.setId(friendRows.getLong("id"));
            user.setEmail(friendRows.getString("email"));
            user.setLogin(friendRows.getString("login"));
            user.setBirthday(friendRows.getDate("birthday").toLocalDate());

            Friend friend = new Friend();
            friend.setUser(user);
            friend.setFriendshipStatus(friendRows.getString("friendship_status"));

        }
        return friends;

    }
}
