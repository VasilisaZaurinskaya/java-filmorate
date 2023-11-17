package user;

import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class UserTestData {

    public static Set<Long> createFriendList() {
        Set<Long> friendList = new HashSet<>();
        friendList.add(1L);
        friendList.add(2L);
        friendList.add(3L);
        return friendList;
    }

    public static Set<Long> createFilmList() {
        Set<Long> filmList = new HashSet<>();
        filmList.add(1L);
        filmList.add(2L);
        filmList.add(3L);
        return filmList;
    }

    public static User getUserOne() {
        return new User(null, "email@example.com", "Login", "Name", LocalDateTime.now().toLocalDate(), createFriendList(), createFilmList());
    }


    public static User getUserTwo() {
        return new User(null, "email12@example.com", "LoginLogin", "User", LocalDateTime.now().minusHours(1L).toLocalDate(), createFriendList(), createFilmList());
    }

    public static User getUser() {
        return new User(null, "usernamee,ail@example.com", "LoginOne", "UserName", LocalDateTime.now().minusHours(7L).toLocalDate(), createFriendList(), createFilmList());
    }

}
