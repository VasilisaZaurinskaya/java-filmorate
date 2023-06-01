package ru.yandex.practicum.filmorate.storage.db;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.*;

@Component
@Slf4j
@Primary
@AllArgsConstructor
public class UserDbStorage implements UserStorage {
    public static final String USERS = "users";
    public static final String FRIENDS = "friends";
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User createUser(User user) {

        Map<String, Object> values = new HashMap<>();
        values.put("name", user.getName());
        values.put("email", user.getEmail());
        values.put("login", user.getLogin());
        values.put("birthday", user.getBirthday());

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(USERS)
                .usingGeneratedKeyColumns("user_id");

        Long userId = simpleJdbcInsert.executeAndReturnKey(values).longValue();

        return getUserbyId(userId);
    }

    @Override
    public User updateUser(User user) {
        if (getUserbyId(user.getId()) == null)
            throw new NotFoundException("Пользователь с идентификатором " + user.getId() + " не найден.");
        jdbcTemplate.update(
                "UPDATE " + USERS + " SET " +
                        "name = ?, login = ?, email = ?, birthday = ? " +
                        "where user_id = ?",
                user.getName(),
                user.getLogin(),
                user.getEmail(),
                user.getBirthday(),
                user.getId()
        );
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM " + USERS);
        ArrayList<User> users = new ArrayList<User>();
        while (userRows.next()) {
            User user = new User();
            user.setId(userRows.getLong("user_id"));
            user.setEmail(userRows.getString("email"));
            user.setName(userRows.getString("name"));
            user.setLogin(userRows.getString("login"));
            user.setBirthday(Objects.requireNonNull(userRows.getDate("birthday")).toLocalDate());
            users.add(user);

        }
        return users;

    }

    @Override
    public User getUserbyId(Long id) {

        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM " + USERS + " WHERE user_id = ?", id);

        if (userRows.next()) {

            User user = new User();
            user.setId(userRows.getLong("user_id"));
            user.setName(userRows.getString("name"));
            user.setEmail(userRows.getString("email"));
            user.setLogin(userRows.getString("login"));
            user.setBirthday(Objects.requireNonNull(userRows.getDate("birthday")).toLocalDate());

            log.info("Найден пользователь: {} {}", user.getId(), user.getName());

            return user;
        } else {
            log.info("Пользователь с идентификатором {} не найден.", id);
            throw new NotFoundException("Пользователь с идентификатором " + id + " не найден.");
        }
    }


    public List<Map<String, Object>> getAllFriends() {
        SqlRowSet friendRows = jdbcTemplate.queryForRowSet("SELECT * FROM " + FRIENDS + " ORDER BY user_id");
        ArrayList<Map<String, Object>> friends = new ArrayList<>();
        while (friendRows.next()) {
            Map<String, Object> friend = new HashMap<>();
            friend.put("user_id", friendRows.getLong("user_id"));
            friend.put("friend_id", friendRows.getLong("friend_id"));
            friend.put(FRIENDS + "hip_status", friendRows.getString(FRIENDS + "hip"));
            friends.add(friend);


        }
        return friends;

    }

    @Override
    public void deleteUser(Long id) {
        String sqlQuery = "DELETE FROM " + USERS + " WHERE user_id = ?";
        jdbcTemplate.update(sqlQuery, id);

    }

    @Override
    public List<User> getFriendList(Long userId) {

        String sql = "SELECT * FROM " + FRIENDS + " AS f LEFT JOIN " + USERS + " AS u ON f.friend_id = u.user_id " +
                "WHERE f.user_id = ?";
        SqlRowSet friendRows = jdbcTemplate.queryForRowSet(sql, userId);
        ArrayList<User> friends = new ArrayList<User>();
        while (friendRows.next()) {
            User user = new User();
            user.setId(friendRows.getLong("friend_id"));
            user.setName(friendRows.getString("name"));
            user.setEmail(friendRows.getString("email"));
            user.setLogin(friendRows.getString("login"));
            user.setBirthday(Objects.requireNonNull(friendRows.getDate("birthday")).toLocalDate());

            Friend friend = new Friend();
            friend.setFriend(user);
            friend.setFriendshipStatus(friendRows.getString(FRIENDS + "hip"));

            friends.add(user);
        }
        return friends;

    }

    @Override
    public void createFriend(User user, User friend) {

        Map<String, Object> values = new HashMap<>();
        values.put("friend_id", friend.getId());
        values.put("user_id", user.getId());
        values.put(FRIENDS + "hip_status", "friend");

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(FRIENDS);
        simpleJdbcInsert.execute(values);
    }

    @Override
    public void deleteFriend(Long id, Long friendId) {
        String sql = "DELETE FROM " + FRIENDS + " WHERE friend_id = ? AND user_id = ? ";
        jdbcTemplate.update(sql, friendId, id);
    }

    @Override
    public List<User> getMutualFriends(Long id, Long otherId) {
        String sql = "SELECT * FROM " + USERS + " WHERE user_id IN (SELECT f1.friend_id FROM " + FRIENDS + " " +
                "AS f1 INNER JOIN " + FRIENDS + " AS f2 ON f1.friend_id = f2.friend_id WHERE f1.user_id = ? " +
                "AND f2.user_id = ?);";
        SqlRowSet friendRows = jdbcTemplate.queryForRowSet(sql, id, otherId);
        ArrayList<User> mutualFriends = new ArrayList<User>();
        while (friendRows.next()) {
            User user = new User();
            user.setId(friendRows.getLong("user_id"));
            user.setName(friendRows.getString("name"));
            user.setEmail(friendRows.getString("email"));
            user.setLogin(friendRows.getString("login"));
            user.setBirthday(Objects.requireNonNull(friendRows.getDate("birthday")).toLocalDate());

            Friend friend = new Friend();
            friend.setFriend(user);

            mutualFriends.add(user);
        }
        return mutualFriends;

    }
}
