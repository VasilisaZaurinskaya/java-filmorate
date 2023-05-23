package ru.yandex.practicum.filmorate.storage.db;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@Primary
@AllArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User createUser(User user) {

        Map<String, Object> values = new HashMap<>();
        values.put("name", user.getName());
        values.put("email", user.getEmail());
        values.put("login", user.getLogin());
        values.put("birthday", user.getBirthday());

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");

        Long userId = simpleJdbcInsert.executeAndReturnKey(values).longValue();

        return getUserbyId(userId);
    }

    @Override
    public User updateUser(User user) {
        if (getUserbyId(user.getId()) == null)
            throw new NotFoundException("Пользователь с идентификатором " + user.getId() + " не найден.");
        jdbcTemplate.update(
                "update users set " +
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
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from users");
        ArrayList<User> users = new ArrayList<User>();
        while (userRows.next()) {
            User user = new User();
            user.setId(userRows.getLong("user_id"));
            user.setEmail(userRows.getString("email"));
            user.setName(userRows.getString("name"));
            user.setLogin(userRows.getString("login"));
            user.setBirthday(userRows.getDate("birthday").toLocalDate());
            users.add(user);

        }
        return users;

    }

    @Override
    public User getUserbyId(Long id) {

        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from users where user_id = ?", id);

        if (userRows.next()) {

            User user = new User();
            user.setId(userRows.getLong("user_id"));
            user.setName(userRows.getString("name"));
            user.setEmail(userRows.getString("email"));
            user.setLogin(userRows.getString("login"));
            user.setBirthday(userRows.getDate("birthday").toLocalDate());

            log.info("Найден пользователь: {} {}", user.getId(), user.getName());

            return user;
        } else {
            log.info("Пользователь с идентификатором {} не найден.", id);
            throw new NotFoundException("Пользователь с идентификатором " + id + " не найден.");
        }
    }


    public List<Map<String, Object>> getAllFriends() {
        SqlRowSet friendRows = jdbcTemplate.queryForRowSet("select * from friends order by user_id");
        ArrayList<Map<String, Object>> friends = new ArrayList<>();
        while (friendRows.next()) {
            Map<String, Object> friend = new HashMap<>();
            friend.put("user_id", friendRows.getLong("user_id"));
            friend.put("friend_id", friendRows.getLong("friend_id"));
            friend.put("friendship_status", friendRows.getString("friendship"));
            friends.add(friend);


        }
        return friends;

    }

    @Override
    public List<User> getFriendList(Long userId) {
        String sql = "select * from friends as f left join users as u on f.friend_id = u.user_id where f.user_id = ?";
        SqlRowSet friendRows = jdbcTemplate.queryForRowSet(sql, userId);
        ArrayList<User> friends = new ArrayList<User>();
        while (friendRows.next()) {
            User user = new User();
            user.setId(friendRows.getLong("friend_id"));
            user.setName(friendRows.getString("name"));
            user.setEmail(friendRows.getString("email"));
            user.setLogin(friendRows.getString("login"));
            user.setBirthday(friendRows.getDate("birthday").toLocalDate());

            Friend friend = new Friend();
            friend.setFriend(user);
            friend.setFriendshipStatus(friendRows.getString("friendship"));

            friends.add(user);
        }
        return friends;

    }

    @Override
    public void createFriend(User user, User friend) {

        Map<String, Object> values = new HashMap<>();
        values.put("friend_id", friend.getId());
        values.put("user_id", user.getId());
        values.put("friendship_status", "friend");

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("friends");
        simpleJdbcInsert.execute(values);
    }

    @Override
    public void deleteFriend(Long id, Long friendId) {
        String sql = "delete from friends where friend_id = ? and user_id = ? ";
        jdbcTemplate.update(sql, friendId, id);
    }

    @Override
    public List<User> getMitualFriends(Long id, Long otherId) {
        String sql = "select * from users where user_id in (select f1.friend_id from friends AS f1 inner join friends AS f2 ON f1.friend_id = f2.friend_id where f1.user_id = ? AND f2.user_id = ?);";
        SqlRowSet friendRows = jdbcTemplate.queryForRowSet(sql, id, otherId);
        ArrayList<User> mitualFriends = new ArrayList<User>();
        while (friendRows.next()) {
            User user = new User();
            user.setId(friendRows.getLong("user_id"));
            user.setName(friendRows.getString("name"));
            user.setEmail(friendRows.getString("email"));
            user.setLogin(friendRows.getString("login"));
            user.setBirthday(friendRows.getDate("birthday").toLocalDate());

            Friend friend = new Friend();
            friend.setFriend(user);

            mitualFriends.add(user);
        }
        return mitualFriends;

    }
}
